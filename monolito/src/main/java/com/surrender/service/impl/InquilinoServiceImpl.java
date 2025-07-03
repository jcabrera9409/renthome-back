package com.surrender.service.impl;

import com.surrender.model.Inquilino;
import com.surrender.repo.InquilinoRepo;
import com.surrender.service.IInquilinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
