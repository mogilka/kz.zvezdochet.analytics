package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import kz.zvezdochet.analytics.bean.PlanetText;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.PlanetService;

/**
 * Сервис планет в знаках Зодиака
 * @author Natalie Didenko
 */
public class PlanetTextService extends GenderTextModelService {

	public PlanetTextService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "planettext" : "us_planettext";
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		PlanetText dict = (PlanetText)model;
//		TextGender genderText = dict.getGenderTexts();
//		if (genderText != null)
//			dict.setGenderText((TextGender)new TextGenderService().save(genderText));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + " values(0,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"planetid = ?, " +
					"type = ?, " +
					"text = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, dict.getPlanet().getId());
			ps.setString(2, dict.getType());
			ps.setString(3, dict.getText());
			result = ps.executeUpdate();
			if (1 == result) {
				if (null == model.getId()) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        model.setId(autoIncKeyFromApi);
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
			afterSave();
		}
		return dict;
	}

	@Override
	public PlanetText init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		PlanetText dict = (model != null) ? (PlanetText)model : (PlanetText)create();
		dict = (PlanetText)super.init(rs, model);
		dict.setPlanet((Planet)new PlanetService().find(rs.getLong("planetid")));
		dict.setType(rs.getString("type"));
		dict.setTextDamaged(rs.getString("text_damaged"));
		dict.setUrl(rs.getString("url"));
		return dict;
	}

	@Override
	public Model create() {
		return new PlanetText();
	}

	/**
	 * Поиск толкования планеты
	 * @param planetid идентификатор планеты
	 * @param type тип толкования
	 * @return толкование планеты
	 * @throws DataAccessException
	 */
	public Model findByPlanet(Long planetid, String type) throws DataAccessException {
		if (null == planetid || null == type) return null;
		Model model = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName + " where planetid = ? and type = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, planetid);
			ps.setString(2, type);
			rs = ps.executeQuery();
			if (rs.next()) 
				model = init(rs, null);
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
		return model;
	}
}
