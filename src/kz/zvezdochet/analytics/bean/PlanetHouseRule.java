package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.PlanetHouseRuleService;
import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.service.ModelService;

/**
 * Толкование планет в астрологических домах
 * @author Natalie Didenko
 */
public class PlanetHouseRule extends PlanetHouseText {

	/**
	 * Планета
	 */
    private Planet planet2;
        
	/**
	 * Астрологический дом
	 */
    private House house2;

	/**
	 * Аспект
	 */
    private Aspect aspect;

	public House getHouse2() {
		return house2;
	}

	public void setHouse2(House house) {
		this.house2 = house;
	}

	public Planet getPlanet2() {
		return planet2;
	}

	public void setPlanet2(Planet planet) {
		this.planet2 = planet;
	}

	public Aspect getAspect() {
		return aspect;
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
	}

	@Override
	public ModelService getService() {
		return new PlanetHouseRuleService();
	}
}
