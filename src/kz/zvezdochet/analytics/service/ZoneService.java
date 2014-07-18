package kz.zvezdochet.analytics.service;


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
}
