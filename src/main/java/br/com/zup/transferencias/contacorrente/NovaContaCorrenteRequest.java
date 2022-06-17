package br.com.zup.transferencias.contacorrente;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.*;

public class NovaContaCorrenteRequest {

    @NotNull
    @Size(min = 4, max = 4)
    private String agencia;

    @NotNull
    @Size(min = 6, max = 6)
    private String numeroConta;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @CPF
    private String cpf;

    @NotBlank
    @Length(max = 120)
    private String titular;

    public NovaContaCorrenteRequest() {
    }

    public NovaContaCorrenteRequest(String agencia, String numeroConta, String email, String cpf, String titular) {
        this.agencia = agencia;
        this.numeroConta = numeroConta;
        this.email = email;
        this.cpf = cpf;
        this.titular = titular;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTitular() {
        return titular;
    }

    public ContaCorrente toModel() {
        return new ContaCorrente(agencia,numeroConta,email,cpf,titular);
    }
}
