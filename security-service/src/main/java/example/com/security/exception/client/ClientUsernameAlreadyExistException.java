package example.com.security.exception.client;

import jakarta.persistence.EntityExistsException;

public class ClientUsernameAlreadyExistException extends EntityExistsException {
    public ClientUsernameAlreadyExistException(String message) {
        super(message);
    }
}
