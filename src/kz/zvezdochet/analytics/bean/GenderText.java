package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.core.bean.Base;
import kz.zvezdochet.core.service.BaseService;

/**
 * Класс, представляющий текстовую информацию
 * для мужского и женского пола
 * @author Nataly Didenko
 *
 */
public class GenderText extends Base {
	private static final long serialVersionUID = 7663798183215999740L;

	/**
	 * Текст для мужчин
	 */
    private String maletext;

	/**
	 * Текст для женщин
	 */
    private String femaletext;

	public String getMaletext() {
		return maletext;
	}

	public void setMaletext(String maletext) {
		this.maletext = maletext;
	}

	public String getFemaletext() {
		return femaletext;
	}

	public void setFemaletext(String femaletext) {
		this.femaletext = femaletext;
	}

	@Override
	public BaseService getService() {
		return null;
	}
}
