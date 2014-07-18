package kz.zvezdochet.analytics.service;

/**
 * Реализация сервиса Инь-Ян
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class InYanService extends GenderTextDiagramService {

	public InYanService() {
		tableName = "inyan";
	}
}
