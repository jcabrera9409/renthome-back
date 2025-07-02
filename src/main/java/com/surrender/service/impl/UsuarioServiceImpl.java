package com.surrender.service.impl;

import com.surrender.model.Usuario;
import com.surrender.repo.UsuarioRepo;
import com.surrender.service.IUsuarioService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl extends CRUDImpl<Usuario, Integer> implements IUsuarioService {

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
        Optional<Usuario> existe = usuarioRepo.findByEmail(usuario.getEmail());
        if(!existe.isPresent()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            return usuarioRepo.save(usuario);
        } else {
            return null;
        }
    }
}
