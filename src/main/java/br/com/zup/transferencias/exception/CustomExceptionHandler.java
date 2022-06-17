package br.com.zup.transferencias.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadronizado> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                        WebRequest webRequest) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Integer totalErros = fieldErrors.size();
        String palavraErro = totalErros == 1 ? "erro" : "erros";
        String mensagemGeral = "Validação falha com " + totalErros + " " + palavraErro + ".";
        ErroPadronizado erroPadronizado = gerarErroPadronizado(
                httpStatus, webRequest, mensagemGeral
        );
        fieldErrors.forEach(erroPadronizado::adicionarErro);

        return ResponseEntity.status(httpStatus).body(erroPadronizado);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroPadronizado> handleResponseStatus(ResponseStatusException ex,
                                                                WebRequest webRequest) {
        HttpStatus httpStatus = ex.getStatus();
        String mensagemGeral = "Houve um problema com a sua requisição.";
        ErroPadronizado erroPadronizado = gerarErroPadronizado(
                httpStatus, webRequest, mensagemGeral
        );
        erroPadronizado.adicionarErro(ex.getReason());

        return ResponseEntity.status(httpStatus).body(erroPadronizado);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErroPadronizado> handleUniqueConstraintErrors(SQLIntegrityConstraintViolationException ex,
                                                                WebRequest webRequest) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        String mensagemGeral = "Houve um problema com a sua requisição.";
        ErroPadronizado erroPadronizado = gerarErroPadronizado(
                httpStatus, webRequest, mensagemGeral
        );
        erroPadronizado.adicionarErro(ex.getMessage());

        Throwable cause = ex.getCause();

        return ResponseEntity.status(httpStatus).body(erroPadronizado);
    }

    public ErroPadronizado gerarErroPadronizado(HttpStatus httpStatus, WebRequest webRequest,
                                                String mensagemGeral) {
        Integer codigoHttp = httpStatus.value();
        String mensagemHttp = httpStatus.getReasonPhrase();
        String caminho = webRequest.getDescription(false).replace("uri=", "");
        ErroPadronizado erroPadronizado = new ErroPadronizado(
                codigoHttp, mensagemHttp, mensagemGeral, caminho
        );

        return erroPadronizado;
    }

}