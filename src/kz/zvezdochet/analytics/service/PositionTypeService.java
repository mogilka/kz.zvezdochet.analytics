package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.PositionType;
import kz.zvezdochet.core.bean.BaseEntity;
import kz.zvezdochet.core.service.ReferenceService;

/**
 * Реализация сервиса позиций объектов
 * @author nataly
 *
 * @see ReferenceService Реализация сервиса справочников  
 */
public class PositionTypeService extends ReferenceService {

	public PositionTypeService() {
		tableName = "positiontype";
	}

	@Override
	public BaseEntity createEntity() {
		return new PositionType();
	}
}
