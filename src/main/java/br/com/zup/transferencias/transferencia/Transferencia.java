package br.com.zup.transferencias.transferencia;

import br.com.zup.transferencias.contacorrente.ContaCorrente;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal valor;

    @ManyToOne
    private ContaCorrente contaOrigem;

    @ManyToOne
    private ContaCorrente contaDestino;

    private LocalDateTime instanteTransferencia;

    public Transferencia(BigDecimal valor, ContaCorrente contaOrigem, ContaCorrente contaDestino) {
        this.valor = valor;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.instanteTransferencia = LocalDateTime.now();
    }

    /**
     * @deprecated Construtor de uso exclusivo do Hibernate
     */
    @Deprecated
    public Transferencia(){

    }

    public Long getId() {
        return id;
    }
}
