package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.CardKindService;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.service.ModelService;


/**
 * Вид космограммы
 * @author Nataly Didenko
 */
public class CardKind extends TextGenderDictionary {
	private static final long serialVersionUID = 7203713223993014957L;

	@Override
	public ModelService getService() {
		return new CardKindService();
	}

	/**
	 * Направление рисунка up|down|left|right (для лука и т.п.)
	 */
	private String direction;

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
}