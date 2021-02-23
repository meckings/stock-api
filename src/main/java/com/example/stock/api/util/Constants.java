package com.example.stock.api.util;

public class Constants {
    public static final String API_PREFIX_V1="/api/v1";
    public static final String TOKEN_PREFIX="Bearer ";
    public static final String TOKEN_PREFIX_SMALL ="bearer ";
    public static final String TOKEN_PREFIX_BIG ="BEARER ";

    enum Roles{
        USER,
        ADMIN,
        DEV
    }
}
