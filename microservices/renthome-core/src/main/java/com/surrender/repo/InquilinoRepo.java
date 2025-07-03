package com.surrender.repo;

import com.surrender.model.Inquilino;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquilinoRepo extends IGenericRepo<Inquilino, Integer> {
    // Puedes agregar métodos personalizados aquí si es necesario
    Page<Inquilino> findAll(Pageable pageable);
}
