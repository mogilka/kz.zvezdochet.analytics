package kz.zvezdochet.analytics.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import kz.zvezdochet.analytics.bean.Element;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Реализация сервиса стихий
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Реализация сервиса простого справочника  
 */
public class ElementService extends GenderTextDiagramService {

	public ElementService() {
		tableName = "elements";
	}

	@Override
	public Element init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		if (null == model)
			model = (Element)create();
		super.init(rs, model);
		((Element)model).setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		((Element)model).setDiaName(rs.getString("Diagram"));
		return (Element)model;
	}

	@Override
	public Model create() {
		return new Element();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}
}
