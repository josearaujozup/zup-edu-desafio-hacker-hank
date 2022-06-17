package br.com.zup.transferencias.transferencia;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ContaTransferenciaRequest {

    @NotNull
    @Size(min = 4, max = 4)
    private String agencia;

    @NotNull
    @Size(min = 6, max = 6)
    private String numeroConta;

    public ContaTransferenciaRequest() {
    }

    public ContaTransferenciaRequest(String agencia, String numeroConta) {
        this.agencia = agencia;
        this.numeroConta = numeroConta;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getNumeroConta() {
        return numeroConta;
    }
}
