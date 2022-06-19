package br.com.zup.transferencias.transferencia;

import br.com.zup.transferencias.contacorrente.ContaCorrente;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferenciaResponse {

    private BigDecimal valor;

    private ContaCorrenteTransferenciaResponse contaOrigem;

    private ContaCorrenteTransferenciaResponse contaDestino;

    private LocalDateTime instanteTransferencia;

    public TransferenciaResponse() {
    }

    public TransferenciaResponse(Transferencia transferencia) {
        this.valor = transferencia.getValor();
        this.contaOrigem = new ContaCorrenteTransferenciaResponse(transferencia.getContaOrigem());
        this.contaDestino = new ContaCorrenteTransferenciaResponse(transferencia.getContaDestino());
        this.instanteTransferencia = transferencia.getInstanteTransferencia();
    }

    public BigDecimal getValor() {
        return valor;
    }

    public ContaCorrenteTransferenciaResponse getContaOrigem() {
        return contaOrigem;
    }

    public ContaCorrenteTransferenciaResponse getContaDestino() {
        return contaDestino;
    }

    public LocalDateTime getInstanteTransferencia() {
        return instanteTransferencia;
    }
}
