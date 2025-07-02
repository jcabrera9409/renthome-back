package com.surrender.controller;

import com.surrender.dto.APIResponseDTO;
import com.surrender.model.Inquilino;
import com.surrender.service.IInquilinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/v1/inquilinos")
public class InquilinoController {

    @Autowired
    private IInquilinoService inquilinoService;

    @PostMapping
    public ResponseEntity<APIResponseDTO<Inquilino>> registrar(@RequestBody Inquilino inquilino) {
        Inquilino nuevo = inquilinoService.registrar(inquilino);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Inquilino registrado", nuevo, 200));
    }

    @PutMapping
    public ResponseEntity<APIResponseDTO<Inquilino>> modificar(@RequestBody Inquilino inquilino) {
        Inquilino actualizado = inquilinoService.modificar(inquilino);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Inquilino modificado", actualizado, 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Void>> eliminar(@PathVariable Integer id) {
        inquilinoService.eliminar(id);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Inquilino eliminado", null, 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Inquilino>> listarPorId(@PathVariable Integer id) {
        Optional<Inquilino> inquilino = inquilinoService.listarPorId(id);
        if (inquilino.isPresent()) {
            return ResponseEntity.ok(new APIResponseDTO<>(true, "Inquilino encontrado", inquilino.get(), 200));
        } else {
            return ResponseEntity.status(404).body(new APIResponseDTO<>(false, "Inquilino no encontrado", null, 404));
        }
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<Iterable<Inquilino>>> listarTodos() {
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Lista de inquilinos", inquilinoService.listarTodos(), 200));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<APIResponseDTO<Page<Inquilino>>> filtrar(Pageable pageable) {
        Page<Inquilino> page = inquilinoService.filtrar(pageable);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Página de inquilinos", page, 200));
    }
}
