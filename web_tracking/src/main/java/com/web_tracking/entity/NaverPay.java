package com.web_tracking.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class NaverPay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;

    private String ip;
    private String totalPrice;
    private LocalDateTime clickDate;

}
