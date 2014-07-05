package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.InYan;
import kz.zvezdochet.core.bean.BaseEntity;

/**
 * Реализация сервиса Инь-Ян
 * @author nataly
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class InYanService extends GenderTextDiagramService {

	public InYanService() {
		tableName = "inyan";
	}

	@Override
	public BaseEntity createEntity() {
		return new InYan();
	}
}
