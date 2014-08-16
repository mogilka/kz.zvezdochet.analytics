package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.Degree;
import kz.zvezdochet.core.bean.Model;

/**
 * Сервис Зодиакальных градусов
 * @author Nataly Didenko
 */
public class DegreeService extends GenderTextDictionaryService {

	public DegreeService() {
		tableName = "degrees";
	}

	@Override
	public Model create() {
		return new Degree();
	}
}
