package com.web_tracking.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
@Data
public class NaverPayDTO {
    private String total_price;
    private ArrayList<Map<String,String>> products;
    private LocalDateTime click_date;
}
