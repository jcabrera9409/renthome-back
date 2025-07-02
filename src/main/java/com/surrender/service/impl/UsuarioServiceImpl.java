package com.surrender.service.impl;

import com.surrender.model.Usuario;
import com.surrender.repo.UsuarioRepo;
import com.surrender.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl extends CRUDImpl<Usuario, Integer> implements IUsuarioService {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Override
    protected UsuarioRepo getRepo() {
        return usuarioRepo;
    }
}
