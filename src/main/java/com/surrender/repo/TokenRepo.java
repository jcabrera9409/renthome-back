package com.surrender.repo;

import com.surrender.model.Token;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TokenRepo extends IGenericRepo<Token, Integer> {
    Page<Token> findAll(Pageable pageable);
    // Métodos personalizados si es necesario
}
