package br.com.zup.transferencias.transferencia;

import br.com.zup.transferencias.contacorrente.ContaCorrente;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferenciaRequest {

    @NotNull
    @Positive
    private BigDecimal valor;

    @NotNull
    @Positive
    private Long idContaOrigem;

    @NotNull
    @Positive
    private Long idContaDestino;

    public TransferenciaRequest() {
    }

    public TransferenciaRequest(BigDecimal valor, Long idContaOrigem, Long idContaDestino) {
        this.valor = valor;
        this.idContaOrigem = idContaOrigem;
        this.idContaDestino = idContaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Long getIdContaOrigem() {
        return idContaOrigem;
    }

    public Long getIdContaDestino() {
        return idContaDestino;
    }

    public Transferencia toModel(ContaCorrente contaOrigem, ContaCorrente contaDestino) {
        return new Transferencia(this.valor,contaOrigem,contaDestino);
    }
}
