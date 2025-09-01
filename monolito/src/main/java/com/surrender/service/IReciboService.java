package com.surrender.service;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.surrender.model.Recibo;

public interface IReciboService extends ICRUD<Recibo, Integer> {
    List<Recibo> generarRecibosPorPeriodo(Integer casaId, YearMonth periodo);
    Page<Recibo> filtrarPorCasa(Integer casaId, String filtro, Pageable pageable);
}