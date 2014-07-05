package kz.zvezdochet.analytics.service;


/**
 * Реализация сервиса справочника Синастрические аспекты планет
 * @author nataly
 *
 * @see PlanetAspectService Реализация интерфейса сервиса аспектов планет  
 */
public class SynastryAspectService extends PlanetAspectService {

	public SynastryAspectService() {
		tableName = "synastryaspects";
	}
}
