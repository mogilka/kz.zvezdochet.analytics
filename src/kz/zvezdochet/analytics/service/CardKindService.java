package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.CardKind;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.TextGenderDictionaryService;


/**
 * Сервис вида космограммы
 * @author Nataly Didenko
 */
public class CardKindService extends TextGenderDictionaryService {

	public CardKindService() {
		tableName = "cardkinds";
	}

	@Override
	public Model create() {
		return new CardKind();
	}
}
