package org.renthome.service.impl;

import org.renthome.model.Usuario;
import org.renthome.repository.UsuarioRepository;
import org.renthome.service.IUsuarioService;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación del servicio de Usuario para Quarkus
 * Migrado desde Spring Boot UsuarioServiceImpl
 * 
 * NOTA: Solo incluye operaciones CRUD básicas, sin lógica de autenticación
 * La autenticación se migrará en la Fase 5
 */
@ApplicationScoped
public class UsuarioServiceImpl extends CRUDImpl<Usuario, Integer> implements IUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Inject
    UsuarioRepository usuarioRepository;

    @Override
    protected PanacheRepository<Usuario> getRepo() {
        return usuarioRepository;
    }

    @Override
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        logger.info("Intentando registrar usuario con email: {}", usuario.email);
        
        Optional<Usuario> existe = usuarioRepository.findByEmail(usuario.email);
        if (!existe.isPresent()) {
            // TODO: Encriptar contraseña - se implementará en Fase 5 con seguridad
            // Por ahora guardamos la contraseña tal como viene para mantener funcionalidad básica
            logger.warn("TEMPORAL: Contraseña sin encriptar - se implementará en Fase 5");
            usuarioRepository.persist(usuario);
            logger.info("Usuario registrado exitosamente: {}", usuario.email);
            return usuario;
        } else {
            logger.warn("Intento de registro fallido: el email {} ya existe", usuario.email);
            return null;
        }
    }

    @Override
    @Transactional
    public Usuario modificarUsuario(Usuario usuario) {
        logger.info("Modificando usuario con id: {}", usuario.id);
        
        Optional<Usuario> existe = usuarioRepository.find("id", usuario.id).firstResultOptional();
        if (existe.isPresent()) {
            Usuario usuarioExistente = existe.get();
            // Mantener la contraseña existente durante modificación
            usuario.password = usuarioExistente.password;
            
            Usuario actualizado = usuarioRepository.getEntityManager().merge(usuario);
            logger.info("Usuario modificado exitosamente: {}", actualizado.email);
            return actualizado;
        } else {
            logger.warn("Intento de modificación fallido: usuario con id {} no encontrado", usuario.id);
            return null;
        }
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        logger.debug("Buscando usuario por email: {}", email);
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    @Override
    public boolean existeEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    // Override de métodos base para usar las implementaciones específicas
    @Override
    @Transactional
    public Usuario registrar(Usuario usuario) {
        return registrarUsuario(usuario);
    }

    @Override
    @Transactional
    public Usuario modificar(Usuario usuario) {
        return modificarUsuario(usuario);
    }
}
