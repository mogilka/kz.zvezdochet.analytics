package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;

/**
 * Справочник "Планеты в знаках Зодиака"
 * @author Nataly Didenko
 *
 */
public class PlanetSignTextReference extends TextGenderReference {
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
}
