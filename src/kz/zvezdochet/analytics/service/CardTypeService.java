package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.CardType;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.GenderTextDictionaryService;

/**
 * Сервис типов космограмм
 * @author Nataly Didenko
 */
public class CardTypeService extends GenderTextDictionaryService {

	public CardTypeService() {
		tableName = "cardtypes";
	}

	@Override
	public Model create() {
		return new CardType();
	}
}
