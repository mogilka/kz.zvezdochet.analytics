package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.PositionType;
import kz.zvezdochet.core.bean.Model;
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

	@Override
	public Model create() {
		return new PositionType();
	}
}
