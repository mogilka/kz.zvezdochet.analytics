package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.analytics.bean.TextGenderReference;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;
import kz.zvezdochet.util.IColorizedObject;
import kz.zvezdochet.util.IDiagramObject;

/**
 * Прототип реализации сервиса диаграммных объектов
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Прототип реализации сервиса простого справочника  
 */
public abstract class GenderTextDiagramService extends GenderTextReferenceService {

	@Override
	public TextGenderReference init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		if (null == model)
			model = (TextGenderReference)create();
		super.init(rs, model);
		if (model instanceof IColorizedObject)
			((IColorizedObject)model).setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		((IDiagramObject)model).setDiaName(rs.getString("Diagram"));
		return (TextGenderReference)model;
	}

	@Override
	public Model save(Model element) throws DataAccessException {
		TextGenderReference reference = (TextGenderReference)element;
		reference.setGenderText((GenderText)new GenderTextService().save(reference.getGenderText()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(text, genderid, code, name, description, color, diagram) values(?,?,?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"color = ?, " +
					"diagram = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setString(1, reference.getText());
			if (reference.getGenderText() != null)
				ps.setLong(2, reference.getGenderText().getId());
			else
				ps.setLong(2, java.sql.Types.NULL);
			ps.setString(3, reference.getCode());
			ps.setString(4, reference.getName());
			ps.setString(5, reference.getDescription());
			if (reference instanceof IColorizedObject)
				ps.setString(6, CoreUtil.colorToRGB(((IColorizedObject)reference).getColor()));
			ps.setString(7, ((IDiagramObject)reference).getDiaName());
			result = ps.executeUpdate();
			if (result == 1) {
				if (element.getId() == null) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        element.setId(autoIncKeyFromApi);
					    //System.out.println("inserted " + tableName + "\t" + autoIncKeyFromApi);
					}
					if (rsid != null) rsid.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)	ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			update();
		}
		return reference;
	}
}
