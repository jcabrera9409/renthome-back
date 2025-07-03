package com.surrender.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICRUD<T, ID> {
    T registrar(T t);
    T modificar(T t);
    void eliminar(ID id);
    Optional<T> listarPorId(ID id);
    List<T> listarTodos();
    Page<T> filtrar(Pageable pageable);
}
