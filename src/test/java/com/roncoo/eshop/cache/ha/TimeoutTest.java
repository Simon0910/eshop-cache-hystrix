package com.roncoo.eshop.cache.ha;

import com.roncoo.eshop.cache.ha.http.HttpClientUtils;

public class TimeoutTest {

    public static void main(String[] args) throws Exception {
        String response = HttpClientUtils.sendGetRequest("http://localhost:8081/getProductInfo?productId=-3");
        System.out.println("结果为：" + response);
    }

}
