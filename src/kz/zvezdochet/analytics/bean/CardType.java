package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.CardTypeService;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.service.ModelService;


/**
 * Тип космограммы
 * @author Natalie Didenko
 */
public class CardType extends TextGenderDictionary {
	private static final long serialVersionUID = -9177543578300771645L;

	@Override
	public ModelService getService() {
		return new CardTypeService();
	}
}