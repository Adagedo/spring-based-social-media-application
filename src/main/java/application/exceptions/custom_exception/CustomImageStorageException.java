package application.exceptions.custom_exception;

public class CustomImageStorageException extends RuntimeException{

    public CustomImageStorageException(String message){
        super(message);
    }

    public CustomImageStorageException(String message, Throwable cause){
        super(message, cause);
    }
}
