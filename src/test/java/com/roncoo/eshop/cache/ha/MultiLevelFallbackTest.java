package com.roncoo.eshop.cache.ha;

import com.roncoo.eshop.cache.ha.hystrix.command.GetProductInfoCommand3;

public class MultiLevelFallbackTest {

    public static void main(String[] args) throws Exception {
        GetProductInfoCommand3 getProductInfoCommand1 = new GetProductInfoCommand3(-1L);
        System.out.println(getProductInfoCommand1.execute());
        GetProductInfoCommand3 getProductInfoCommand2 = new GetProductInfoCommand3(-2L);
        System.out.println(getProductInfoCommand2.execute());
    }

}
