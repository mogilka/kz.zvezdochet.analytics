package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.PlanetTextService;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.TextGenderModel;
import kz.zvezdochet.core.service.ModelService;

/**
 * Толкование планеты
 * @author Nataly Didenko
 */
public class PlanetText extends TextGenderModel {
	private static final long serialVersionUID = -8328248201235163517L;

	/**
	 * Планета
	 */
    private Planet planet;
    /**
     * Тип
     */
    private String type;

	public ModelService getService() {
		return new PlanetTextService();
	}

	public Planet getPlanet() {
		return planet;
	}

	public void setPlanet(Planet planet) {
		this.planet = planet;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
