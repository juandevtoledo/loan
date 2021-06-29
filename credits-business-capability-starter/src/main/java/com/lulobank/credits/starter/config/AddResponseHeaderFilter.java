package com.lulobank.credits.starter.config;

import io.vavr.control.Option;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/*"})
public class AddResponseHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        Option.of(request.getHeader("token-type"))
                .filter(headerTypeToken -> headerTypeToken.equals("userToken"))
                .peek(headerTypeToken -> Option.of(request.getHeader("Authorization"))
                        .peek(header -> response.setHeader("nextAccessToken", removeBearer(header))));
        chain.doFilter(request, response);
    }

    private String removeBearer(String header) {
        return header.replace("Bearer ", "");
    }
}
