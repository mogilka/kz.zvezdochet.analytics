package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.Degree;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.TextGenderDictionaryService;

/**
 * Сервис Зодиакальных градусов
 * @author Nataly Didenko
 */
public class DegreeService extends TextGenderDictionaryService {

	public DegreeService() {
		tableName = "degrees";
	}

	@Override
	public Model create() {
		return new Degree();
	}
}
