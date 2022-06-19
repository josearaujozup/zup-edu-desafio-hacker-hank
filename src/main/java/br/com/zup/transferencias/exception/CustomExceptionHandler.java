package br.com.zup.transferencias.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import org.hibernate.exception.ConstraintViolationException;
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

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErroPadronizado> handleResponseStatus(RuntimeException ex,
                                                                WebRequest webRequest) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        String mensagemGeral = "Houve um problema com a sua requisição.";
        ErroPadronizado erroPadronizado = gerarErroPadronizado(
                httpStatus, webRequest, mensagemGeral
        );
        erroPadronizado.adicionarErro(ex.getMessage());

        return ResponseEntity.status(httpStatus).body(erroPadronizado);
    }

//    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroPadronizado> handleUniqueConstraintErrors(DataIntegrityViolationException ex,
                                                                WebRequest webRequest) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        String mensagemGeral = "Houve um problema com a sua requisição.";
        ErroPadronizado erroPadronizado = gerarErroPadronizado(
                httpStatus, webRequest, mensagemGeral
        );
//        erroPadronizado.adicionarErro(ex.getMessage());

        Throwable cause = ex.getCause();

        String failedField = ((ConstraintViolationException) cause).getConstraintName();

        if (failedField.contains("UK_CPF")) {
            erroPadronizado.adicionarErro("Cpf já cadastrado na api");
        }

        if (failedField.contains("UK_EMAIL")) {
            erroPadronizado.adicionarErro("Email já cadastrado na api");
        }

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
