package com.web_tracking.domain;

import lombok.Data;

@Data
public class SiteDTO {
    long seq;
    String orderDate;
    String ip;
    String site;
    long price;

}
