package telran.java2022.security.filter.PostSecurity;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import telran.java2022.accounting.dao.UserAccountRepository;
import telran.java2022.accounting.model.UserAccount;
import telran.java2022.post.dao.PostRepository;
import telran.java2022.post.dto.exceptions.PostNotFoundException;
import telran.java2022.post.model.Post;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(45)

public class EditPostFilter implements Filter {
    final UserAccountRepository userAccountRepository;
    final PostRepository postRepository;



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
            Post post = postRepository.findById(path[3]).orElseThrow(() -> new PostNotFoundException(path[3]));

            UserAccount userAccount = userAccountRepository.findById(request.getUserPrincipal().getName()).get();
            if (!userAccount.getLogin().equals(post.getAuthor())){
                response.sendError(403, "No sufficient privileges, cannot edit post");
                return;
            }
        }
        filterChain.doFilter(request, response);

    }

    private boolean checkEndPoint(String method, String servletPath) {
        return servletPath.matches("/forum/post/\\w+/?");
    }
}
