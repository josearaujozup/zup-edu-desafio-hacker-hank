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

@RestController
public class RealizarTransferenciaController {

    private TransferenciaRepository transferenciaRepository;

    private ContaCorrenteRepository contaCorrenteRepository;

    public RealizarTransferenciaController(TransferenciaRepository transferenciaRepository, ContaCorrenteRepository contaCorrenteRepository) {
        this.transferenciaRepository = transferenciaRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
    }

    // Create: POST -> 201
    @PostMapping("/transferencias")
    @Transactional
    public ResponseEntity<Void> cadastrar(@RequestBody @Valid TransferenciaRequest request, UriComponentsBuilder uriComponentsBuilder){

        ContaCorrente contaOrigem = contaCorrenteRepository.findById(request.getIdContaOrigem())
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Conta origem não encontrada."
                        )
                );

        ContaCorrente contaDestino = contaCorrenteRepository.findById(request.getIdContaDestino())
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Conta destino não encontrada."
                        )
                );

        if(contaOrigem.equals(contaDestino)){
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Conta de destino igual a conta de origem."
            );
        }

        Transferencia transferencia = request.toModel(contaOrigem, contaDestino);
        transferencia.realizarTransferencia();
        transferenciaRepository.save(transferencia);

        URI location = uriComponentsBuilder.path("/transferencias/{id}")
                .buildAndExpand(transferencia.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

}
