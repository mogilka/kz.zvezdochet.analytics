package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.bean.TextDictionary;

/**
 * Прототип справочника, содержащего толкования
 * для мужского и женского пола
 * @author Nataly Didenko
 *
 */
public class TextGenderDictionary extends TextDictionary {
	private static final long serialVersionUID = 4426110756083980222L;

	/**
	 * Толкование для обоих полов
	 */
    private GenderText genderText;

	public GenderText getGenderText() {
		return genderText;
	}

	public void setGenderText(GenderText genderText) {
		this.genderText = genderText;
	}
}
