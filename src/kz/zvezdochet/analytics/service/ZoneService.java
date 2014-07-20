package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.Zone;
import kz.zvezdochet.core.bean.Model;


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
	public Model create() {
		return new Zone();
	}
}
