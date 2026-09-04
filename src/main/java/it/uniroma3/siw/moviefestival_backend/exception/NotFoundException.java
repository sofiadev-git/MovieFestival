package it.uniroma3.siw.moviefestival_backend.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }
}
