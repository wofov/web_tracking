package com.web_tracking.controller;

import com.bellelanco_api.repository.NaverOrdersProductRepository;
import com.bellelanco_api.repository.NaverOrdersRepository;
import com.bellelanco_api.service.Cafe24Service;
import okhttp3.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class Cafe24Controller {


    private final NaverOrdersRepository naverOrdersRepository;
    private final NaverOrdersProductRepository naverOrdersProductRepository;
    private final Cafe24Service cafe24Service;

    public Cafe24Controller(NaverOrdersRepository naverOrdersRepository,
                            NaverOrdersProductRepository naverOrdersProductRepository,
                            Cafe24Service cafe24Service) {
        this.naverOrdersRepository = naverOrdersRepository;
        this.naverOrdersProductRepository = naverOrdersProductRepository;
        this.cafe24Service = cafe24Service;
    }

    @GetMapping("/bellelanco")
    public void ResponseCode(@RequestParam String code,
                             HttpServletResponse res) throws Exception{
        cafe24Service.getCafe24Token(code,res);
    }

    @GetMapping("/naver/orders")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> naverOrders(
            @RequestParam(required = false,defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime str){
        return ResponseEntity.ok(cafe24Service.naverOrders(str));
    }


    @GetMapping("/naver/orders/set")
    @ResponseBody
    public Map<String,Object> getNaverOrderSet(){
        return cafe24Service.Set();
    }







}
