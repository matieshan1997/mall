package com.yami.shop.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

@Component
public class GetUserFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String method = httpServletRequest.getMethod();
        String contentType = httpServletRequest.getContentType().toUpperCase();
        if("POST".equals(method) && "APPLICATION/JSON".equals(contentType)) {
            ServletRequest requestWrapper = new RequestWrapper(httpServletRequest);
            String body = HttpHelper.getBodyString(requestWrapper);
            String param = URLDecoder.decode(body,"utf-8");
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> paramMap = mapper.readValue(param, Map.class);

            if(paramMap.get("username")!=null&&paramMap.get("userRole")!=null){
                UserContext.setUserInfo((String) paramMap.get("username"), (String) paramMap.get("userRole"));
            }


            filterChain.doFilter(requestWrapper, servletResponse);
        }else{//get请求直接放行
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
