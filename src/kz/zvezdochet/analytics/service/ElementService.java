package kz.zvezdochet.analytics.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.analytics.bean.Element;
import kz.zvezdochet.core.bean.Base;
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
	public Element init(ResultSet rs, Base base) throws DataAccessException, SQLException {
		Element element = new Element();
		super.init(rs, element);
		element.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		element.setDiaName(rs.getString("Diagram"));
		return element;
	}
}
