package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.Halfsphere;
import kz.zvezdochet.core.bean.BaseEntity;

/**
 * Реализация сервиса полусфер
 * @author nataly
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class HalfsphereService extends GenderTextDiagramService {

	public HalfsphereService() {
		tableName = "halfspheres";
	}

	@Override
	public BaseEntity createEntity() {
		return new Halfsphere();
	}
}
