package com.roncoo.eshop.cache.ha.filter;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * hystrix请求上下文过滤器
 *
 * @author Administrator
 */
@Component
@WebFilter(filterName = "Filter01", urlPatterns = {"/*"})
public class HystrixRequestContextFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.shutdown();
        }
    }

    public void destroy() {

    }

}
