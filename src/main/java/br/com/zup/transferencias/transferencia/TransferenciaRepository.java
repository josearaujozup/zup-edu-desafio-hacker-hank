package br.com.zup.transferencias.transferencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {
    List<Transferencia> findAllByContaDestinoIdOrContaOrigemId(Long idContaDestino, Long idContaOrigem);
}
