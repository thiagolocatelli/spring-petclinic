package org.springframework.samples.petclinic.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoggingInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("milliseconds", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        Logger logger = LoggerFactory.getLogger(handler.getClass());

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            logger = LoggerFactory.getLogger(handlerMethod.getBeanType());
        }

        Long milliseconds = (Long) request.getAttribute("milliseconds");

        if (request.getQueryString() != null) {
            logger.info("Request: {}?{} took {} milliseconds to complete", request.getRequestURI(), request.getQueryString(), System.currentTimeMillis() -
                    milliseconds);
        } else {
            logger.info("Request: {} took {} milliseconds to complete", request.getRequestURI(), System.currentTimeMillis() - milliseconds);
        }
    }

}
