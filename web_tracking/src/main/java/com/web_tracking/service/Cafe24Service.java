package com.web_tracking.service;

import com.bellelanco_api.constants.WebConstants;
import com.bellelanco_api.entity.*;
import com.bellelanco_api.repository.*;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Cafe24Service {

    private final TokenRepository tokenRepository;
    private final NaverOrdersRepository naverOrdersRepository;
    private final NaverOrdersProductRepository naverOrdersProductRepository;
    private final NaverPayRepository naverPayRepository;
    private final NaverPayProductRepository naverPayProductRepository;
    private final BellelancoRepository bellelancoRepository;
    private final OrderIdRepository orderIdRepository;

    public void getCafe24Token(String code, HttpServletResponse res) throws Exception{

        res.setContentType("text/html; charset=utf-8");
        PrintWriter pr = res.getWriter();

        String base64 = java.util.Base64.getEncoder().encodeToString((WebConstants.GAP).getBytes());

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("bellelanco1.cafe24.com")
                .addPathSegment("api")
                .addPathSegment("v2")
                .addPathSegment("oauth")
                .addPathSegment("token")
                .build();
        OkHttpClient client = new OkHttpClient();

        okhttp3.RequestBody formBody = new FormBody.Builder()
                .add("grant_type","authorization_code")
                .add("code",code)
                .add("redirect_uri","https://ads.hailcosmetic.shop/api/bellelanco")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization","Basic "+base64)
                .header("Content-Type","application/x-www-form-urlencoded")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        if(response.isSuccessful()){

            String body = response.body() != null ? response.body().string() : null;
            JSONObject obj = new JSONObject(body);

            String token = (String) obj.get("access_token");

            okState(token);

            pr.print("<html><script>;window.location.href='https://admin.hailcosmetic.shop';</script><body></body></html>");//메인
            pr.close();

        }

    }
    public Map<String,Object> naverOrders(LocalDateTime str){

        try{
            if(str == null){
                return Map.of(
                        WebConstants.STATUS,WebConstants.FAIL
                );
            }

            List<NaverOrders> na = naverOrdersRepository.findAll();

            String str_date = "";

            if(na.size()!=0){
                str_date = na.get(na.size()-1).getOrderDate().format(DateTimeFormatter.ofPattern(WebConstants.PATTERN_YMD));
            }else{
                str_date = str.format(DateTimeFormatter.ofPattern(WebConstants.PATTERN_YMD));
            }

            String end_date = str.format(DateTimeFormatter.ofPattern(WebConstants.PATTERN_YMD));

            HttpUrl orderurl = new HttpUrl.Builder()
                    .scheme("https")
                    .host("bellelanco1.cafe24api.com")
                    .addPathSegment("api")
                    .addPathSegment("v2")
                    .addPathSegment("admin")
                    .addPathSegment("orders")
                    .addQueryParameter("start_date", str_date)
                    .addQueryParameter("end_date", end_date)
                    .addQueryParameter("limit", "400")
                    .addQueryParameter("embed","items")
                    .build();
            OkHttpClient orderclient = new OkHttpClient();

            Request orderrequest = new Request.Builder()
                    .url(orderurl)
                    .header("Authorization", "Bearer " + currentToken())
                    .header("Content-Type", "application/json")
                    .header("X-Cafe24-Api-Version", "2023-09-01")
                    .get()
                    .build();

            Response orderres = orderclient.newCall(orderrequest).execute();

            if (orderres.isSuccessful()) {

                String orderbody = orderres.body() != null ? orderres.body().string() : null;
                JSONObject orderobj = new JSONObject(orderbody);
                JSONArray result = orderobj.getJSONArray("orders");

                int size = result.toList().size();
                List<Map<String,Object>> list = new ArrayList<>();

                int check = 0 ;

                while (size > 0){

                    if(result.getJSONObject(size-1).get("order_place_name").equals("네이버 페이")){

                        Map<String,Object> re = new HashMap<>();
                        re.put("total_price",result.getJSONObject(size-1).getJSONObject("initial_order_amount").get("order_price_amount"));

                        JSONArray detail = result.getJSONObject(size-1).getJSONArray("items");
                        String orderId =(String) result.getJSONObject(size-1).get("order_id");
                        String orderDate = (String)result.getJSONObject(size-1).get("order_date");

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
                        LocalDateTime localDateTime = LocalDateTime.parse(orderDate, formatter);

                        int detailSize = detail.toList().size();
                        List<Map<String,Object>> itemList = new ArrayList<>();

                        String long1 = (String)result.getJSONObject(size-1).getJSONObject("initial_order_amount").get("order_price_amount");
                        String[] part = long1.split("\\.");

                        NaverOrders ck = naverOrdersRepository.findByOrderId(orderId);

                        check = 0;

                        if(ck==null){
                            NaverOrders naverOrders = NaverOrders.builder()
                                    .orderId(orderId)
                                    .totalPrice(Long.parseLong(part[0]))
                                    .orderDate(localDateTime)
                                    .build();
                            naverOrdersRepository.save(naverOrders);
                            check = 1 ;
                        }

                        while(detailSize > 0){

                            String long2 = (String)detail.getJSONObject(detailSize-1).get("product_price");
                            String[] part2 = long2.split("\\.");

                            if(check==1){

                                NaverOrdersProduct product = NaverOrdersProduct.builder()
                                        .orderId(orderId)
                                        .totalPrice(Long.parseLong(part[0]))
                                        .productTitle((String)detail.getJSONObject(detailSize-1).get("product_name_default"))
                                        .productNumber(0)
                                        .productQnt((int)detail.getJSONObject(detailSize-1).get("quantity"))
                                        .productPrice(Long.parseLong(part2[0]))
                                        .build();
                                naverOrdersProductRepository.save(product);

                            }

                            Map<String,Object> it = new HashMap<>();
                            it.put("product_title",detail.getJSONObject(detailSize-1).get("product_name_default"));
                            it.put("product_number",detail.getJSONObject(detailSize-1).get("product_no"));
                            it.put("product_qnt",detail.getJSONObject(detailSize-1).get("quantity"));
                            it.put("product_price",detail.getJSONObject(detailSize-1).get("product_price"));
                            itemList.add(it);

                            detailSize--;
                        }
                        re.put("item",itemList);
                        list.add(re);

                    }
                    size --;
                }

                return Map.of(
                        WebConstants.STATUS, WebConstants.OK
                );
            }
            else{
                return Map.of(
                        WebConstants.STATUS,WebConstants.FAIL
                );
            }
        }catch (Exception e){
            return Map.of(
                    WebConstants.STATUS,WebConstants.FAIL
            );
        }
    }

    public void okState(String token){

        Token token1 = Token.builder()
                .type("code")
                .state(token).build();
        tokenRepository.save(token1);

    }

    public String currentToken(){
        List<Token> list = tokenRepository.findAll();
        return list.get(list.size()-1).getState();
    }


    public Map<String,Object> Set(){

        try{
            List<NaverPay> pay = naverPayRepository.findAll();
            List<Long> paySeq = new ArrayList<>();

            List<NaverOrders> order = naverOrdersRepository.findAll();
            List<String> orderId = new ArrayList<>();

            for(NaverPay np : pay){

                for(NaverOrders no : order){

                    if(np.getTotalPrice().equals(String.valueOf(no.getTotalPrice()))){

                        List<NaverPayProduct> npp = naverPayProductRepository.findByNaverPaySeq(np.getSeq());

                        List<NaverOrdersProduct> nop = naverOrdersProductRepository.findByOrderId(no.getOrderId());

                        if(npp.size() == nop.size()){

                            int w = 0 ;
                            int size = 0 ;

                            while(w<npp.size()){

                                if(npp.get(w).getProductTitle().equals(nop.get(w).getProductTitle())){
                                    size ++ ;
                                }

                                w ++ ;
                            }

                            int ipCheck = 0 ;

                            if(w == size){

                                List<Bellelanco> ipList = bellelancoRepository.findByIpAddress(np.getIp());

                                if(ipList.size()!=0){

                                    for(Bellelanco bellelanco : ipList){

                                        if(bellelanco.getPayment()==0 && ipCheck==0){

                                            Bellelanco b = bellelancoRepository.findById(bellelanco.getSeq()).orElseThrow();
                                            b.setOrderDate(b.getSignDate().plusMinutes(1));
                                            b.setPayment(no.getTotalPrice());
                                            b.setOrderTime(b.getSignDate().plusMinutes(1).format(DateTimeFormatter.ofPattern(WebConstants.PATTERN_YMD)));
                                            b.setNaver(WebConstants.Y);
                                            bellelancoRepository.save(b);

                                            OrderId o = OrderId.builder()
                                                    .ipAddress(b.getIpAddress())
                                                    .orderId(no.getOrderId())
                                                    .payment(no.getTotalPrice())
                                                    .orderTime(b.getSignDate().plusMinutes(1).format(DateTimeFormatter.ofPattern(WebConstants.PATTERN_YMD)))
                                                    .build();
                                            orderIdRepository.save(o);

                                            ipCheck ++ ;

                                            paySeq.add(np.getSeq());
                                            orderId.add(no.getOrderId());

                                        }

                                    }

                                }

                            }

                        }

                    }

                }

            }
            seqDelete(paySeq);
            orderIdDelete(orderId);

            return Map.of(WebConstants.STATUS,WebConstants.OK);
        }catch (Exception e){
            return Map.of(
                    WebConstants.STATUS,WebConstants.FAIL
            );
        }
    }

    public void seqDelete(List<Long> seq){
        for(Long delete : seq){
            naverPayRepository.deleteById(delete);

            List<NaverPayProduct> naverPayProduct = naverPayProductRepository.findByNaverPaySeq(delete);
            for(NaverPayProduct n : naverPayProduct){
                naverPayProductRepository.deleteById(n.getSeq());
            }
        }
    }
    public void orderIdDelete(List<String> orderId){
        for(String delete : orderId){
            naverOrdersRepository.deleteByOrderId(delete);

            List<NaverOrdersProduct> naverOrdersProducts = naverOrdersProductRepository.findByOrderId(delete);
            for(NaverOrdersProduct n : naverOrdersProducts){
                naverOrdersProductRepository.deleteById(n.getOrderSeq());
            }

        }

    }



}
