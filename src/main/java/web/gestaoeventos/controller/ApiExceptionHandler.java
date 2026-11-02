package web.gestaoeventos.controller;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(
            ResponseStatusException exception) {
        HttpStatusCode status = exception.getStatusCode();
        String detail = exception.getReason() == null
                ? "A solicitação não pôde ser concluída"
                : exception.getReason();
        return resposta(status, detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException exception) {
        String detail = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));
        return resposta(HttpStatus.BAD_REQUEST, detail.isBlank()
                ? "Campos da requisição inválidos"
                : detail);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ProblemDetail> handleInvalidRequest(Exception exception) {
        return resposta(
                HttpStatus.BAD_REQUEST,
                "Corpo ou parâmetro da requisição inválido");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(
            NoResourceFoundException exception) {
        return resposta(HttpStatus.NOT_FOUND, "Endpoint não encontrado");
    }

    private ResponseEntity<ProblemDetail> resposta(HttpStatusCode status, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        HttpStatus httpStatus = HttpStatus.resolve(status.value());
        if (httpStatus != null) {
            problem.setTitle(httpStatus.getReasonPhrase());
        }
        return ResponseEntity.status(status).body(problem);
    }
}
