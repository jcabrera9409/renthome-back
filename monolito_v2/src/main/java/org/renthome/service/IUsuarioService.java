package org.renthome.service;

import org.renthome.model.Usuario;

/**
 * Interface para servicio de Usuario
 * Migrado desde Spring Boot IUsuarioService
 */
public interface IUsuarioService extends ICRUD<Usuario, Integer> {
    
    /**
     * Registrar usuario con encriptación de contraseña
     * (sin lógica de autenticación - solo CRUD)
     */
    Usuario registrarUsuario(Usuario usuario);
    
    /**
     * Modificar usuario manteniendo contraseña existente
     */
    Usuario modificarUsuario(Usuario usuario);
    
    /**
     * Buscar usuario por email
     */
    Usuario buscarPorEmail(String email);
    
    /**
     * Verificar si existe usuario por email
     */
    boolean existeEmail(String email);
    
    /**
     * Cambiar contraseña de usuario
     */
    boolean cambiarPassword(Integer usuarioId, String passwordActual, String passwordNueva);
}
