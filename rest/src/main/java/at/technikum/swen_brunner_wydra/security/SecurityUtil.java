package at.technikum.swen_brunner_wydra.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SecurityUtil {

    public static Long requireUserId(HttpServletRequest request) {
        Object v = request.getAttribute("userId");
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
}
