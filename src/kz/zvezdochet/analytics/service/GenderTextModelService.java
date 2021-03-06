package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.bean.TextGenderModel;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Сервис моделей с гендерной информацией
 * @author Natalie Didenko
 */
public abstract class GenderTextModelService extends ModelService {

	@Override
	public Model save(Model model) throws DataAccessException {
		TextGenderDictionary dict = (TextGenderDictionary)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + "(text, genderid) values(?,?)";
			else
				sql = "update " + tableName + " set " +
					"genderid = ?, " +
					"text = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
//			if (dict.getGenderTexts() != null)
//				ps.setLong(1, dict.getGenderTexts().getId());
//			else
//				ps.setLong(1, java.sql.Types.NULL);
			ps.setString(2, dict.getText());
			result = ps.executeUpdate();
			if (result == 1) {
				if (null == model.getId()) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        model.setId(autoIncKeyFromApi);
//					    System.out.println("inserted " + tableName + "\t" + autoIncKeyFromApi);
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
			afterSave();
		}
		return dict;
	}

	@Override
	public TextGenderModel init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		TextGenderModel type = (model != null) ? (TextGenderModel)model : (TextGenderModel)create();
		type.setId(rs.getLong("ID"));
		type.setText(rs.getString("Text"));
//		if (rs.getString("GenderID") != null) {
//			TextGender genderText = (TextGender)new TextGenderService().find(rs.getLong("GenderID"));
//			if (genderText != null)
//				type.setGenderText(genderText);
//		}
		return type;
	}
}
