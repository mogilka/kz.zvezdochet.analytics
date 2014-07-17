package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Planet;

/**
 * Справочник "Аспекты планет"
 * @author Nataly Didenko
 *
 */
public class PlanetAspectTextReference extends TextGenderReference {
	private static final long serialVersionUID = -1987115320328881211L;

	/**
	 * Первая планета
	 */
    private Planet planet1;
        
	/**
	 * Вторая планета
	 */
    private Planet planet2;
        
	/**
	 * Тип отношения
	 */
    private AspectType type;

	public AspectType getType() {
		return type;
	}

	public Planet getPlanet1() {
		return planet1;
	}

	public void setPlanet1(Planet planet1) {
		this.planet1 = planet1;
	}

	public Planet getPlanet2() {
		return planet2;
	}

	public void setPlanet2(Planet planet2) {
		this.planet2 = planet2;
	}

	public void setType(AspectType type) {
		this.type = type;
	}
}
