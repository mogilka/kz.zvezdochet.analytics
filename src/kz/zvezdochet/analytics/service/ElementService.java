package kz.zvezdochet.analytics.service;

import java.sql.ResultSet;
import java.sql.SQLException;

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
	public Element init(ResultSet rs, Model base) throws DataAccessException, SQLException {
		Element element = (Element)create();
		super.init(rs, element);
		element.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		element.setDiaName(rs.getString("Diagram"));
		return element;
	}

	@Override
	public Model create() {
		return new Element();
	}
}
