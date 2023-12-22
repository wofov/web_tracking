package com.web_tracking.service;

import com.bellelanco_api.constants.WebConstants;
import com.bellelanco_api.entity.Bellelanco;
import com.bellelanco_api.entity.OrderId;
import com.bellelanco_api.queryDsl.QueryDslRepository;
import com.bellelanco_api.repository.BellelancoRepository;
import com.bellelanco_api.repository.OrderIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BellelancoService {

    private final BellelancoRepository bellelancoRepository;
    private final OrderIdRepository orderIdRepository;
    private final QueryDslRepository queryDsl;


    public void BellelancoConnectSite(String brand,
                                      String site,
                                      String productNum,
                                      String code,
                                      HttpServletResponse res,
                                      HttpServletRequest req,
                                      Map<String,String> all) throws Exception{

        String result = req.getHeader("User-Agent").toUpperCase();
        String device = "";
        if(result.contains("MOBI")){
            device = "Mobile";
        }else{
            device = "Pc";
        }

        String href= "" ;
        String url = "" ;

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : all.entrySet()) {
            if (sb.length() == 0){
                sb.append("?");
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        switch (brand){
            case "BELLELANCO" -> {
                url = "https://bellelanco.com/product/"+productNum + sb.toString();
                productNum = all.get("product_no");
            }
            case "VINNERTY" -> {
                url = "https://vinnerty.com/skin-skin6/product/"+productNum + sb.toString();
                productNum = all.get("product_no");
            }
            case "AUBEVENI" -> {
                String[] list = req.getServletPath().split("/");
                StringBuilder sb2 = new StringBuilder();
                for (int i = 4; i < list.length; i++) {
                    sb2.append(list[i]);
                    if (i < list.length - 1) {
                        sb2.append("/");
                    }
                }
                url = "https://aubeveni.com/skin-skin4/product/" + sb2.toString();
                productNum = list[4];
            }
        }

        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = req.getRemoteAddr();
        }


        try{
            LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC);
            String signTime = time.plusHours(9).format(DateTimeFormatter.ofPattern(WebConstants.PATTERN_YMD));

            if(queryDsl.findByIp(ip, signTime)==null){
                if(brand.equals("bellelanco")){
                    brand = "BELLELANCO";
                }

                Bellelanco bellelanco = new Bellelanco();
                bellelanco.setCode(code);
                bellelanco.setDevice(device);
                bellelanco.setBrand(brand);
                bellelanco.setConnectSite(site);
                bellelanco.setProduct(productNum);
                bellelanco.setSignTime(signTime);
                bellelanco.setPayment(0);
                bellelanco.setIpAddress(ip);
                bellelancoRepository.save(bellelanco);

            }

            href = "<script> window.location.href='"+url+"';</script>";
        }catch (Exception e){

            switch (brand){
                case "BELLELANCO" -> url = "https://bellelanco1.com/skin-skin21";
                case "VINNERTY" -> url = "https://vinnerty.com/skin-skin6";
                case "AUBEVENI" -> url = "https://aubeveni.com/skin-skin4";
            }

            href = "<script> window.location.href='"+url+"';</script>";
        }

        res.setContentType("text/html; charset=utf-8");
        PrintWriter pr = res.getWriter();
        pr.println(href);
        pr.close();

    }

    public void BellelancoPaymentAfter(String price,
                                       String orderId,
                                       HttpServletRequest req) {

        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = req.getRemoteAddr();
        }

        String cleanNumberString = price.replace(",", "");

        long number = Long.parseLong(cleanNumberString);

        LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC);
        String signTime = time.plusHours(9).format(DateTimeFormatter.ofPattern(WebConstants.PATTERN_YMD));

        Bellelanco bellelanco = queryDsl.findByIp(ip,signTime);

        if(bellelanco!=null){

            bellelanco.setPayment(number + bellelanco.getPayment());
            bellelanco.setOrderDate(time);
            bellelanco.setOrderTime(time.plusHours(9).format(DateTimeFormatter.ofPattern(WebConstants.PATTERN_YMD)));
            bellelancoRepository.save(bellelanco);

            OrderId id = OrderId.builder()
                    .orderId(orderId)
                    .ipAddress(ip)
                    .orderTime(time.plusHours(9).format(DateTimeFormatter.ofPattern(WebConstants.PATTERN_YMD)))
                    .payment(number).build();
            orderIdRepository.save(id);

        }

    }



}
