package com.web_tracking.repository;

import com.bellelanco_api.entity.Bellelanco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface BellelancoRepository extends JpaRepository<Bellelanco,Long> {

    @Query("SELECT DISTINCT connectSite FROM Bellelanco")
    List findDistinctConnectSite();
    @Query("SELECT DISTINCT brand FROM Bellelanco")
    List findDistinctBrand();
    @Query("SELECT DISTINCT brand FROM Bellelanco WHERE orderTime = :date")
    List findDistinctBrandAndOrderTime(String date);
    @Query("SELECT DISTINCT connectSite FROM Bellelanco WHERE orderTime = :date")
    List findDistinctConnectSiteOrderTime(String date);


    Bellelanco findByIpAddressAndOrderTime(String ipAddress,String signTime);

    List<Bellelanco> findByIpAddress(String ipAddress);
    List<Bellelanco> findByOrderTimeAndPaymentNot(String orderTime,long payment);

    List<Bellelanco> findByNaver(String naver);

    @Transactional
    void deleteByPayment(long payment);

    List<Bellelanco> findByPayment(long payment);
    List<Bellelanco> findByPaymentAndSignDateBefore(long payment, LocalDateTime signDate);
    List<Bellelanco> findByIpAddressAndSignDateBefore(String ipAddress,LocalDateTime signDate);

    @Transactional
    void deleteByPaymentAndSignDateBefore(long payment, LocalDateTime signDate);


}
