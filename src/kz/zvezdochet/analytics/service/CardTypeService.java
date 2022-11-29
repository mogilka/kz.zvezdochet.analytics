package kz.zvezdochet.analytics.service;

import java.util.Locale;

import kz.zvezdochet.analytics.bean.CardType;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.TextGenderDictionaryService;

/**
 * Сервис типов космограмм
 * @author Natalie Didenko
 */
public class CardTypeService extends TextGenderDictionaryService {

	public CardTypeService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "cardtypes" : "us_cardtypes";
	}

	@Override
	public Model create() {
		return new CardType();
	}
}
