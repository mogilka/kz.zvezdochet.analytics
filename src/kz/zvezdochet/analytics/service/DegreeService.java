package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

import kz.zvezdochet.analytics.bean.Degree;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.TextGenderDictionaryService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.service.PlanetService;

/**
 * Сервис Зодиакальных градусов
 * @author Natalie Didenko
 */
public class DegreeService extends TextGenderDictionaryService {

	public DegreeService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "degrees" : "us_degrees";
	}

	@Override
	public Model create() {
		return new Degree();
	}

	@Override
	public Degree init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		Degree dict = (model != null) ? (Degree)model : (Degree)create();
		dict = (Degree)super.init(rs, model);
		dict.setPositive(rs.getBoolean("positive"));
		dict.setRoyal(rs.getBoolean("royal"));
		dict.setDestructive(rs.getBoolean("destructive"));
		dict.setOccult(rs.getBoolean("occult"));
		PlanetService service = new PlanetService();
		dict.setPlanet((Planet)service.find(rs.getLong("planetid")));
		long val = rs.getLong("planet2id");
		if (val > 0)
			dict.setPlanet2((Planet)service.find(val));
		val = rs.getLong("planet3id");
		if (val > 0)
			dict.setPlanet3((Planet)service.find(val));
		return dict;
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		Degree dict = (Degree)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(text, code, name, description, positive, royal, destructive, date) values(?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"text = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"positive = ?, " +
					"royal, " +
					"destructive, " +
					"date " +
					"where id = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, dict.getText());
			ps.setString(2, dict.getCode());
			ps.setString(3, dict.getName());
			ps.setString(4, dict.getDescription());
			ps.setInt(5, dict.isPositive() ? 1 : 0);
			ps.setInt(6, dict.isRoyal() ? 1 : 0);
			ps.setInt(7, dict.isDestructive() ? 1 : 0);
			ps.setString(8, DateUtil.formatCustomDateTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
			if (model.getId() != null)
				ps.setLong(9, model.getId());
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
}
