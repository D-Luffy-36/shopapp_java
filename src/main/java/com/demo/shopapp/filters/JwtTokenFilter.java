package com.demo.shopapp.filters;

import com.demo.shopapp.components.JwtTokenUtils;
import com.demo.shopapp.entities.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;


    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try{
            if (isByPassToken(request)) {
                // cho đi qua custom filter change
                filterChain.doFilter(request, response);
                return;
            }
            // lấy giá trị của header HTTP với tên "Authorization" từ requestfinal String authHeader = request.getHeader("Authorization");
            final String authHeader = request.getHeader("Authorization");
            final String token = authHeader.substring(7);
            final String identifier = jwtTokenUtils.extractIdentifier(token); // Lấy email hoặc phone từ token

            if(authHeader == null && !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }

            if (identifier != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(identifier);
                if(jwtTokenUtils.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response); //enable bypas
        // lỗi Sql có thể ở đây, căt bearer nhưng token bị null
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Đặt mã trạng thái 401
            response.getWriter().write("Unauthorized: " + e.getMessage()); // Gửi thông báo lỗi chi tiết
            response.getWriter().flush();
        }
    }


    private boolean isByPassToken(@NonNull HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        final List<Pair<String, String>> byPassTokens = List.of(
                Pair.of(apiPrefix + "/products", "GET"),
                Pair.of(apiPrefix + "/categories", "GET"),
                Pair.of(apiPrefix + "/roles", "GET"),
                Pair.of(apiPrefix + "/images/", "GET"),
                Pair.of(apiPrefix + "/users/login", "POST"),
                Pair.of(apiPrefix + "/users/register", "POST"),
                Pair.of(apiPrefix + "/users/login/social-login", "GET"),
                Pair.of(apiPrefix + "/users/login/social/callback", "GET")
        );

        for (Pair<String, String> byPassToken : byPassTokens) {
            if (requestURI.equals(byPassToken.getKey()) && method.equalsIgnoreCase(byPassToken.getValue())) {
                System.out.println("Bypassing token for: " + requestURI);
                return true;
            }

            if (requestURI.startsWith(byPassToken.getKey()) && method.equalsIgnoreCase(byPassToken.getValue())) {
                System.out.println("Bypassing token for prefix: " + requestURI);
                return true;
            }
        }

        System.out.println("JWT Required for: " + requestURI);
        return false;
    }


    // function này có lỗi ngu
//    private boolean isByPassToken(@NonNull HttpServletRequest request) {
//
//        String requestURI = request.getRequestURI();
//        String method = request.getMethod();
//
//        final List<Pair<String, String>> byPassTokens = List.of(
//                Pair.of(apiPrefix + "/products", "GET"),
//                Pair.of(apiPrefix + "/categories", "GET"),
//                Pair.of(apiPrefix + "/roles", "GET"),
//                Pair.of(apiPrefix + "/images/", "GET"),
//                Pair.of(apiPrefix + "/users/login", "POST"),
//                Pair.of(apiPrefix + "/users/register", "POST")
//        );
//
//        for (Pair<String, String> byPassToken : byPassTokens) {
//            String path = request.getServletPath();
//            String httpMethod = request.getMethod();
//
//            if(request.getServletPath().contains(byPassToken.getKey())  &&
//                    request.getMethod().equals(byPassToken.getValue())  ){
//               return true;
//            }
//
//            // Nếu request bắt đầu bằng "/images/", cho phép truy cập công khai
//            if (requestURI.startsWith(path)  && method.equalsIgnoreCase(httpMethod)) {
//                return true;
//            }
//
//        }
//        return false;
//    }

}
