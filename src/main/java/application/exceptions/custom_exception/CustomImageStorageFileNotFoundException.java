package application.exceptions.custom_exception;

public class CustomImageStorageFileNotFoundException extends CustomImageStorageException{
    public CustomImageStorageFileNotFoundException(String message) {
        super(message);
    }

    public CustomImageStorageFileNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
