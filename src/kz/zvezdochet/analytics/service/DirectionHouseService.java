package kz.zvezdochet.analytics.service;


/**
 * Реализация сервиса справочника Дирекции планет по астрологическим домам
 * @author nataly
 *
 * @see PlanetHouseService Реализация интерфейса сервиса планет в домах  
 */
public class DirectionHouseService extends PlanetHouseService {

	public DirectionHouseService() {
		tableName = "directionhouses";
	}
}
