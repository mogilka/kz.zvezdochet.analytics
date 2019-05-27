package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.Category;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.PlanetService;

/**
 * Сервис категорий
 * @author Natalie Didenko
 */
public class CategoryService extends DictionaryService {

	public CategoryService() {
		tableName = "categories";
	}

	@Override
	public List<Model> getList() throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + " order by objectid, priority";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Category category = init(rs, null);
				list.add(category);
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
	public Model save(Model model) throws DataAccessException {
		Category dict = (Category)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(priority, objectid, code, name, description) values(?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"priority = ?, " +
					"objectid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setInt(1, dict.getPriority());
			ps.setLong(2, dict.getObjectId());
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
			afterSave();
		}
		return dict;
	}

	@Override
	public Category init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		Category type = (model != null) ? (Category)model : (Category)create();
		super.init(rs, type);
		type.setPriority(rs.getInt("Priority"));
		type.setObjectId(rs.getLong("ObjectID"));
		type.setPlanet((Planet)new PlanetService().find(type.getObjectId()));
		return type;
	}

	@Override
	public Model create() {
		return new Category();
	}
}
