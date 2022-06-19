package br.com.zup.transferencias.transferencia;

import br.com.zup.transferencias.contacorrente.ContaCorrente;

public class ContaCorrenteTransferenciaResponse {

    private String agencia;

    private String numeroConta;

    public ContaCorrenteTransferenciaResponse() {
    }

    public ContaCorrenteTransferenciaResponse(ContaCorrente contaCorrente) {
        this.agencia = contaCorrente.getAgencia();
        this.numeroConta = contaCorrente.getNumeroConta();
    }

    public String getAgencia() {
        return agencia;
    }

    public String getNumeroConta() {
        return numeroConta;
    }
}
