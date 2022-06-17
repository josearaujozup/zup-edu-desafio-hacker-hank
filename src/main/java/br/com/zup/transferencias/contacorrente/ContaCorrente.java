package br.com.zup.transferencias.contacorrente;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class ContaCorrente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4)
    private Integer agencia;

    @Column(nullable = false, length = 6)
    private Integer numeroConta;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Column(nullable = false, length = 120)
    private String titular;

    @Column(nullable = false)
    private BigDecimal saldo = BigDecimal.ZERO;

    public ContaCorrente(Long id, Integer agencia, Integer numeroConta, String email, String cpf, String titular) {
        this.id = id;
        this.agencia = agencia;
        this.numeroConta = numeroConta;
        this.email = email;
        this.cpf = cpf;
        this.titular = titular;
    }

    /**
     * @deprecated Construtor de uso exclusivo do Hibernate
     */
    @Deprecated
    public ContaCorrente(){

    }

    public Long getId() {
        return id;
    }
}
