package com.web_tracking.service;

import com.bellelanco_api.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenDecoderService {
    private final JwtTokenProvider jwtTokenProvider;

    public String headerdecoder(HttpServletRequest req, String info){
        String headerJWT = jwtTokenProvider.resolveToken(req);

        String result = "";
        try{
            if(!StringUtils.isEmpty(headerJWT)) {
                String payload = headerJWT.split("\\.")[1];
                Base64.Decoder decoder = Base64.getUrlDecoder();
                decoder.decode(payload);
                BasicJsonParser jsonParser = new BasicJsonParser();
                Map<String, Object> jsonArray = jsonParser.parseMap(new String(decoder.decode(payload)));
                result = jsonArray.get(info).toString();
            }
            return result;
        }catch (Exception e){
            return result;
        }

    }


}
