package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import kz.zvezdochet.analytics.bean.Numerology;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Сервис нумерологии
 * @author Natalie Didenko
 */
public class NumerologyService extends ModelService {

	public NumerologyService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "numerology" : "us_numerology";
	}

	@Override
	public Model create() {
		return new Numerology();
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model init(ResultSet rs, Model base) throws DataAccessException, SQLException {
		Numerology type = (base != null) ? (Numerology)base : (Numerology)create();
		type.setId(rs.getLong("id"));
		type.setNumber(rs.getInt("number"));
		type.setYeartext(rs.getString("yeartext"));
		type.setImage(rs.getString("image"));
		type.setDescription(rs.getString("description"));
		type.setZoroastrsyn(rs.getString("zoroastrsyn"));
		type.setBirth(rs.getString("birth"));
		type.setName(rs.getString("name"));
		return type;
	}

	/**
	 * Поиск по числу
	 * @param number число
	 * @return толкование
	 * @throws DataAccessException
	 */
	public Model find(int number) throws DataAccessException {
		Model model = create();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName + " where number = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, number);
			rs = ps.executeQuery();
			if (rs.next()) 
				return init(rs, model);
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
		return null;
	}
}
