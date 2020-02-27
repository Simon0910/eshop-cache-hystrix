package com.roncoo.eshop.cache.ha;

import com.roncoo.eshop.cache.ha.http.HttpClientUtils;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CircuitBreakerTest {

    @Test
    public void run() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            String response = HttpClientUtils.sendGetRequest("http://localhost:8081/getProductInfo?productId=1");
            System.out.println("第" + (i + 1) + "次请求，结果为：" + response);
        }
        for (int i = 0; i < 5; i++) {
            String response = HttpClientUtils.sendGetRequest("http://localhost:8081/getProductInfo?productId=-1");
            System.out.println("第" + (i + 1) + "次请求，结果为：" + response);
        }

        // 统计单位，有一个时间窗口的，我们必须要等到那个时间窗口过了以后，才会说，hystrix看一下最近的这个时间窗口
        // 比如说，最近的3秒内，有10条数据，其中异常的数据有没有到一定的比例不会开启断路器
        // 如果到了一定的比例50%，那么才会短路
        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
            String response = HttpClientUtils.sendGetRequest("http://localhost:8081/getProductInfo?productId=1");
            System.out.println("第" + (i + 1) + "次请求，结果为：" + response);
        }


    }

}
