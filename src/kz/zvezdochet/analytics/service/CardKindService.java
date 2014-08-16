package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.CardKind;
import kz.zvezdochet.core.bean.Model;


/**
 * Сервис вида космограммы
 * @author Nataly Didenko
 */
public class CardKindService extends GenderTextDictionaryService {

	public CardKindService() {
		tableName = "cardkinds";
	}

	@Override
	public Model create() {
		return new CardKind();
	}
}
