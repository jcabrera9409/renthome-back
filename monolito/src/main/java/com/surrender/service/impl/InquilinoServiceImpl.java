package com.surrender.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.surrender.model.Inquilino;
import com.surrender.repo.InquilinoRepo;
import com.surrender.service.IInquilinoService;

@Service
public class InquilinoServiceImpl extends CRUDImpl<Inquilino, Integer> implements IInquilinoService {

    private static final Logger logger = LoggerFactory.getLogger(InquilinoServiceImpl.class);

    @Autowired
    private InquilinoRepo inquilinoRepo;

    @Override
    protected InquilinoRepo getRepo() {
        logger.debug("Obteniendo repositorio de inquilinos");
        return inquilinoRepo;
    }

    @Override
    public List<Inquilino> listarDisponibles() {
        logger.info("Listando todos los inquilinos disponibles (activos)");
        List<Inquilino> inquilinos = inquilinoRepo.findByActivoTrueOrderByNombreCompletoAsc();
        logger.info("Se encontraron {} inquilinos disponibles", inquilinos.size());
        return inquilinos;
    }
}
