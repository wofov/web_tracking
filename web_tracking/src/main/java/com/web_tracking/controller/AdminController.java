package com.web_tracking.controller;

import com.bellelanco_api.domain.LoginDTO;
import com.bellelanco_api.domain.PasswordDTO;
import com.bellelanco_api.service.AdminService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AdminController {

    @Autowired
    private AdminService adminService;



    @PostMapping("/admin/login")
    public ResponseEntity<Map<String,String>> getLogin(@RequestBody LoginDTO dto) {
        return ResponseEntity.ok(adminService.Login(dto));
    }

    @PutMapping("/admin")
    public ResponseEntity<Map<String,String>> getAdminUpdate(@RequestBody PasswordDTO dto,
                                                             HttpServletRequest req){
        return ResponseEntity.ok(adminService.AdminUpdate(dto,req));
    }

    @GetMapping("/admin")
    public ResponseEntity<Map<String,Object>> getPaymentSite(
            @PageableDefault(size = 20,direction = Sort.Direction.DESC) Pageable pageRequest,
            @RequestParam(required = false,defaultValue = "") String site,
            @RequestParam(required = false,defaultValue = "") String payment,
            @RequestParam(required = false,defaultValue = "") String brand,
            @RequestParam(required = false,defaultValue = "") String date,
            @RequestParam(required = false,defaultValue = "") String naver,
            @RequestParam(required = false,defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime str,
            @RequestParam(required = false,defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ){
        return ResponseEntity.ok(adminService.PaymentSite(pageRequest,site,brand,payment,date,naver,str,end));
    }

    @DeleteMapping("/admin/{orderId}")
    public ResponseEntity<Map<String,String>> getAdminDelete(@PathVariable String orderId){
        return ResponseEntity.ok(adminService.AdminDelete(orderId));
    }

    @GetMapping("/admin/date")
    public ResponseEntity<Map<String,Object>> getAdminDate(
            @RequestParam(required = false,defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date
    ){
        return ResponseEntity.ok(adminService.AdminDate(date));
    }

    @GetMapping("/admin/download")
    public ResponseEntity<Object> getExcel(
            @RequestParam(required = false,defaultValue = "") String site,
            @RequestParam(required = false,defaultValue = "") String payment,
            @RequestParam(required = false,defaultValue = "") String brand,
            @RequestParam(required = false,defaultValue = "") String date,
            @RequestParam(required = false,defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime str,
            @RequestParam(required = false,defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ){

        return ResponseEntity.ok(adminService.Excel(site, brand, payment,date, str, end));
    }


    @GetMapping("/admin/site")
    public ResponseEntity<Map<String,Object>> getSiteList(){
        return ResponseEntity.ok(adminService.SiteList());
    }

    @GetMapping("/admin/brand")
    public ResponseEntity<Map<String,Object>> getBrandList(){
        return ResponseEntity.ok(adminService.BrandList());
    }







}
