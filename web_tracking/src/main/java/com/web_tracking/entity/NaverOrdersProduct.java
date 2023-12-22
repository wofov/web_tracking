package com.web_tracking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaverOrdersProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderSeq;

    private String orderId;

    private long totalPrice;
    private String productTitle;
    private int productNumber;
    private int productQnt;
    private long productPrice;

}
