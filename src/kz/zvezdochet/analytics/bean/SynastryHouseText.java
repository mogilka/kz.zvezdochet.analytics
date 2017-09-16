package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.SynastryHouseService;
import kz.zvezdochet.core.service.ModelService;

/**
 * Толкование планеты партнёра в синастрическом доме
 * @author Nataly Didenko
 *
 */
public class SynastryHouseText extends PlanetHouseText {
	private static final long serialVersionUID = -2548423592834590887L;

	@Override
	public ModelService getService() {
		return new SynastryHouseService();
	}
}
