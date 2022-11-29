package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.DegreeService;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.service.ModelService;

/**
 * Зодиакальный градус
 * @author Natalie Didenko
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

	/**
	 * Оккультный
	 */
	private boolean occult;

	public boolean isOccult() {
		return occult;
	}
	public void setOccult(boolean occult) {
		this.occult = occult;
	}
	private Planet planet;
	private Planet planet2;
	private Planet planet3;

	public Planet getPlanet() {
		return planet;
	}
	public void setPlanet(Planet planet) {
		this.planet = planet;
	}
	public Planet getPlanet2() {
		return planet2;
	}
	public void setPlanet2(Planet planet2) {
		this.planet2 = planet2;
	}
	public Planet getPlanet3() {
		return planet3;
	}
	public void setPlanet3(Planet planet3) {
		this.planet3 = planet3;
	}
}
