package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.analytics.bean.TextGenderDictionary;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;
import kz.zvezdochet.util.IColorizedObject;
import kz.zvezdochet.util.IDiagramObject;

/**
 * Прототип сервиса диаграммных объектов
 * @author Nataly Didenko
 */
public abstract class GenderTextDiagramService extends GenderTextDictionaryService {

	@Override
	public TextGenderDictionary init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		if (null == model)
			model = (TextGenderDictionary)create();
		super.init(rs, model);
		if (model instanceof IColorizedObject)
			((IColorizedObject)model).setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		((IDiagramObject)model).setDiaName(rs.getString("Diagram"));
		return (TextGenderDictionary)model;
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		TextGenderDictionary dict = (TextGenderDictionary)model;
		dict.setGenderText((GenderText)new GenderTextService().save(dict.getGenderText()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (model.getId() == null) 
				sql = "insert into " + tableName + 
					"(text, genderid, code, name, description, color, diagram) values(?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"color = ?, " +
					"diagram = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, dict.getText());
			if (dict.getGenderText() != null)
				ps.setLong(2, dict.getGenderText().getId());
			else
				ps.setLong(2, java.sql.Types.NULL);
			ps.setString(3, dict.getCode());
			ps.setString(4, dict.getName());
			ps.setString(5, dict.getDescription());
			if (dict instanceof IColorizedObject)
				ps.setString(6, CoreUtil.colorToRGB(((IColorizedObject)dict).getColor()));
			ps.setString(7, ((IDiagramObject)dict).getDiaName());
			result = ps.executeUpdate();
			if (result == 1) {
				if (model.getId() == null) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        model.setId(autoIncKeyFromApi);
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
		return dict;
	}
}
