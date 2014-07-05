package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.Zone;
import kz.zvezdochet.core.bean.BaseEntity;

/**
 * Реализация сервиса зон
 * @author nataly
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class ZoneService extends GenderTextDiagramService {

	public ZoneService() {
		tableName = "zones";
	}

	@Override
	public BaseEntity createEntity() {
		return new Zone();
	}
}
