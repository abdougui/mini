package mini.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class CustomAuthorization extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path=request.getServletPath();
        if(path.equals("/api/auth") || path.equals("/api/users/batch") ||  path.equals("/api/users/generate") )
            filterChain.doFilter(request,response);
        else{
            String token=request.getHeader("accessToken");
            if(token!=null ) {
                try {
                    Algorithm algorithm=Algorithm.HMAC256("password".getBytes());
                    JWTVerifier verifier= JWT.require(algorithm).build();
                    DecodedJWT decodedJWT=verifier.verify(token);
                    String username=decodedJWT.getSubject();
                    Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority(decodedJWT.getClaim("roles").toString()));
                    UsernamePasswordAuthenticationToken authenticationToken=
                            new UsernamePasswordAuthenticationToken(username,null,authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request,response);
                }catch (Exception ex){
                    response.setHeader("error",ex.getMessage());
                    new ObjectMapper().writeValue(response.getOutputStream(),new HashMap<String ,String >().put("accessToken",token));
                }

            }else filterChain.doFilter(request,response);

        }
    }
}
