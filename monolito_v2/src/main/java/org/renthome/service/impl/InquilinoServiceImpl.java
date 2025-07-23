package org.renthome.service.impl;

import org.renthome.model.Inquilino;
import org.renthome.repository.InquilinoRepository;
import org.renthome.service.IInquilinoService;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación del servicio de Inquilino para Quarkus
 * Migrado desde Spring Boot InquilinoServiceImpl
 */
@ApplicationScoped
public class InquilinoServiceImpl extends CRUDImpl<Inquilino, Integer> implements IInquilinoService {

    private static final Logger logger = LoggerFactory.getLogger(InquilinoServiceImpl.class);

    @Inject
    InquilinoRepository inquilinoRepository;

    @Override
    protected PanacheRepository<Inquilino> getRepo() {
        logger.debug("Obteniendo repositorio de inquilinos");
        return inquilinoRepository;
    }
}
