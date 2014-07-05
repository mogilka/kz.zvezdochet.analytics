package kz.zvezdochet.analytics.service;


/**
 * Реализация сервиса типов космограмм
 * @author nataly
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class CardTypeService extends GenderTextReferenceService {

	public CardTypeService() {
		tableName = "cardtypes";
	}
}
