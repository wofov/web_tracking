package com.web_tracking.service;

import com.bellelanco_api.entity.*;
import com.bellelanco_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledService {

    private final BellelancoRepository bellelancoRepository;
    private final NaverOrdersRepository naverOrdersRepository;
    private final NaverOrdersProductRepository naverOrdersProductRepository;
    private final NaverPayRepository naverPayRepository;
    private final NaverPayProductRepository naverPayProductRepository;

    @Scheduled(cron = "00 00 08 * * ?", zone = "Asia/Seoul")
    public void NaverDataDelete(){

        LocalDateTime nowTime = LocalDateTime.now().minusDays(3);

        List<NaverOrders> na = naverOrdersRepository.findByOrderDateBefore(nowTime);
        for(NaverOrders order : na){

            List<NaverOrdersProduct> orderId = naverOrdersProductRepository.findByOrderId(order.getOrderId());
            for(NaverOrdersProduct naverOrdersProduct : orderId){
                naverOrdersProductRepository.deleteById(naverOrdersProduct.getOrderSeq());
            }

            naverOrdersRepository.deleteById(order.getOrderSeq());

        }

        List<NaverPay> np = naverPayRepository.findByClickDateBefore(nowTime);
        for(NaverPay pay : np){

            List<NaverPayProduct> seq = naverPayProductRepository.findByNaverPaySeq(pay.getSeq());
            for(NaverPayProduct naverPayProduct : seq){
                naverPayProductRepository.deleteById(naverPayProduct.getSeq());
            }

            naverPayRepository.deleteById(pay.getSeq());

        }


    }

    @Scheduled(cron = "00 00 07 * * ?", zone = "Asia/Seoul")
    public void ConnectDataDelete(){

        LocalDateTime nowTime = LocalDateTime.now().minusDays(3);

        bellelancoRepository.deleteByPaymentAndSignDateBefore(0L,nowTime);


    }



}
