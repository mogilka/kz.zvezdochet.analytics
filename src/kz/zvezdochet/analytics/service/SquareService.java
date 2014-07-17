package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.Square;
import kz.zvezdochet.core.bean.Base;

/**
 * Реализация сервиса крестов
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class SquareService extends GenderTextDiagramService {

	public SquareService() {
		tableName = "squares";
	}

	@Override
	public Base create() {
		return new Square();
	}
}
