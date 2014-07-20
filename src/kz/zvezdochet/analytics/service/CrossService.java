package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.Cross;
import kz.zvezdochet.core.bean.Model;


/**
 * Реализация сервиса крестов
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class CrossService extends GenderTextDiagramService {

	public CrossService() {
		tableName = "crosses";
	}

	@Override
	public Model create() {
		return new Cross();
	}
}
