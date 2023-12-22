package com.web_tracking.repository;

import com.bellelanco_api.entity.NaverPay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NaverPayRepository  extends JpaRepository<NaverPay,Long> {

    List<NaverPay> findByClickDateBefore(LocalDateTime clickDate);
}
