package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.Degree;
import kz.zvezdochet.core.bean.Model;



/**
 * Реализация сервиса Зодиакальных градусов
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class DegreeService extends GenderTextReferenceService {

	public DegreeService() {
		tableName = "degrees";
	}

	@Override
	public Model create() {
		return new Degree();
	}
}
