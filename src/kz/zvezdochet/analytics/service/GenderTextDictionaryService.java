package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.analytics.bean.TextGenderDictionary;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Прототип сервиса простого справочника
 * @author Nataly Didenko
 */
public abstract class GenderTextDictionaryService extends DictionaryService {

	@Override
	public Model save(Model model) throws DataAccessException {
		TextGenderDictionary dict = (TextGenderDictionary)model;
		dict.setGenderText((GenderText)new GenderTextService().save(dict.getGenderText()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(text, genderid, code, name, description) values(?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ? " +
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

	@Override
	public TextGenderDictionary init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		TextGenderDictionary type = (model != null) ? (TextGenderDictionary)model : (TextGenderDictionary)create();
		super.init(rs, model);
		type.setText(rs.getString("Text"));
		if (rs.getString("GenderID") != null) {
			GenderText genderText = (GenderText)new GenderTextService().find(rs.getLong("GenderID"));
			if (genderText != null)
				type.setGenderText(genderText);
		}
		return type;
	}

	@Override
	public Model create() {
		return new TextGenderDictionary();
	}
}
