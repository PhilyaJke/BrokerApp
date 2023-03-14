package accelerator.group.brokerapp.Security;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveAccessToken((HttpServletRequest) request);
//        if(jwtTokenProvider.resolveAccessToken((HttpServletRequest) request).substring(1,6).equals("")) {
//            String[] req = jwtTokenProvider.resolveAccessToken((HttpServletRequest) request).split(" ");
//            token = req[1];
//        }else{
//            token =
//        }

        try {
            if (token != null && jwtTokenProvider.validateAccessToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuth(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }catch (JwtAuthException exc){
            SecurityContextHolder.clearContext();
            System.out.println("dfkvdflkjnvdflkjnvdklfnv");
            ((HttpServletResponse) response).sendError(exc.getHttpStatus().value());
            throw new JwtAuthException("JWT TOKEN IS expired or invalid");
        }

        chain.doFilter(request, response);

    }
}
