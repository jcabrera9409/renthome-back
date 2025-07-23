package org.renthome.service.impl;

import org.renthome.model.Contrato;
import org.renthome.repository.ContratoRepository;
import org.renthome.service.IContratoService;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación del servicio de Contrato para Quarkus
 */
@ApplicationScoped
public class ContratoServiceImpl extends CRUDImpl<Contrato, Integer> implements IContratoService {

    private static final Logger logger = LoggerFactory.getLogger(ContratoServiceImpl.class);

    @Inject
    ContratoRepository contratoRepository;

    @Override
    protected PanacheRepository<Contrato> getRepo() {
        logger.debug("Obteniendo repositorio de contratos");
        return contratoRepository;
    }
}
