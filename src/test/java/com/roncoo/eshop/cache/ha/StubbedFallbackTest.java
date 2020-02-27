package com.roncoo.eshop.cache.ha;

import com.roncoo.eshop.cache.ha.hystrix.command.GetProductInfoCommand2;

public class StubbedFallbackTest {

    public static void main(String[] args) {
        GetProductInfoCommand2 getProductInfoCommand2 = new GetProductInfoCommand2(-1L);
        System.out.println(getProductInfoCommand2.execute());
    }

}
