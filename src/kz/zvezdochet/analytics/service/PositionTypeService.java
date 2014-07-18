package kz.zvezdochet.analytics.service;

import kz.zvezdochet.core.service.ReferenceService;

/**
 * Реализация сервиса позиций объектов
 * @author Nataly Didenko
 *
 * @see ReferenceService Реализация сервиса справочников  
 */
public class PositionTypeService extends ReferenceService {

	public PositionTypeService() {
		tableName = "positiontype";
	}
}
