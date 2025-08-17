package com.surrender.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.surrender.model.Casa;
import com.surrender.repo.CasaRepo;
import com.surrender.service.ICasaService;

@Service
public class CasaServiceImpl implements ICasaService {

    private static final Logger logger = LoggerFactory.getLogger(CasaServiceImpl.class);

    @Autowired
    private CasaRepo casaRepo;

    @Override
    public List<Casa> listarPorUsuario(String email) {
        logger.info("Listando casas para usuario con email: {}", email);
        List<Casa> casas = casaRepo.findByUsuariosEmail(email);
        logger.info("Se encontraron {} casas para el usuario: {}", casas.size(), email);
        return casas;
    }
}
