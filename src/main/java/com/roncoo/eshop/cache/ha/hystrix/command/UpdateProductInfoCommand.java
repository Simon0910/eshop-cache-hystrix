package com.roncoo.eshop.cache.ha.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.roncoo.eshop.cache.ha.model.ProductInfo;

public class UpdateProductInfoCommand extends HystrixCommand<ProductInfo> {

    private Long productId;

    public UpdateProductInfoCommand(Long productId) {
        super(HystrixCommandGroupKey.Factory.asKey("UpdateProductInfoGroup"));
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        GetProductInfoCommand.flushCache(productId);
        return null;
    }
}
