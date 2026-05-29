/*-------------------------------------------------------------------------
 * RequestLoggingFilter
 * -------------------------------------
 * DEVELOPMENT FILE: This file logs request methods and responses.
 *
 * -------------------------------------------------------------------------
 * */
package com.example.tts_in_spring.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.printf("➡️  %s %s%n", req.getMethod(), req.getRequestURI());

        chain.doFilter(request, response);
    }
}