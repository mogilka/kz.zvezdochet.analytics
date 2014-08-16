package kz.zvezdochet.analytics.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import kz.zvezdochet.analytics.bean.Element;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Сервис стихий
 * @author Nataly Didenko
 */
public class ElementService extends GenderTextDiagramService {

	public ElementService() {
		tableName = "elements";
	}

	@Override
	public Element init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		Element element = (model != null) ? (Element)model : (Element)create();
		super.init(rs, model);
		element.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		element.setDiaName(rs.getString("Diagram"));
		return element;
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
