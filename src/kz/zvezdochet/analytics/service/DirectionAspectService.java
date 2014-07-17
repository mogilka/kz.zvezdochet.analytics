package kz.zvezdochet.analytics.service;


/**
 * Реализация сервиса справочника Дирекции планет
 * @author Nataly Didenko
 *
 * @see PlanetAspectService Реализация сервиса аспектов планет  
 */
public class DirectionAspectService extends PlanetAspectService {

	public DirectionAspectService() {
		tableName = "directionaspects";
	}
}
