package kz.zvezdochet.analytics.service;

import java.util.List;

import kz.zvezdochet.analytics.bean.Halfsphere;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;


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

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}
}
