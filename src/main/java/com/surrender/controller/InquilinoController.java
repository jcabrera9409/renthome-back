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
        APIResponseDTO<Inquilino> response = (nuevo != null)
            ? APIResponseDTO.success("Inquilino registrado", nuevo, 201)
            : APIResponseDTO.error("No se pudo registrar el inquilino", 400);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @PutMapping
    public ResponseEntity<APIResponseDTO<Inquilino>> modificar(@RequestBody Inquilino inquilino) {
        Inquilino actualizado = inquilinoService.modificar(inquilino);
        APIResponseDTO<Inquilino> response = (actualizado != null)
            ? APIResponseDTO.success("Inquilino modificado", actualizado, 200)
            : APIResponseDTO.error("No se pudo modificar el inquilino", 400);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Void>> eliminar(@PathVariable Integer id) {
        try {
            inquilinoService.eliminar(id);
            APIResponseDTO<Void> response = APIResponseDTO.success("Inquilino eliminado", null, 200);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            APIResponseDTO<Void> response = APIResponseDTO.error("No se pudo eliminar el inquilino", 400);
            return ResponseEntity.status(400).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Inquilino>> listarPorId(@PathVariable Integer id) {
        Optional<Inquilino> inquilino = inquilinoService.listarPorId(id);
        APIResponseDTO<Inquilino> response = inquilino.map(i -> APIResponseDTO.success("Inquilino encontrado", i, 200))
            .orElseGet(() -> APIResponseDTO.error("Inquilino no encontrado", 404));
        return ResponseEntity.status(response.isSuccess() ? 200 : 404).body(response);
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<Iterable<Inquilino>>> listarTodos() {
        Iterable<Inquilino> lista = inquilinoService.listarTodos();
        APIResponseDTO<Iterable<Inquilino>> response = APIResponseDTO.success("Lista de inquilinos", lista, 200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<APIResponseDTO<Page<Inquilino>>> filtrar(Pageable pageable) {
        Page<Inquilino> page = inquilinoService.filtrar(pageable);
        APIResponseDTO<Page<Inquilino>> response = APIResponseDTO.success("Página de inquilinos", page, 200);
        return ResponseEntity.ok(response);
    }
}
