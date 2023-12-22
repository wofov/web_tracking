package com.web_tracking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bellelanco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;

    private String ipAddress;
    private String connectSite;
    private long payment;
    private String product;
    private String brand;
    private String code;
    private String device;
    private String naver;

    @CreationTimestamp
    private LocalDateTime signDate;
    private String signTime;
    private LocalDateTime orderDate;
    private String orderTime;






}
