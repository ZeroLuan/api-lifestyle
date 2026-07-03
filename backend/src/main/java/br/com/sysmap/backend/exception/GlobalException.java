package br.com.sysmap.backend.exception;

// Exceção genérica para erros inesperados (500 Internal Server Error)
public class GlobalException extends RuntimeException {
    public GlobalException(String message) {
        super(message);
    }
}