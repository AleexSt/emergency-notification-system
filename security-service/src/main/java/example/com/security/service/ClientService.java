package example.com.security.service;

import example.com.security.dto.request.SecurityRequest;
import example.com.security.dto.response.TokenResponse;
import example.com.security.entity.Client;
import example.com.security.exception.client.ClientBadCredentialsException;
import example.com.security.exception.client.ClientNotFoundException;
import example.com.security.exception.client.ClientUsernameAlreadyExistException;
import example.com.security.mapper.ClientMapper;
import example.com.security.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final MessageSourceService message;
    private final PasswordEncoder passwordEncoder;
    private final ClientMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final TokenService tokenService;

    public Boolean register(SecurityRequest request){
        if(clientRepository.findByUsername(request.username()).isPresent()){
            throw new ClientUsernameAlreadyExistException(message.getProperty("client.username.already_exists"));
        }

        Client client = mapper.mapToEntity(request, passwordEncoder);
        clientRepository.saveAndFlush(client);

        return true;
    }

    public TokenResponse authenticate(SecurityRequest request){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
        }catch (InternalAuthenticationServiceException e){
            throw new ClientNotFoundException(message.getProperty("client.not_found", request.username()));
        }catch (BadCredentialsException e){
            throw new ClientBadCredentialsException(message.getProperty("client.bad_cred"));
        }

        Client client = loadUserByUsername(request.username());
        tokenService.deletePreviousClientToken(client);
        String jwt = jwtService.generateJwt(client);
        tokenService.createToken(client, jwt);

        return new TokenResponse(jwt);

    }

    public Client loadUserByUsername(String username){
        return clientRepository.findByUsername(username)
                .orElseThrow(() -> new ClientNotFoundException(
                        message.getProperty("client.not_found", username)
                ));
    }

}
