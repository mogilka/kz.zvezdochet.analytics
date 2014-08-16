package kz.zvezdochet.analytics.service;

import java.util.List;

import kz.zvezdochet.analytics.bean.InYan;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;

/**
 * Сервис Инь-Ян
 * @author Nataly Didenko
 */
public class InYanService extends GenderTextDiagramService {

	public InYanService() {
		tableName = "inyan";
	}

	@Override
	public Model create() {
		return new InYan();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}
}
