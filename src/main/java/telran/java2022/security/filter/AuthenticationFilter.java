package telran.java2022.security.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.java2022.accounting.dao.UserAccountRepository;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {
    final UserAccountRepository userAccountRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {



        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (checkEndPoint(request.getMethod(), request.getServletPath())){
            String token = request.getHeader("Authorization");
            if (token == null){
                response.sendError(401 );
                return;
            }

            String[] credentials =  getCredentialsFromToken(token);


        }
//        System.out.println(request.getHeader("Authorization"));
//        System.out.println(request.getMethod() );
//        System.out.println(request.getServletPath());
        filterChain.doFilter(request, response);


    }

    private String[] getCredentialsFromToken(String token) {
        String[] basicAuth = token.split(" ");
        String decode = new String(Base64.getDecoder().decode(basicAuth[1]));
        String[] credentials = decode.split(":");
        return credentials;
    }

    private boolean checkEndPoint(String method, String servletPath) {

        return !  ("POST".equalsIgnoreCase(method) && servletPath.equals("/account/register"));
    }
}
