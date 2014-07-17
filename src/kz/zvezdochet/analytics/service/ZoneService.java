package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.Zone;
import kz.zvezdochet.core.bean.Base;

/**
 * Реализация сервиса зон
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class ZoneService extends GenderTextDiagramService {

	public ZoneService() {
		tableName = "zones";
	}

	@Override
	public Base create() {
		return new Zone();
	}
}
