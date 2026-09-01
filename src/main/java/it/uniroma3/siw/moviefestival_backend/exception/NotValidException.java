package it.uniroma3.siw.moviefestival_backend.exception;

public class NotValidException extends RuntimeException{
    public NotValidException(String message){
        super(message);
    }
}
