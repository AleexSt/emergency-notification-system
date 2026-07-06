package example.com.security.service;

import example.com.security.entity.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtService jwtService;
    private final TokenService tokenService;
    private final ClientService clientService;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication
    ) {
        String jwt = jwtService.extractJwt(request);

        if(jwt != null && !jwt.isEmpty()){
            String username = jwtService.extractUsername(jwt);
            Client client = clientService.loadUserByUsername(username);

            tokenService.deletePreviousClientToken(client);
            SecurityContextHolder.clearContext();
        }
    }
}
