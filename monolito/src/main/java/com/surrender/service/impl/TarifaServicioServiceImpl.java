package com.surrender.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.surrender.model.TarifaServicio;
import com.surrender.repo.TarifaServicioRepo;
import com.surrender.service.ITarifaServicioService;

@Service
public class TarifaServicioServiceImpl extends CRUDImpl<TarifaServicio, Integer> implements ITarifaServicioService {

    private static final Logger logger = LoggerFactory.getLogger(TarifaServicioServiceImpl.class);

    @Autowired
    private TarifaServicioRepo tarifaServicioRepo;

    @Override
    protected TarifaServicioRepo getRepo() {
        return tarifaServicioRepo;
    }

    @Override
    public List<TarifaServicio> listarActivos() {
        logger.info("Listando tarifas de servicio activas");
        List<TarifaServicio> activos = tarifaServicioRepo.findByActivoTrue();
        logger.info("Se encontraron {} tarifas activas", activos.size());
        return activos;
    }

    @Override
    public boolean cambiarEstado(Integer id, boolean activo) {
        logger.info("Cambiando estado de tarifa con id: {} a {}", id, activo ? "activo" : "inactivo");
        try {
            Optional<TarifaServicio> tarifaOpt = tarifaServicioRepo.findById(id);
            if (tarifaOpt.isPresent()) {
                TarifaServicio tarifa = tarifaOpt.get();
                tarifa.setActivo(activo);
                tarifaServicioRepo.save(tarifa);
                logger.info("Estado de tarifa con id: {} cambiado exitosamente a {}", id, activo ? "activo" : "inactivo");
                return true;
            } else {
                logger.warn("No se encontró tarifa con id: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al cambiar estado de tarifa con id: {}: {}", id, e.getMessage());
            return false;
        }
    }

    @Override
    public List<TarifaServicio> listarPorCasa(Integer casaId) {
        logger.info("Listando tarifas de servicio para casa ID: {}", casaId);
        List<TarifaServicio> tarifas = tarifaServicioRepo.findByCasaId(casaId);
        logger.info("Se encontraron {} tarifas para la casa {}", tarifas.size(), casaId);
        return tarifas;
    }

    @Override
    public List<TarifaServicio> listarActivosPorCasa(Integer casaId) {
        logger.info("Listando tarifas de servicio activas para casa ID: {}", casaId);
        List<TarifaServicio> tariffasActivas = tarifaServicioRepo.findByCasaIdAndActivoTrue(casaId);
        logger.info("Se encontraron {} tarifas activas para la casa {}", tariffasActivas.size(), casaId);
        return tariffasActivas;
    }

    @Override
    public Page<TarifaServicio> filtrarPorCasa(Integer casaId, Pageable pageable) {
        logger.info("Filtrando tarifas de servicio para casa ID: {} con paginación", casaId);
        Page<TarifaServicio> page = tarifaServicioRepo.findByCasaId(casaId, pageable);
        logger.info("Se obtuvo página con {} elementos de {} total para casa {}", 
            page.getNumberOfElements(), page.getTotalElements(), casaId);
        return page;
    }
}
