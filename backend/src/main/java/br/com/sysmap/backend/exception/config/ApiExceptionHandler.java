package br.com.sysmap.backend.exception.config;

import br.com.sysmap.backend.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // Intercepta exceções lançadas em qualquer Controller da aplicação
public class ApiExceptionHandler {

    // 400 Bad Request — arquivo inválido, IDs inválidos, etc.
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request) {

        log.error("API ERROR [400] {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponseDTO(ex.getMessage()));
    }

    // 400 Bad Request — campos obrigatórios não informados (@Valid / @Validated)
    // Retorna mensagem genérica conforme documentação: "Informe os campos obrigatórios corretamente."
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.error("API ERROR [400] {} {}: falha na validação dos campos", request.getMethod(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponseDTO("Informe os campos obrigatórios corretamente."));
    }

    // 403 Forbidden — regras de negócio que bloqueiam a ação
    // Ex: conta desativada, criador tentando se inscrever na própria atividade,
    //     usuário sem permissão para editar/excluir/concluir atividade
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbidden(
            ForbiddenException ex,
            HttpServletRequest request) {

        log.error("API ERROR [403] {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponseDTO(ex.getMessage()));
    }

    // 404 Not Found — recursos não encontrados
    // Ex: usuário não encontrado, atividade não encontrada, participante não encontrado
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        log.error("API ERROR [404] {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponseDTO(ex.getMessage()));
    }

    // 409 Conflict — conflito de dados
    // Ex: e-mail ou CPF já pertence a outro usuário, usuário já inscrito na atividade
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(
            ConflictException ex,
            HttpServletRequest request) {

        log.error("API ERROR [409] {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponseDTO(ex.getMessage()));
    }

    // 500 Internal Server Error — erro inesperado (GlobalException ou qualquer exceção não tratada)
    @ExceptionHandler({GlobalException.class, Exception.class})
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        log.error("API ERROR [500] {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponseDTO("Erro inesperado."));
    }
}
