package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.PlanetTextService;
import kz.zvezdochet.core.bean.TextGenderModel;
import kz.zvezdochet.core.service.ModelService;

/**
 * Толкование планеты
 * @author Nataly Didenko
 */
public class PlanetText extends TextGenderModel {
	private static final long serialVersionUID = -8328248201235163517L;

	/**
	 * Идентификатор планеты
	 */
    private long planetid;
    /**
     * Тип
     */
    private String type;

	public ModelService getService() {
		return new PlanetTextService();
	}

	public long getPlanetid() {
		return planetid;
	}

	public void setPlanetid(long planetid) {
		this.planetid = planetid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
