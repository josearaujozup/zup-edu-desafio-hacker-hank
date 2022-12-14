package br.com.zup.transferencias.contacorrente;

import br.com.zup.transferencias.exception.SaldoInsuficienteException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

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

    @Version
    private int versao;

    public ContaCorrente(String agencia, String numeroConta, String email, String cpf, String titular) {
        this.agencia = agencia;
        this.numeroConta = numeroConta;
        this.email = email;
        this.cpf = cpf;
        this.titular = titular;
        this.saldo = BigDecimal.ZERO.setScale(2);
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

    public void debitar(BigDecimal valor) {

        if(this.saldo.compareTo(valor) < 0){
            throw new SaldoInsuficienteException("Conta com saldo Insuficiente");
        }

        this.saldo = this.saldo.subtract(valor);
    }

    public void creditar(BigDecimal valor) {
        this.saldo =  this.saldo.add(valor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContaCorrente that = (ContaCorrente) o;
        return id.equals(that.id) && cpf.equals(that.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cpf);
    }
}
