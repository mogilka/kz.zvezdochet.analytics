package kz.zvezdochet.analytics.service;

import java.util.List;

import kz.zvezdochet.analytics.bean.Zone;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;


/**
 * Реализация сервиса зон
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public class ZoneService extends GenderTextDiagramService {

	public ZoneService() {
		tableName = "zones";
	}

	@Override
	public Model create() {
		return new Zone();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}
}
