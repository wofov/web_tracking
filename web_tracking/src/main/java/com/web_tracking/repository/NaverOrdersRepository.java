package com.web_tracking.repository;

import com.bellelanco_api.entity.NaverOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface NaverOrdersRepository extends JpaRepository<NaverOrders,Long> {

    @Query("SELECT DISTINCT orderId FROM NaverOrders")
    List findDistinctOrderId();

    NaverOrders findByOrderId(String orderId);

    @Transactional
    void deleteByOrderId(String orderId);

    List<NaverOrders> findByOrderDateBefore(LocalDateTime orderDate);

    void deleteByOrderDateBefore(LocalDateTime orderDate);
}
