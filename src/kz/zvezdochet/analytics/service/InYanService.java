package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.InYan;
import kz.zvezdochet.core.bean.Base;

/**
 * Реализация сервиса Инь-Ян
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class InYanService extends GenderTextDiagramService {

	public InYanService() {
		tableName = "inyan";
	}

	@Override
	public Base create() {
		return new InYan();
	}
}
