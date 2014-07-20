package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.Halfsphere;
import kz.zvezdochet.core.bean.Model;


/**
 * Реализация сервиса полусфер
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class HalfsphereService extends GenderTextDiagramService {

	public HalfsphereService() {
		tableName = "halfspheres";
	}

	@Override
	public Model create() {
		return new Halfsphere();
	}
}
