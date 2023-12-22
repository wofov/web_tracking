package com.web_tracking.constants;

import java.util.Map;

public class WebConstants {

    //jwt
    public static final String AUTHORIZATION = "Authorization";
    public static final String JWTDECODER = "\\.";
    public static final String EXPIRE_TOKEN = "expire token";

    //map
    public static final Map<String,String> MAP_OK = Map.of("status","ok");
    public static final Map<String,String> MAP_FAIL = Map.of("status","fail");
    public static final String STATUS = "Status";
    public static final String MESSAGE = "Message";
    public static final String FAIL = "Fail";
    public static final String OK = "Ok";
    public static final String Y = "Y";
    public static final String LIST = "list";

    //etc
    public static final String TOKEN = "token";
    public static final String ERROR = "error";
    public static final String ROLE = "role";
    public static final String ADMIN_SEQ = "adminSeq";
    public static final String GAP = "";
    public static final String WILDCARD = "*";
    public static final String D_WILDCARD = "/**";
    public static final String NON_USER = " non user";

    public static final String UNKNOWN_ERROR = "Unknown Error";

    public static final long ZERO = 0;
    public static final String PATTERN_YMD = "yyyy/MM/dd";


}
