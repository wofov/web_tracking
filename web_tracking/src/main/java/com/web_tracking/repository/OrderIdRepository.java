package com.web_tracking.repository;

import com.bellelanco_api.entity.OrderId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderIdRepository extends JpaRepository<OrderId,Long> {


    OrderId findByorderId(String orderId);
}
