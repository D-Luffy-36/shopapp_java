package com.demo.shopapp.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
public class WebUtils {
    // trả về request hiện tại
    public static HttpServletRequest getRequest() {
        return  ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
