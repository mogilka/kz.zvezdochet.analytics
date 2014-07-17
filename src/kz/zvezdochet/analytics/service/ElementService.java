package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.Element;
import kz.zvezdochet.core.bean.Base;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
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
	public List<Base> getList() throws DataAccessException {
        List<Base> list = new ArrayList<Base>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " order by name";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				Element element = init(rs);
				list.add(element);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { 
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (SQLException e) { 
				e.printStackTrace(); 
			}
		}
		return list;
	}

	@Override
	public Element init(ResultSet rs) throws DataAccessException, SQLException {
		Element element = (Element)super.init(rs);
		element.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		element.setDiaName(rs.getString("Diagram"));
		return element;
	}

	@Override
	public Base create() {
		return new Element();
	}
}
