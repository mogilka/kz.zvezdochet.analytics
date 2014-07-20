package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.CardKind;
import kz.zvezdochet.core.bean.Model;


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

	@Override
	public Model create() {
		return new CardKind();
	}
}
