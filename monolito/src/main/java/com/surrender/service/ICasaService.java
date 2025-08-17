package com.surrender.service;

import java.util.List;

import com.surrender.model.Casa;

public interface ICasaService {
    
    /**
     * Lista todas las casas asociadas a un usuario por su email
     * @param email Email del usuario
     * @return Lista de casas del usuario
     */
    List<Casa> listarPorUsuario(String email);
}
