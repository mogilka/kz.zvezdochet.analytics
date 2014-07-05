package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.Cross;
import kz.zvezdochet.core.bean.BaseEntity;

/**
 * Реализация сервиса крестов
 * @author nataly
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class CrossService extends GenderTextDiagramService {

	public CrossService() {
		tableName = "crosses";
	}

	@Override
	public BaseEntity createEntity() {
		return new Cross();
	}
}
