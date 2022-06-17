package br.com.zup.transferencias.contacorrente;

import java.math.BigDecimal;

public class ContaCorrenteResponse {

    private String agencia;

    private String numeroConta;

    private BigDecimal saldo;

    public ContaCorrenteResponse() {
    }

    public ContaCorrenteResponse(String agencia, String numeroConta, BigDecimal saldo) {
        this.agencia = agencia;
        this.numeroConta = numeroConta;
        this.saldo = saldo;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
