package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.Square;
import kz.zvezdochet.core.bean.BaseEntity;

/**
 * Реализация сервиса крестов
 * @author nataly
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class SquareService extends GenderTextDiagramService {

	public SquareService() {
		tableName = "squares";
	}

	@Override
	public BaseEntity createEntity() {
		return new Square();
	}
}
