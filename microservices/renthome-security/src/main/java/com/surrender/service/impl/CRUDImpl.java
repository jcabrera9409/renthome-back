package com.surrender.service.impl;

import com.surrender.repo.IGenericRepo;
import com.surrender.service.ICRUD;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public abstract class CRUDImpl<T, ID> implements ICRUD<T, ID> {
    protected abstract IGenericRepo<T, ID> getRepo();

    @Override
    public T registrar(T t) {
        return getRepo().save(t);
    }

    @Override
    public T modificar(T t) {
        return getRepo().save(t);
    }

    @Override
    public void eliminar(ID id) {
        getRepo().deleteById(id);
    }

    @Override
    public Optional<T> listarPorId(ID id) {
        return getRepo().findById(id);
    }

    @Override
    public List<T> listarTodos() {
        return getRepo().findAll();
    }

    @Override
    public Page<T> filtrar(Pageable pageable) {
        return getRepo().findAll(pageable);
    }
}
