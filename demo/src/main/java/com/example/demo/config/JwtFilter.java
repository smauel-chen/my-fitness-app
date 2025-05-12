// package com.example.demo.config;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// import org.springframework.web.filter.OncePerRequestFilter;


// import java.io.IOException;

// public class JwtFilter extends OncePerRequestFilter {


//     @Override
//     protected void doFilterInternal(
//         HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//             throws ServletException, IOException {
        
//         String path = request.getRequestURI();
//         if(path.startsWith("/login")||path.startsWith("/user")){
//             filterChain.doFilter(request, response);
//             return;
//         }
        
//         String authHeader = request.getHeader("Authorization");

//         // 檢查 token 格式
//         if (authHeader != null && authHeader.startsWith("Bearer ")) {
//             String token = authHeader.substring(7); // 拿掉 Bearer 開頭

//             String username = JwtUtil.validateToken(token).getAudience();
//             if (username != null) {
//                 // ✅ 通過驗證 → 放行
//                 filterChain.doFilter(request, response);
//                 return;
//             }
//         }

//         // ❌ 沒通過 → 回傳 401
//         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//         response.getWriter().write("Unauthorized");
//         return;
//     }
// }
