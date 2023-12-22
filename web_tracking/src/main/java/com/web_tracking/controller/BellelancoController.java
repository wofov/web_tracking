package com.web_tracking.controller;

import com.bellelanco_api.service.BellelancoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class BellelancoController {

    @Autowired
    private BellelancoService bellelancoService;


    @GetMapping("/{brand}/{site}/{code}/{productNum}/**")
    public void getBellelancoConnectSite(
            @PathVariable String brand,
            @PathVariable String site,
            @PathVariable String productNum,
            @PathVariable String code,
            @RequestParam Map<String,String> all,
            HttpServletResponse res,
            HttpServletRequest req) throws Exception {

        bellelancoService.BellelancoConnectSite(brand,site,productNum,code,res,req,all);
    }

    @PutMapping("/set-price/{price}/{orderId}")
    @ResponseBody
    public void getBellelancoPaymentAfter(@PathVariable String price,
                                          @PathVariable String orderId,
                                          HttpServletRequest req) {

        bellelancoService.BellelancoPaymentAfter(price,orderId,req);

    }


}
