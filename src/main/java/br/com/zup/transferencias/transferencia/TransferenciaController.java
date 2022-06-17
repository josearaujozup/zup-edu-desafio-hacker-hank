package br.com.zup.transferencias.transferencia;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
public class TransferenciaController {

    private TransferenciaRepository transferenciaRepository;

    public TransferenciaController(TransferenciaRepository transferenciaRepository) {
        this.transferenciaRepository = transferenciaRepository;
    }


    // Create: POST -> 201
    @PostMapping("/transferencias")
    public ResponseEntity<Void> cadastrar(@RequestBody @Valid TransferenciaRequest request, UriComponentsBuilder uriComponentsBuilder){

        

        URI location = uriComponentsBuilder.path("/transferencias/{id}")
                .buildAndExpand(transferencia.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

}
