package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.SynastryHouseText;
import kz.zvezdochet.core.bean.Model;

/**
 * Сервис планет в синастрических домах
 * @author Natalie Didenko
 */
public class SynastryHouseService extends PlanetHouseService {

	public SynastryHouseService() {
		tableName = "synastryhouses";
	}

	@Override
	public Model create() {
		return new SynastryHouseText();
	}
}
