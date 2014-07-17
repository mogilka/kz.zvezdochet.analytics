package kz.zvezdochet.analytics.service;


/**
 * Реализация сервиса видов космограмм
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class CardKindService extends GenderTextReferenceService {

	public CardKindService() {
		tableName = "cardkinds";
	}
}
