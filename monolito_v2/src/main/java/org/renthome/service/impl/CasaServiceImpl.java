package org.renthome.service.impl;

import org.renthome.model.Casa;
import org.renthome.repository.CasaRepository;
import org.renthome.service.ICasaService;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación del servicio de Casa para Quarkus
 */
@ApplicationScoped
public class CasaServiceImpl extends CRUDImpl<Casa, Integer> implements ICasaService {

    private static final Logger logger = LoggerFactory.getLogger(CasaServiceImpl.class);

    @Inject
    CasaRepository casaRepository;

    @Override
    protected PanacheRepository<Casa> getRepo() {
        logger.debug("Obteniendo repositorio de casas");
        return casaRepository;
    }
}
