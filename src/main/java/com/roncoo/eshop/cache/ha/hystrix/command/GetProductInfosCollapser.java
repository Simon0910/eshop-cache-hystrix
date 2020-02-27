package com.roncoo.eshop.cache.ha.hystrix.command;

import com.alibaba.fastjson.JSONArray;
import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.roncoo.eshop.cache.ha.http.HttpClientUtils;
import com.roncoo.eshop.cache.ha.model.ProductInfo;

import java.util.Collection;
import java.util.List;

public class GetProductInfosCollapser extends HystrixCollapser<List<ProductInfo>, ProductInfo, Long> {

    private Long productId;

    public GetProductInfosCollapser(Long productId) {
        // super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("GetProductInfosCollapser"))
        //         .andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter()
        //                 // 控制一个Batch中最多允许多少个request被合并，然后才会触发一个batch的执行
        //                 // 默认值是无限大，就是不依靠这个数量来触发执行，而是依靠时间
        //                 .withMaxRequestsInBatch(100)
        //                 // 控制一个batch创建之后，多长时间以后就自动触发batch的执行，默认是10毫秒
        //                 .withTimerDelayInMilliseconds(20)));
        this.productId = productId;
    }

    @Override
    public Long getRequestArgument() {
        return productId;
    }

    @Override
    protected HystrixCommand<List<ProductInfo>> createCommand(
            Collection<com.netflix.hystrix.HystrixCollapser.CollapsedRequest<ProductInfo, Long>> requests) {
        StringBuilder paramsBuilder = new StringBuilder("");
        for (CollapsedRequest<ProductInfo, Long> request : requests) {
            paramsBuilder.append(request.getArgument()).append(",");
        }
        String params = paramsBuilder.toString();
        params = params.substring(0, params.length() - 1);

        System.out.println("createCommand方法执行，params=" + params);

        return new BatchCommand(requests);
    }

    @Override
    protected void mapResponseToRequests(
            List<ProductInfo> batchResponse,
            Collection<com.netflix.hystrix.HystrixCollapser.CollapsedRequest<ProductInfo, Long>> requests) {
        int count = 0;
        for (CollapsedRequest<ProductInfo, Long> request : requests) {
            request.setResponse(batchResponse.get(count++));
        }
        System.out.println("count = " + count);
    }

    @Override
    protected String getCacheKey() {
        System.out.println("getCacheKey = " + productId);
        return "product_info_" + productId;
    }

    private static final class BatchCommand extends HystrixCommand<List<ProductInfo>> {

        public final Collection<CollapsedRequest<ProductInfo, Long>> requests;

        public BatchCommand(Collection<CollapsedRequest<ProductInfo, Long>> requests) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("GetProductInfosCollapserBatchCommand")));
            this.requests = requests;
        }

        @Override
        protected List<ProductInfo> run() {
            // 将一个批次内的商品id给拼接在了一起
            StringBuilder paramsBuilder = new StringBuilder("");
            for (CollapsedRequest<ProductInfo, Long> request : requests) {
                paramsBuilder.append(request.getArgument()).append(",");
            }
            String params = paramsBuilder.toString();
            params = params.substring(0, params.length() - 1);

            // 在这里，我们可以做到什么呢，将多个商品id合并在一个batch内，直接发送一次网络请求，获取到所有的结果

            String url = "http://localhost:8082/getProductInfos?productIds=" + params;
            String response = HttpClientUtils.sendGetRequest(url);

            List<ProductInfo> productInfos = JSONArray.parseArray(response, ProductInfo.class);
            for (ProductInfo productInfo : productInfos) {
                System.out.println("BatchCommand内部，productInfo=" + productInfo);
            }

            return productInfos;
        }

    }

}
