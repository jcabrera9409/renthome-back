package com.surrender.service.impl;

import com.surrender.model.Usuario;
import com.surrender.repo.UsuarioRepo;
import com.surrender.service.IUsuarioService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UsuarioServiceImpl extends CRUDImpl<Usuario, Integer> implements IUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
	private PasswordEncoder passwordEncoder;

    @Override
    protected UsuarioRepo getRepo() {
        return usuarioRepo;
    }

    @Override
    public Usuario registrar(Usuario usuario) {
        logger.info("Intentando registrar usuario con email: {}", usuario.getEmail());
        Optional<Usuario> existe = usuarioRepo.findByEmail(usuario.getEmail());
        if(!existe.isPresent()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            Usuario registrado = usuarioRepo.save(usuario);
            logger.info("Usuario registrado exitosamente: {}", registrado.getEmail());
            return registrado;
        } else {
            logger.warn("Intento de registro fallido: el email {} ya existe", usuario.getEmail());
            return null;
        }
    }

    @Override
    public Usuario modificar(Usuario usuario) {
        logger.info("Modificando usuario con id: {}", usuario.getId());
        Optional<Usuario> existe = usuarioRepo.findById(usuario.getId());
        if (existe.isPresent()) {
            Usuario usuarioExistente = existe.get();
            usuario.setPassword(usuarioExistente.getPassword()); 
            Usuario actualizado = usuarioRepo.save(usuario);
            logger.info("Usuario modificado exitosamente: {}", actualizado.getEmail());
            return actualizado;
        } else {
            logger.warn("Intento de modificación fallido: usuario con id {} no encontrado", usuario.getId());
            return null;
        }
    }
}
