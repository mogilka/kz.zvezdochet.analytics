package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.DegreeService;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.service.ModelService;

/**
 * Зодиакальный градус
 * @author Nataly Didenko
 */
public class Degree extends TextGenderDictionary {
	private static final long serialVersionUID = -3521775467191351633L;

	@Override
	public ModelService getService() {
		return new DegreeService();
	}

	/**
	 * Градус с позитивным толкованием
	 */
	private boolean positive;
	/**
	 * Королевский
	 */
	private boolean royal;
	/**
	 * Разрушительный
	 */
	private boolean destructive;

	public boolean isPositive() {
		return positive;
	}
	public void setPositive(boolean positive) {
		this.positive = positive;
	}
	public boolean isRoyal() {
		return royal;
	}
	public void setRoyal(boolean royal) {
		this.royal = royal;
	}
	public boolean isDestructive() {
		return destructive;
	}
	public void setDestructive(boolean destructive) {
		this.destructive = destructive;
	}
}