package telran.java2022.security.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import telran.java2022.accounting.dao.UserAccountRepository;
import telran.java2022.accounting.dto.exceptions.UserNotFoundException;
import telran.java2022.accounting.model.UserAccount;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {
    final UserAccountRepository userAccountRepository;
//    final ModelMapper modelMapper;

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

            String[] credentials = new String[0];
            try {
                credentials = getCredentialsFromToken(token);
            } catch (Exception e) {
                response.sendError(401, "Invalid Token");
                return;
            }

            UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElseThrow(() -> new UserNotFoundException());
            if (!userAccount.getPassword().equals(credentials[1])){
                System.out.println("Password Incorrect");
                response.sendError(401, "Username or Password is incorrect" );
                return;
            }
//            System.out.println(userAccount.getPassword());

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

        return !  ("POST".equalsIgnoreCase(method) && servletPath.matches("/account/register/?"));
    }
}
