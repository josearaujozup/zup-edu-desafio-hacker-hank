package br.com.zup.transferencias.transferencia;

import br.com.zup.transferencias.contacorrente.ContaCorrente;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferenciaResponse {

    private BigDecimal valor;

    private ContaCorrente contaOrigem;

    private ContaCorrente contaDestino;

    private LocalDateTime instanteTransferencia;

    public TransferenciaResponse() {
    }

    public TransferenciaResponse(Transferencia transferencia) {
        this.valor = transferencia.getValor();
        this.contaOrigem = transferencia.getContaOrigem();
        this.contaDestino = transferencia.getContaDestino();
        this.instanteTransferencia = transferencia.getInstanteTransferencia();
    }

    public BigDecimal getValor() {
        return valor;
    }

    public ContaCorrente getContaOrigem() {
        return contaOrigem;
    }

    public ContaCorrente getContaDestino() {
        return contaDestino;
    }

    public LocalDateTime getInstanteTransferencia() {
        return instanteTransferencia;
    }
}
