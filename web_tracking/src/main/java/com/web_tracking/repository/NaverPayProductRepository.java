package com.web_tracking.repository;


import com.bellelanco_api.entity.NaverPayProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NaverPayProductRepository  extends JpaRepository<NaverPayProduct,Long> {

    List<NaverPayProduct> findByNaverPaySeq(long seq);
}
