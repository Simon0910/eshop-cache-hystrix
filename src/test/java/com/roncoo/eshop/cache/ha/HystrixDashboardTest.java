package com.roncoo.eshop.cache.ha;

import com.roncoo.eshop.cache.ha.http.HttpClientUtils;

public class HystrixDashboardTest {

    public static void main(String[] args) throws Exception {
        // for (int i = 0; i < 100; i++) {
        //     new HystrixDashboardTest.TestThread(i).start();
        // }

        String response = HttpClientUtils.sendGetRequest("http://localhost:8081/getProductInfo?productId=1");
        System.out.println("结果为：" + response);
    }

    private static class TestThread extends Thread {
        private int index;

        public TestThread(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            String response = HttpClientUtils.sendGetRequest("http://localhost:8081/getProductInfo?productId=1");
            System.out.println("第" + (index + 1) + "次请求，结果为：" + response);
        }
    }


}
