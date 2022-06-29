package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.HouseSignRuleService;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.TextGenderModel;
import kz.zvezdochet.core.service.ModelService;

/**
 * Правило дома в знаке Зодиака
 * @author Natalie Didenko
 *
 */
public class HouseSignRule extends TextGenderModel {
	private static final long serialVersionUID = 6790363390753400482L;

	/**
	 * Планета
	 */
    private Planet planet;
        
	public Planet getPlanet() {
		return planet;
	}

	public void setPlanet(Planet planet) {
		this.planet = planet;
	}

	/**
	 * Знак Зодиака
	 */
    private Sign sign2;

	public Sign getSign2() {
		return sign2;
	}

	public void setSign2(Sign sign) {
		this.sign2 = sign;
	}

	@Override
	public ModelService getService() {
		return new HouseSignRuleService();
	}
}
