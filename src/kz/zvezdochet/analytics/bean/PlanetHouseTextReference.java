package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;

/**
 * Справочник "Планеты в астрологических домах"
 * @author Nataly Didenko
 *
 */
public class PlanetHouseTextReference extends TextGenderReference {
	private static final long serialVersionUID = 743166962242413913L;

	/**
	 * Планета
	 */
    private Planet planet;
        
	/**
	 * Астрологический дом
	 */
    private House house;

	/**
	 * Тип аспекта
	 */
    private AspectType aspectType;

	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
	}

	public Planet getPlanet() {
		return planet;
	}

	public void setPlanet(Planet planet) {
		this.planet = planet;
	}

	public AspectType getAspectType() {
		return aspectType;
	}

	public void setAspectType(AspectType aspectType) {
		this.aspectType = aspectType;
	}
}
