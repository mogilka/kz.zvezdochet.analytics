package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.bean.TextReference;

/**
 * Прототип справочника, содержащего толкования
 * для мужского и женского пола
 * @author Nataly
 *
 */
public class TextGenderReference extends TextReference {
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
