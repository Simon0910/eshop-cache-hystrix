package com.roncoo.eshop.cache.ha.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.roncoo.eshop.cache.ha.http.HttpClientUtils;
import com.roncoo.eshop.cache.ha.model.ProductInfo;

/**
 * 获取商品信息
 *
 * @author Administrator
 */
public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {

    public static final HystrixCommandKey KEY = HystrixCommandKey.Factory.asKey("GetProductInfoCommand");

    private Long productId;

    public GetProductInfoCommand(Long productId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"))
                .andCommandKey(KEY)
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetProductInfoPool"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(10) // 线程核心数
                        // .withMaximumSize(20) // 允许最大线程核心数
                        // .withAllowMaximumSizeToDivergeFromCoreSize(true) // 开启允许最大线程核心数
                        // .withKeepAliveTimeMinutes(1) // 扩容的线程空闲1分钟释放调
                        .withMaxQueueSize(12) // 线程池满后, 队列的大小
                        .withQueueSizeRejectionThreshold(15)) //  队列的大小 = MaxQueueSize < QueueSizeRejectionThreshold ? MaxQueueSize : QueueSizeRejectionThreshold
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerRequestVolumeThreshold(30) // 一个统计窗口内30个请求才统计 决定是否短路
                        .withCircuitBreakerErrorThresholdPercentage(40) // 一个统计窗口内40%是error了 就短路
                        .withCircuitBreakerSleepWindowInMilliseconds(3000) // 多长时间回复正常
                        // timeout也设置大一些，否则如果请求放等待队列中时间太长了，直接就会timeout，等不到去线程池里执行了
                        // .withExecutionTimeoutInMilliseconds(20000)
                        // .withExecutionTimeoutInMilliseconds(200)
                        .withExecutionTimeoutInMilliseconds(3000)
                        // fallback，sempahore限流，30个，避免太多的请求同时调用fallback被拒绝访问
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(30))
        );
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        System.out.println("调用接口，查询商品数据，productId=" + productId);

        if (productId.equals(-1L)) {
            throw new Exception();
        }

        if (productId.equals(-2)) {
            Thread.sleep(3000);
        }

        String url = "http://127.0.0.1:8082/getProductInfo?productId=" + productId;
        String response = HttpClientUtils.sendGetRequest(url);
        System.out.println(("GetProductInfoCommand"));
        return JSONObject.parseObject(response, ProductInfo.class);
    }

    @Override
    protected String getCacheKey() {
        return "product_info_" + productId;
    }


    @Override
    protected ProductInfo getFallback() {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setName("降级商品");
        return productInfo;
    }

    public static void flushCache(Long productId) {
        HystrixRequestCache.getInstance(KEY, HystrixConcurrencyStrategyDefault.getInstance())
                .clear("product_info_" + productId);
    }

}
