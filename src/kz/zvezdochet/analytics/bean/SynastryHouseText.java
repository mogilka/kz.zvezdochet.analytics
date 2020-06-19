package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.SynastryHouseService;
import kz.zvezdochet.core.service.ModelService;

/**
 * Толкование планеты партнёра в синастрическом доме
 * @author Natalie Didenko
 *
 */
public class SynastryHouseText extends PlanetHouseText {
	private static final long serialVersionUID = -2548423592834590887L;

	@Override
	public ModelService getService() {
		return new SynastryHouseService();
	}

	/**
	 * Уровень критичности аспекта
	 */
	private int level;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
