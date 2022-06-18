package br.com.zup.transferencias.transferencia;

import br.com.zup.transferencias.contacorrente.ContaCorrente;
import br.com.zup.transferencias.contacorrente.ContaCorrenteRepository;
import br.com.zup.transferencias.contacorrente.ContaCorrenteResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class ListarTransferenciaController {

    private TransferenciaRepository transferenciaRepository;

    private ContaCorrenteRepository contaCorrenteRepository;

    public ListarTransferenciaController(TransferenciaRepository transferenciaRepository, ContaCorrenteRepository contaCorrenteRepository) {
        this.transferenciaRepository = transferenciaRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
    }

    @GetMapping("/contas/{id}/transferencias")
    public ResponseEntity<List<TransferenciaResponse>> listarTransferencias(@PathVariable Long id){

        ContaCorrente contaCorrente = contaCorrenteRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, "Conta Corrente não encontrada"));

        List<TransferenciaResponse> transferencias = transferenciaRepository
                .findAllByContaDestinoIdOrContaOrigemId(contaCorrente.getId(),contaCorrente.getId())
                .stream().map(TransferenciaResponse::new).collect(Collectors.toList());

        return ResponseEntity.ok(transferencias);
    }

}
