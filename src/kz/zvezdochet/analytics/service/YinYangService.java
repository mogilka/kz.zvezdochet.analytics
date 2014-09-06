package kz.zvezdochet.analytics.service;

import java.util.List;

import kz.zvezdochet.analytics.bean.YinYang;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;

/**
 * Сервис Инь-Ян
 * @author Nataly Didenko
 */
public class YinYangService extends GenderTextDiagramService {

	public YinYangService() {
		tableName = "yinyang";
	}

	@Override
	public Model create() {
		return new YinYang();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}
}
