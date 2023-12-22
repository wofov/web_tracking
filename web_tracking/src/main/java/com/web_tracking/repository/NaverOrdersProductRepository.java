package com.web_tracking.repository;

import com.bellelanco_api.entity.NaverOrdersProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NaverOrdersProductRepository extends JpaRepository<NaverOrdersProduct,Long> {

    List<NaverOrdersProduct> findByOrderId(String orderId);

}
