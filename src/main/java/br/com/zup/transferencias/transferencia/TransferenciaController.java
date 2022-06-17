package br.com.zup.transferencias.transferencia;

import br.com.zup.transferencias.contacorrente.ContaCorrente;
import br.com.zup.transferencias.contacorrente.ContaCorrenteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class TransferenciaController {

    private TransferenciaRepository transferenciaRepository;

    private ContaCorrenteRepository contaCorrenteRepository;

    public TransferenciaController(TransferenciaRepository transferenciaRepository, ContaCorrenteRepository contaCorrenteRepository) {
        this.transferenciaRepository = transferenciaRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
    }

    // Create: POST -> 201
    @PostMapping("/transferencias")
    @Transactional
    public ResponseEntity<Void> cadastrar(@RequestBody @Valid TransferenciaRequest request, UriComponentsBuilder uriComponentsBuilder){

        ContaCorrente contaOrigem = contaCorrenteRepository.findByAgenciaAndNumeroConta(
                request.getContaOrigemRequest().getAgencia(),request.getContaOrigemRequest().getNumeroConta())
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Conta origem não encontrado."
                        )
                );

        ContaCorrente contaDestino = contaCorrenteRepository.findByAgenciaAndNumeroConta(
                        request.getContaDestinoRequest().getAgencia(),request.getContaDestinoRequest().getNumeroConta())
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Conta destino não encontrado."
                        )
                );

        Transferencia transferencia = request.toModel(contaOrigem, contaDestino);
        transferenciaRepository.save(transferencia);

        URI location = uriComponentsBuilder.path("/transferencias/{id}")
                .buildAndExpand(transferencia.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

}
