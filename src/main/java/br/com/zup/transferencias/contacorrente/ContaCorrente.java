package br.com.zup.transferencias.contacorrente;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UK_CPF", columnNames = {"cpf"}),
        @UniqueConstraint(name = "UK_EMAIL", columnNames = {"email"})})
public class ContaCorrente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4)
    private String agencia;

    @Column(nullable = false, length = 6)
    private String numeroConta;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 14)
    private String cpf;

    @Column(nullable = false, length = 120)
    private String titular;

    @Column(nullable = false)
    private BigDecimal saldo;

    public ContaCorrente(String agencia, String numeroConta, String email, String cpf, String titular) {
        this.agencia = agencia;
        this.numeroConta = numeroConta;
        this.email = email;
        this.cpf = cpf;
        this.titular = titular;
        this.saldo = BigDecimal.ZERO;
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
