package com.surrender.repo;

import java.util.List;

import com.surrender.model.Casa;

public interface CasaRepo extends IGenericRepo<Casa, Integer> {
    
    /**
     * Encuentra todas las casas asociadas a un usuario por su email
     * Spring Data JPA generará automáticamente la consulta:
     * SELECT c FROM Casa c JOIN c.usuarios u WHERE u.email = ?1
     * @param email Email del usuario
     * @return Lista de casas del usuario
     */
    List<Casa> findByUsuariosEmail(String email);
}
