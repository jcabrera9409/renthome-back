package com.surrender.repo;

import com.surrender.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioRepo extends IGenericRepo<Usuario, Integer> {
    // Puedes agregar métodos personalizados aquí si es necesario
    Page<Usuario> findAll(Pageable pageable);
}
