package br.com.zup.transferencias.contacorrente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Long> {
    public boolean existsByCpf(String cpf);

    public boolean existsByEmail(String email);

}
