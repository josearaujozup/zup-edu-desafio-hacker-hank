package br.com.zup.transferencias.transferencia;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferenciaRequest {

    @Positive
    private BigDecimal valor;

    @NotNull
    @Valid
    private ContaTransferenciaRequest contaOrigem;

    @NotNull
    @Valid
    private ContaTransferenciaRequest contaDestino;

    public TransferenciaRequest() {
    }

    public TransferenciaRequest(BigDecimal valor, ContaTransferenciaRequest contaOrigem, ContaTransferenciaRequest contaDestino) {
        this.valor = valor;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public ContaTransferenciaRequest getContaOrigem() {
        return contaOrigem;
    }

    public ContaTransferenciaRequest getContaDestino() {
        return contaDestino;
    }
}
