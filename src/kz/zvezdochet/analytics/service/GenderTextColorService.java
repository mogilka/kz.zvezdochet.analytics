package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.core.bean.IColorizedObject;
import kz.zvezdochet.core.bean.IDiagramObject;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.TextGenderDictionaryService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Прототип сервиса модели, имеющей цвет
 * @author Nataly Didenko
 * //TODO удалить класс??
 */
public abstract class GenderTextColorService extends TextGenderDictionaryService {

	@Override
	public TextGenderDictionary init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		TextGenderDictionary type = (model != null) ? (TextGenderDictionary)model : (TextGenderDictionary)create();
		super.init(rs, type);
		if (type instanceof IColorizedObject)
			((IColorizedObject)type).setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		return type;
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		TextGenderDictionary dict = (TextGenderDictionary)model;
//		dict.setGenderText((TextGender)new TextGenderService().save(dict.getGenderTexts()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(text, genderid, code, name, description, color) values(?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"color = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, dict.getText());
//			if (dict.getGenderTexts() != null)
//				ps.setLong(2, dict.getGenderTexts().getId());
//			else
//				ps.setLong(2, java.sql.Types.NULL);
			ps.setString(3, dict.getCode());
			ps.setString(4, dict.getName());
			ps.setString(5, dict.getDescription());
			if (dict instanceof IColorizedObject)
				ps.setString(6, CoreUtil.colorToRGB(((IColorizedObject)dict).getColor()));
			ps.setString(7, ((IDiagramObject)dict).getDiaName());
			result = ps.executeUpdate();
			if (result == 1) {
				if (null == model.getId()) { 
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
