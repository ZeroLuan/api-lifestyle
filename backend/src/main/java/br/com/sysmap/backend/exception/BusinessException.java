package br.com.sysmap.backend.exception;

// Exceção para erros de regra de negócio que não se encaixam nos status HTTP específicos.
// Use quando a operação é tecnicamente válida mas viola uma regra de negócio,
// e ainda não há uma exceção semântica mais específica para o caso.
// Na maioria dos casos, prefira EntityNotFoundException, ConflictException ou ForbiddenException.
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
