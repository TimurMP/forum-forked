package telran.java2022.security.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import telran.java2022.accounting.dao.UserAccountRepository;
import telran.java2022.accounting.model.UserAccount;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(23)

public class UserUpdateFilter implements Filter {
    final UserAccountRepository userAccountRepository;


    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (!((HttpServletRequest) req).getMethod().equals("PUT")){
            filterChain.doFilter(request, response);
            return;
        }


        if (checkEndPoint(request.getMethod(), request.getServletPath())){
            String[] path = request.getServletPath().split("/");
            UserAccount userAccount = userAccountRepository.findById(request.getUserPrincipal().getName()).get();
            if (!userAccount.getLogin().equals(path[3])){
                System.out.println(((HttpServletRequest) req).getMethod());
                response.sendError(403, "No sufficient privileges");
                return;
            }
        }
        System.out.println("Works");
        filterChain.doFilter(request, response);

    }

    private boolean checkEndPoint(String method, String servletPath) {
        return servletPath.matches("/account/user/\\w+/?");
    }
}
