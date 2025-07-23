package org.renthome.service;

import org.renthome.model.Inquilino;

/**
 * Interface para servicio de Inquilino
 * Migrado desde Spring Boot IInquilinoService
 */
public interface IInquilinoService extends ICRUD<Inquilino, Integer> {
    // Métodos adicionales específicos para Inquilino si es necesario
    // Por ahora mantenemos solo los métodos CRUD básicos heredados
}
