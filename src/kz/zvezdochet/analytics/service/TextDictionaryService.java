package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.bean.TextDictionary;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.service.IDictionaryService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Сервис справочников с расширенной текстовой информацией
 * @author Nataly Didenko
 */
public class TextDictionaryService extends DictionaryService implements IDictionaryService {

	@Override
	public Model save(Model model) throws DataAccessException {
		TextDictionary dict = (TextDictionary)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (model.getId() == null) 
				sql = "insert into " + tableName + "(code, name, description, text) values(?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"text = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, dict.getCode());
			ps.setString(2, dict.getName());
			ps.setString(3, dict.getDescription());
			ps.setString(4, dict.getText());
			result = ps.executeUpdate();
			if (result == 1) {
				if (model.getId() == null) { 
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
			update();
		}
		return dict;
	}

	@Override
	public TextDictionary init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		TextDictionary type = (model != null) ? (TextDictionary)model : (TextDictionary)create();
		super.init(rs, type);
		type.setText(rs.getString("Text"));
		return type;
	}

	@Override
	public Model create() {
		return new TextDictionary();
	}
}
