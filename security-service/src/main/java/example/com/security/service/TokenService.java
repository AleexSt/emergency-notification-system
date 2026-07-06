package example.com.security.service;

import example.com.security.entity.Client;
import example.com.security.entity.Token;
import example.com.security.exception.client.ClientJwtNotFoundException;
import example.com.security.exception.client.ClientNotFoundException;
import example.com.security.exception.token.InvalidTokenException;
import example.com.security.repository.TokenRepository;
import jakarta.ws.rs.ext.ParamConverter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class TokenService {
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final ClientService clientService;
    private final MessageSourceService message;

    public void createToken(Client client, String jwt){
        tokenRepository.save(Token.builder().client(client).jwt(jwt).expired(false).revoked(false).build());
    }

    public void deletePreviousClientToken(Client client){
        tokenRepository.findByClient_Id(client.getId()).ifPresent(tokenRepository::delete);
    }

    public boolean isTokenValid(String jwtToken){
        Client client = takeUserDetailsFromJwt(jwtToken);
        boolean isTokenValid = tokenRepository.findByJwt(jwtToken)
                .map(token -> !token.isExpired() && !token.isRevoked())
                .orElse(false);
        if (isTokenValid && jwtService.isJwtValid(jwtToken, client)){
            return true;
        }else{
            throw new InvalidTokenException(message.getProperty("jwt.invalid"));
        }
    }

    public Client takeUserDetailsFromJwt(String jwt){
        String username = jwtService.extractUsername(jwt);

        try{
            return clientService.loadUserByUsername(username);
        }catch (ClientNotFoundException e){
            throw new ClientJwtNotFoundException(e.getMessage());
        }
    }





}
