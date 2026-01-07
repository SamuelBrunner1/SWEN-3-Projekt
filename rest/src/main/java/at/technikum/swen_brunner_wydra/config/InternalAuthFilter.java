package at.technikum.swen_brunner_wydra.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalAuthFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Internal-Secret";

    @Value("${app.internal.secret}")
    private String internalSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // nur internal schützen
        String provided = request.getHeader(HEADER);

        if (internalSecret == null || internalSecret.isBlank()) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal secret not configured");
            return;
        }

        if (!internalSecret.equals(provided)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
