package com.roncoo.eshop.cache.ha.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.roncoo.eshop.cache.ha.degrade.IsDegrade;
import com.roncoo.eshop.cache.ha.model.ProductInfo;

public class GetProductInfoFacadeCommand extends HystrixCommand<ProductInfo> {

    private Long productId;

    public GetProductInfoFacadeCommand(Long productId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetProductInfoFacadeCommand"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(5000)
                        .withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(15))
        );
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        if (!IsDegrade.isDegrade()) {
            return new GetProductInfoCommand4(productId).execute();
        } else {
            return new GetProductInfoFromMySQLCommand(productId).execute();
        }
    }

    // @Override
    // protected ProductInfo getFallback() {
    //     ProductInfo productInfo = new ProductInfo();
    //     productInfo.setName("降级了");
    //     return productInfo;
    // }

}
