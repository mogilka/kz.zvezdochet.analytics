package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.PlanetHouseRuleService;
import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.service.ModelService;

/**
 * Толкование планет в астрологических домах
 * @author Natalie Didenko
 */
public class PlanetHouseRule extends PlanetHouseText {
	private static final long serialVersionUID = 2490951071506168720L;

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

	/**
	 * Знак планеты или куспида
	 */
	private Sign sign;

	/**
	 * Признак, по которому определяется толкование знака: 0|1|2 планета|дом|планета2
	 */
	private int signOwner;

	public Sign getSign() {
		return sign;
	}

	public void setSign(Sign sign) {
		this.sign = sign;
	}

	public int getSignOwner() {
		return signOwner;
	}

	public void setSignOwner(int houseSign) {
		this.signOwner = houseSign;
	}

	@Override
	public String toString() {
		String res = planet.getCode() + " in " + house.getCode();
		if (aspectType != null)
			res += " " + aspectType.getCode();
		if (planet2 != null)
			res += " " + planet2.getCode();
		if (house2 != null)
			res += " " + house2.getCode();
		if (sign != null)
			res += " " + sign.getCode();
		return res;
	}
}
