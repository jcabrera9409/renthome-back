package org.renthome.service.impl;

import org.renthome.service.ICRUD;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Implementación base CRUD para servicios Quarkus
 * Migrado desde Spring Boot CRUDImpl adaptado para Panache Repository
 */
public abstract class CRUDImpl<T, ID> implements ICRUD<T, ID> {
    
    /**
     * Método abstracto que debe implementar cada servicio específico
     * para retornar su repositorio correspondiente
     */
    protected abstract PanacheRepository<T> getRepo();

    @Override
    @Transactional
    public T registrar(T t) {
        PanacheRepository<T> repo = getRepo();
        repo.persist(t);
        return t;
    }

    @Override
    @Transactional
    public T modificar(T t) {
        PanacheRepository<T> repo = getRepo();
        return repo.getEntityManager().merge(t);
    }

    @Override
    @Transactional
    public void eliminar(ID id) {
        PanacheRepository<T> repo = getRepo();
        // Para entidades con Integer ID, necesitamos hacer cast
        if (id instanceof Integer) {
            repo.delete("id", id);
        } else {
            repo.deleteById((Long) id);
        }
    }

    @Override
    public Optional<T> listarPorId(ID id) {
        PanacheRepository<T> repo = getRepo();
        // Para entidades con Integer ID, buscar usando query
        if (id instanceof Integer) {
            return repo.find("id", id).firstResultOptional();
        } else {
            return repo.findByIdOptional((Long) id);
        }
    }

    @Override
    public List<T> listarTodos() {
        PanacheRepository<T> repo = getRepo();
        return repo.listAll();
    }

    @Override
    public List<T> filtrar(int pageIndex, int pageSize) {
        PanacheRepository<T> repo = getRepo();
        return repo.findAll().page(Page.of(pageIndex, pageSize)).list();
    }

    @Override
    public long contar() {
        PanacheRepository<T> repo = getRepo();
        return repo.count();
    }
}
