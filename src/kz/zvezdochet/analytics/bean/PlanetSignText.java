package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.PlanetSignService;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.TextGenderModel;
import kz.zvezdochet.core.service.ModelService;

/**
 * Толкование планеты в знаке Зодиака
 * @author Natalie Didenko
 *
 */
public class PlanetSignText extends TextGenderModel {
	private static final long serialVersionUID = 2671603240435126965L;

	/**
	 * Категория комбинации
	 */
    private Category category;

	/**
	 * Планета
	 */
    private Planet planet;
        
	/**
	 * Знак Зодиака
	 */
    private Sign sign;

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Planet getPlanet() {
		return planet;
	}

	public void setPlanet(Planet planet) {
		this.planet = planet;
	}

	public Sign getSign() {
		return sign;
	}

	public void setSign(Sign sign) {
		this.sign = sign;
	}

	@Override
	public ModelService getService() {
		return new PlanetSignService();
	}
}
