package br.com.zup.transferencias.transferencia;

import br.com.zup.transferencias.contacorrente.ContaCorrente;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferenciaRequest {

    @Positive
    private BigDecimal valor;

    @NotNull
    @Valid
    private ContaTransferenciaRequest contaOrigemRequest;

    @NotNull
    @Valid
    private ContaTransferenciaRequest contaDestinoRequest;

    public TransferenciaRequest() {
    }

    public TransferenciaRequest(BigDecimal valor, ContaTransferenciaRequest contaOrigem, ContaTransferenciaRequest contaDestino) {
        this.valor = valor;
        this.contaOrigemRequest = contaOrigem;
        this.contaDestinoRequest = contaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public ContaTransferenciaRequest getContaOrigemRequest() {
        return contaOrigemRequest;
    }

    public ContaTransferenciaRequest getContaDestinoRequest() {
        return contaDestinoRequest;
    }

    public Transferencia toModel(ContaCorrente contaOrigem, ContaCorrente contaDestino) {
        contaOrigem.sacar(this.valor);
        contaDestino.depositar(this.valor);
        return new Transferencia(this.valor,contaOrigem,contaDestino);
    }
}
