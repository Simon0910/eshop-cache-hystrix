package com.roncoo.eshop.cache.ha.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.roncoo.eshop.cache.ha.cache.local.BrandCache;
import com.roncoo.eshop.cache.ha.cache.local.LocationCache;
import com.roncoo.eshop.cache.ha.http.HttpClientUtils;
import com.roncoo.eshop.cache.ha.model.ProductInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 获取商品信息
 *
 * @author Administrator
 */
public class GetProductInfoCommand2 extends HystrixCommand<ProductInfo> {

    public static final HystrixCommandKey KEY = HystrixCommandKey.Factory.asKey("GetProductInfoCommand");

    private Long productId;

    public GetProductInfoCommand2(Long productId) {
        super(HystrixCommandGroupKey.Factory.asKey("ProductInfoServiceGroup"));
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        System.out.println("调用接口，查询商品数据，productId=" + productId);
        if (productId.equals(-1L)) {
            throw new Exception();
        }
        String url = "http://127.0.0.1:8082/getProductInfo?productId=" + productId;
        String response = HttpClientUtils.sendGetRequest(url);
        return JSONObject.parseObject(response, ProductInfo.class);
    }

    @Override
    protected ProductInfo getFallback() {
        ProductInfo productInfo = new ProductInfo();
        // 从请求参数中获取到的唯一条数据
        productInfo.setId(productId);
        // 从本地缓存中获取一些数据
        productInfo.setBrandId(BrandCache.getBrandId(productId));
        productInfo.setBrandName(BrandCache.getBrandName(productInfo.getBrandId()));
        productInfo.setCityId(LocationCache.getCityId(productId));
        productInfo.setCityName(LocationCache.getCityName(productInfo.getCityId()));
        // 手动填充一些默认的数据
        productInfo.setColor("默认颜色");
        productInfo.setModifiedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        productInfo.setName("默认商品");
        productInfo.setPictureList("default.jpg");
        productInfo.setPrice(0.0);
        productInfo.setService("默认售后服务");
        productInfo.setShopId(-1L);
        productInfo.setSize("默认大小");
        productInfo.setSpecification("默认规格");
        return productInfo;
    }

}
