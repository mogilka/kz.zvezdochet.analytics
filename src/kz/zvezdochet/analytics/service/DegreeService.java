package kz.zvezdochet.analytics.service;



/**
 * Реализация сервиса Зодиакальных градусов
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class DegreeService extends GenderTextReferenceService {

	public DegreeService() {
		tableName = "degrees";
	}
}
