package kz.zvezdochet.analytics.service;

import java.util.List;

import kz.zvezdochet.analytics.bean.Square;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;

/**
 * Сервис крестов
 * @author Nataly Didenko
 */
public class SquareService extends GenderTextDiagramService {

	public SquareService() {
		tableName = "squares";
	}

	@Override
	public Model create() {
		return new Square();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}
}
