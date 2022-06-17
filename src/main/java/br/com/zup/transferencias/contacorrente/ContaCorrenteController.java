package br.com.zup.transferencias.contacorrente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class ContaCorrenteController {

    private final ContaCorrenteRepository contaCorrenteRepository;

    public ContaCorrenteController(ContaCorrenteRepository contaCorrenteRepository) {
        this.contaCorrenteRepository = contaCorrenteRepository;
    }

    // Create: POST -> 201
    @PostMapping("/contas")
    public ResponseEntity<Void> cadastrar(@RequestBody @Valid NovaContaCorrenteRequest request, UriComponentsBuilder uriComponentsBuilder){

        if (contaCorrenteRepository.existsByCpf(request.getCpf())){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cpf já cadastrado na api");
        }

        if (contaCorrenteRepository.existsByEmail(request.getEmail())){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email já cadastrado na api");
        }

        ContaCorrente contaCorrente = request.toModel();
        contaCorrenteRepository.save(contaCorrente);

        URI location = uriComponentsBuilder.path("/contas/{id}")
                .buildAndExpand(contaCorrente.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // Read: GET -> 200(body)
    @GetMapping("/contas/{id}")
    public ResponseEntity<ContaCorrenteResponse> consultar(@PathVariable Long id){

        ContaCorrente contaCorrente = contaCorrenteRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, "Conta Corrente não encontrada"));

        return ResponseEntity.ok(new ContaCorrenteResponse(contaCorrente.getAgencia(), contaCorrente.getNumeroConta(), contaCorrente.getSaldo()));
    }


}
