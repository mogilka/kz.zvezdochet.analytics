package kz.zvezdochet.analytics.service;


/**
 * Реализация сервиса справочника Планеты в синастрических домах
 * @author Nataly Didenko
 *
 * @see PlanetHouseService Реализация интерфейса сервиса планет в домах  
 */
public class SynastryHouseService extends PlanetHouseService {

	public SynastryHouseService() {
		tableName = "synastryhouses";
	}
}
