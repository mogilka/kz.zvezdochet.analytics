package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.PlanetAspectService;
import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.TextGenderModel;
import kz.zvezdochet.core.service.ModelService;

/**
 * Толкование аспекта планет
 * @author Natalie Didenko
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
	 * @return знак силы
	 */
	public String getMark() {
		String code = type.getCode();
		if (planet1.isDominant() && planet2.isDominant())
			return "\u2191";
		else if (code.equals("NEGATIVE")) {
			if (planet1.isNegative() && planet2.isNegative())
				return "\u2191";
			else if (planet1.isPositive() && planet2.isPositive())
				return "\u2193";
		} else {
			if (planet1.isPositive() && planet2.isPositive())
				return "\u2191";
			else if (planet1.isNegative() && planet2.isNegative())
				return "\u2193";
		}
		return "";
	}

	/**
	 * Толкование транзита
	 */
	private String description;
	/**
	 * Краткое толкование транзита
	 */
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
