package application.exceptions.custom_exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
public class CustomDataNotFoundException extends RuntimeException{

    public CustomDataNotFoundException() {
        super();
    }

    public CustomDataNotFoundException(String message) {
        super(message);
    }
}
