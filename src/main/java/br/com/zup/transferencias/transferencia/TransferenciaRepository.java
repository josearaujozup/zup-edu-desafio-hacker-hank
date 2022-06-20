package br.com.zup.transferencias.transferencia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

    Page<Transferencia> findAllByContaDestinoIdOrContaOrigemId(Long idContaDestino, Long idContaOrigem, Pageable paginacao);
}
