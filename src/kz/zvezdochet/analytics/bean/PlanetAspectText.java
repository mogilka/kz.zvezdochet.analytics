package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.PlanetAspectService;
import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.TextGenderModel;
import kz.zvezdochet.core.service.ModelService;

/**
 * Толкование аспекта планет
 * @author Nataly Didenko
 *
 */
public class PlanetAspectText extends TextGenderModel {
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
	 * Тип аспекта
	 */
    private AspectType type;
	/**
	 * Аспект
	 */
	private Aspect aspect;

	public Aspect getAspect() {
		return aspect;
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
	}

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

	@Override
	public ModelService getService() {
		return new PlanetAspectService();
	}

	/**
	 * Поиск знака силы аспекта
	 * @param p1 первая планета
	 * @param p2 вторая планета
	 * @return знак силы
	 */
	public String getMark(Planet p1, Planet p2) {
		String code = type.getCode();
		if (p1.isLord() && p2.isLord())
			return "\u2191";
		else if (code.equals("NEGATIVE")) {
			if (p1.isNegative() && p2.isNegative())
				return "\u2191";
			else if (p1.isPositive() && p2.isPositive())
				return "\u2193";
		} else {
			if (p1.isPositive() && p2.isPositive())
				return "\u2191";
			else if (p1.isNegative() && p2.isNegative())
				return "\u2193";
		}
		return "";
	}
}
