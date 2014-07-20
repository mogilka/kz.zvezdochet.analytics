package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.CardType;
import kz.zvezdochet.core.bean.Model;


/**
 * Реализация сервиса типов космограмм
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class CardTypeService extends GenderTextReferenceService {

	public CardTypeService() {
		tableName = "cardtypes";
	}

	@Override
	public Model create() {
		return new CardType();
	}
}
