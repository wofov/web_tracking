package com.web_tracking.queryDsl;

import com.bellelanco_api.entity.Bellelanco;
import com.bellelanco_api.entity.QBellelanco;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class QueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QBellelanco bellelanco = QBellelanco.bellelanco;


    public Bellelanco findByIp(String ipAddress,String signTime){
        return queryFactory
                .selectFrom(bellelanco)
                .where(bellelanco.ipAddress.eq(ipAddress).and(bellelanco.signTime.eq(signTime)))
                .fetchFirst();
    }


    public Page<Bellelanco> findByBellelanco(String brand,
                                             String site,
                                             String payment,
                                             Pageable pageable,
                                             String date,
                                             String naver,
                                             LocalDateTime str,
                                             LocalDateTime end){

        BooleanBuilder whereClause = new BooleanBuilder();

        if (!"".equals(site)) {
            whereClause.and(bellelanco.connectSite.eq(site));
        }

        if (!"".equals(brand)) {
            whereClause.and(bellelanco.brand.eq(brand));
        }

        if (!"".equals(naver)) {
            whereClause.and(bellelanco.naver.eq(naver));
        }

        if (str != null) {
            if (date.equals("order")) {
                whereClause.and(bellelanco.orderDate.between(str, end));
            } else {
                whereClause.and(bellelanco.signDate.between(str, end));
            }
        }

        whereClause.and(
                "Y".equals(payment)
                        ? bellelanco.payment.ne(0L)
                        : bellelanco.payment.eq(0L));

        QueryResults<Bellelanco> page = queryFactory
                .selectFrom(bellelanco)
                .where(whereClause)
                .orderBy(bellelanco.seq.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(page.getResults(),pageable,page.getTotal());
    }

    public List<Bellelanco> total(String brand,
                                             String site,
                                             String payment,
                                             String date,
                                             String naver,
                                             LocalDateTime str,
                                             LocalDateTime end) {

        BooleanBuilder whereClause = new BooleanBuilder();

        if (!"".equals(site)) {
            whereClause.and(bellelanco.connectSite.eq(site));
        }

        if (!"".equals(brand)) {
            whereClause.and(bellelanco.brand.eq(brand));
        }

        if (!"".equals(naver)) {
            whereClause.and(bellelanco.naver.eq(naver));
        }

        if (str != null) {
            if ("order".equals(date)) {
                whereClause.and(bellelanco.orderDate.between(str, end));
            } else {
                whereClause.and(bellelanco.signDate.between(str, end));
            }
        }

        whereClause.and(
                "Y".equals(payment)
                        ? bellelanco.payment.ne(0L)
                        : bellelanco.payment.eq(0L));

        List<Bellelanco> results = queryFactory
                .selectFrom(bellelanco)
                .where(whereClause)
                .orderBy(bellelanco.seq.desc())
                .fetch();

        return results;
    }

    public List<Bellelanco> findByOrderDate(String date,String brand){
        System.out.println(date);
        System.out.println(brand);
        BooleanBuilder where = new BooleanBuilder();
        where.and(bellelanco.orderTime.eq(date));
        where.and(bellelanco.brand.eq("BELLELANCO"));
        where.and(bellelanco.payment.ne(0L));
        return queryFactory
                .selectFrom(bellelanco)
                .where(where)
                .fetch();
    }

    public List<Bellelanco> findByBellelancoTotal(String brand,
                                             String site,
                                             String payment,
                                             String date,
                                             LocalDateTime str,
                                             LocalDateTime end){

        BooleanBuilder whereClause = new BooleanBuilder();

        if (!"".equals(site)) {
            whereClause.and(bellelanco.connectSite.eq(site));
        }

        if (!"".equals(brand)) {
            whereClause.and(bellelanco.brand.eq(brand));
        }

        if (str != null) {
            if (date.equals("order")) {
                whereClause.and(bellelanco.orderDate.between(str, end));
            } else {
                whereClause.and(bellelanco.signDate.between(str, end));
            }
        }

        whereClause.and(
                "Y".equals(payment)
                        ? bellelanco.payment.ne(0L)
                        : bellelanco.payment.eq(0L));

        return queryFactory
                .selectFrom(bellelanco)
                .where(whereClause)
                .orderBy(bellelanco.seq.desc())
                .fetch();
    }





}
