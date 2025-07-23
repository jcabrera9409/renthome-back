package org.renthome.service;

import io.quarkus.panache.common.Page;
import java.util.List;
import java.util.Optional;

/**
 * Interface base CRUD para servicios Quarkus
 * Migrado desde Spring Boot ICRUD manteniendo compatibilidad
 */
public interface ICRUD<T, ID> {
    
    /**
     * Registrar una nueva entidad
     */
    T registrar(T t);
    
    /**
     * Modificar una entidad existente
     */
    T modificar(T t);
    
    /**
     * Eliminar entidad por ID
     */
    void eliminar(ID id);
    
    /**
     * Buscar entidad por ID
     */
    Optional<T> listarPorId(ID id);
    
    /**
     * Listar todas las entidades
     */
    List<T> listarTodos();
    
    /**
     * Filtrar con paginación (adaptado para Quarkus Panache)
     */
    List<T> filtrar(int pageIndex, int pageSize);
    
    /**
     * Contar total de entidades
     */
    long contar();
}
