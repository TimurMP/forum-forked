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
@Order(20)

public class AdminFilter implements Filter {

    final UserAccountRepository userAccountRepository;


    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        if (checkEndPoint(request.getMethod(), request.getServletPath())){
            UserAccount userAccount = userAccountRepository.findById(request.getUserPrincipal().getName()).get();
            if (!userAccount.getRoles().contains("Administrator".toUpperCase())){
                response.sendError(403, "No sufficient privileges");
                return;
            }
        }

        filterChain.doFilter(request, response);


    }

    private boolean checkEndPoint(String method, String servletPath) {
        return servletPath.matches("/account/user/\\w+/role/\\w+/?");
    }
}
