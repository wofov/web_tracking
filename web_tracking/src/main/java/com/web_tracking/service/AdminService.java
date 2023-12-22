package com.web_tracking.service;

import com.bellelanco_api.config.security.CipherConfig;
import com.bellelanco_api.config.security.JwtTokenProvider;
import com.bellelanco_api.constants.WebConstants;
import com.bellelanco_api.domain.LoginDTO;
import com.bellelanco_api.domain.PasswordDTO;
import com.bellelanco_api.entity.*;
import com.bellelanco_api.queryDsl.QueryDslRepository;
import com.bellelanco_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private TokenDecoderService tokenDecoderService;

    private final AdminRepository adminRepository;
    private final BellelancoRepository bellelancoRepository;
    private final OrderIdRepository orderIdRepository;
    private final QueryDslRepository queryDsl;

    private final NaverPayRepository naverPayRepository;
    private final NaverPayProductRepository naverPayProductRepository;



    public Map<String,String> Login(LoginDTO dto) {

        Admin admin = adminRepository.findByAdminId(dto.getId());

        CipherConfig config = new CipherConfig();

        if(config.decrypt(dto.getPassword()).equals(config.decrypt(admin.getPassword()))
                && !config.decrypt(dto.getPassword()).equals(WebConstants.ERROR)){

            return Map.of(
                    WebConstants.STATUS,WebConstants.OK,
                    WebConstants.TOKEN,jwtTokenProvider.createToken(
                            admin.getAdminId(),
                            admin.getRole(),
                            admin.getAdminSeq()
                    )
            );

        }

        return WebConstants.MAP_FAIL;

    }

    public Map<String,String> AdminUpdate(PasswordDTO dto,HttpServletRequest req){


        long adminSeq = Long.parseLong(tokenDecoderService.headerdecoder(req,WebConstants.ADMIN_SEQ));

        CipherConfig config = new CipherConfig();

        Admin admin = adminRepository.findById(adminSeq).orElseThrow();

        if(config.decrypt(dto.getPassword()).equals(config.decrypt(admin.getPassword()))){

            admin.setPassword(dto.getNewPassword());
            adminRepository.save(admin);

            return WebConstants.MAP_OK;
        }

        return WebConstants.MAP_FAIL;

    }

    public Map<String,Object> PaymentSite(Pageable pageable,
                                          String site,
                                          String brand,
                                          String payment,
                                          String date,
                                          String naver,
                                          LocalDateTime str,
                                          LocalDateTime end){

        Page<Bellelanco> bellelanco = queryDsl.findByBellelanco(brand,site,payment,pageable,date,naver,str,end);

        for(Bellelanco b : bellelanco.getContent()){

            b.setSignDate(b.getSignDate().plusHours(9));
            if(b.getOrderDate() != null){
                b.setOrderDate(b.getOrderDate().plusHours(9));
            }
        }

        long totalPayment = 0;

        if(payment.equals(WebConstants.Y)){
            for(Bellelanco result : queryDsl.total(brand,site,payment,date,naver,str,end)){
                totalPayment += result.getPayment();
            }
        }

        return Map.of(
                WebConstants.MESSAGE,WebConstants.OK,
                WebConstants.LIST,bellelanco.getContent(),
                "size",bellelanco.getTotalPages(),
                "total",bellelanco.getTotalElements(),
                "payment",totalPayment
        );

    }

    public Map<String,String> AdminDelete(String orderId){

        OrderId id = orderIdRepository.findByorderId(orderId);

        Bellelanco bellelanco = bellelancoRepository.findByIpAddressAndOrderTime(id.getIpAddress(),id.getOrderTime());
        if(bellelanco != null){

            if(bellelanco.getPayment() - id.getPayment() == 0){
                bellelancoRepository.deleteById(bellelanco.getSeq());
            }
            else{
                bellelanco.setPayment(bellelanco.getPayment() - id.getPayment());
                bellelancoRepository.save(bellelanco);
            }
        }

        orderIdRepository.deleteById(id.getOrderIdSeq());

        return WebConstants.MAP_OK;

    }

    public Map<String,Object> AdminDate(LocalDateTime date){

        String orderDate = date.format(DateTimeFormatter.ofPattern(WebConstants.PATTERN_YMD));

        var brand = bellelancoRepository.findDistinctBrandAndOrderTime(orderDate);
        var site = bellelancoRepository.findDistinctConnectSiteOrderTime(orderDate);

        Map<String,Object> result = new HashMap<>();
        for(Object b : brand){
            List<Map<String,Object>> list = new ArrayList<>();

            List<Bellelanco> bellelanco = bellelancoRepository.findByOrderTimeAndPaymentNot(orderDate,0L);

            long totalConnect = WebConstants.ZERO ;
            long totalOrderAmount = WebConstants.ZERO ;

            for(Object s : site){
                long connect = WebConstants.ZERO ;
                long orderAmount = WebConstants.ZERO ;

                for(Bellelanco item : bellelanco){
                    if(item.getConnectSite().toLowerCase().equals(s.toString().toLowerCase())){
                        connect ++ ;
                        totalConnect ++ ;

                        orderAmount += item.getPayment();
                        totalOrderAmount += item.getPayment();
                    }
                }

                if(connect != 0){
                    Map<String,Object> info = new HashMap<>();
                    info.put("site",s);
                    info.put("siteSize",connect);
                    info.put("order",orderAmount);
                    list.add(info);
                }
            }

            list.sort((o1, o2) -> Long.compare((long) o2.get("siteSize"), (long) o1.get("siteSize")));

            Map<String,Object> brandInfo = new HashMap<>();
            brandInfo.put("totalSize",totalConnect);
            brandInfo.put("totalOrder",totalOrderAmount);
            brandInfo.put(WebConstants.LIST,list);

            result.put((String)b,brandInfo);
        }

        return Map.of(WebConstants.LIST,result);


    }

    public List<Bellelanco> excel(String site,
                                  String brand,
                                  String payment,
                                  String date,
                                  LocalDateTime str,
                                  LocalDateTime end){


        List<Bellelanco> bellelanco = queryDsl.findByBellelancoTotal(brand,site,payment,date,str,end);

        for(Bellelanco b : bellelanco){
            b.setSignDate(b.getSignDate().plusHours(9));
            if(b.getOrderDate() != null){
                b.setOrderDate(b.getOrderDate().plusHours(9));
            }
        }
        
        return bellelanco;
    }

    public Object Excel(String site,
                        String brand,
                        String payment,
                        String date,
                        LocalDateTime str,
                        LocalDateTime end){
        List<Bellelanco> bellelancoList = excel(site, brand, payment,date, str, end);

        long totalPayment = 0;
        if(payment.equals(WebConstants.Y)){
            for(Bellelanco b : bellelancoList){
                totalPayment += b.getPayment();
            }
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Payment Data");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"", "IP주소", "접속경로", "결제금액", "상품", "브랜드", "접속일자", "결제일자","소재코드","디바이스","총 결제 금액"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;
        long check = 0;
        for (Bellelanco bellelanco : bellelancoList) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(bellelanco.getSeq());
            dataRow.createCell(1).setCellValue(bellelanco.getIpAddress());
            dataRow.createCell(2).setCellValue(bellelanco.getConnectSite());
            dataRow.createCell(3).setCellValue(bellelanco.getPayment());
            dataRow.createCell(4).setCellValue(bellelanco.getProduct());
            dataRow.createCell(5).setCellValue(bellelanco.getBrand());
            dataRow.createCell(6).setCellValue(bellelanco.getSignDate().toString());
            dataRow.createCell(7).setCellValue(bellelanco.getOrderDate().toString());
            dataRow.createCell(8).setCellValue(bellelanco.getCode());
            dataRow.createCell(9).setCellValue(bellelanco.getDevice());

            if(check==0){
                dataRow.createCell(10).setCellValue(totalPayment);
            }

            check ++;
        }

        ByteArrayResource resource;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            resource = new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payment_data.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);

    }


    public Map<String,Object> SiteList(){

        return Map.of(WebConstants.LIST,bellelancoRepository.findDistinctConnectSite());
    }

    public Map<String,Object> BrandList(){

        return Map.of(WebConstants.LIST,bellelancoRepository.findDistinctBrand());
    }


}
