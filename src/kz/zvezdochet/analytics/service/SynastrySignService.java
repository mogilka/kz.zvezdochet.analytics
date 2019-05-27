package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.SynastryText;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.service.SignService;

/**
 * Сервис синастрических планет
 * @author Natalie Didenko
 */
public class SynastrySignService extends ModelService {

	public SynastrySignService() {
		tableName = "synastrysigns";
	}
	
	@Override
	public List<Model> getList() throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + " order by planetID, sign1ID, sign2ID";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next())
				list.add(init(rs, null));
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
		SynastryText dict = (SynastryText)model;
		//dict.setGenderText((TextGender)new TextGenderService().save(dict.getGenderTexts()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(text, genderid, sign1id, sign2id, planetid) " +
					"values(?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"sign1id = ?, " +
					"sign2id = ?, " +
					"planetid = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, dict.getText());
//			if (dict.getGenderTexts() != null)
//				ps.setLong(2, dict.getGenderTexts().getId());
//			else
//				ps.setLong(2, java.sql.Types.NULL);
			ps.setLong(3, dict.getSign1().getId());
			ps.setLong(4, dict.getSign2().getId());
			ps.setLong(5, dict.getPlanet().getId());
			result = ps.executeUpdate();
			if (result == 1) {
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
	public SynastryText init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		SynastryText dict = (model != null) ? (SynastryText)model : (SynastryText)create();
		SignService service = new SignService();
		dict.setSign1((Sign)service.find(rs.getLong("Sign1ID")));
		dict.setSign2((Sign)service.find(rs.getLong("Sign2ID")));
		dict.setPlanet((Planet)new PlanetService().find(rs.getLong("PlanetID")));
		dict.setText(rs.getString("Text"));
		return dict;
	}

	@Override
	public Model create() {
		return new SynastryText();
	}

	/**
	 * Поиск толкования синастрии по планетам в знаках
	 * @param planet планета
	 * @param sign знак первого партнёра
	 * @param sign2 знак второго партнёра
	 * @return толкование
	 * @throws DataAccessException
	 */
	public SynastryText find(Planet planet, Sign sign, Sign sign2) throws DataAccessException {
        SynastryText model = new SynastryText();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName +
				" where planetid = ? " +
					"and sign1id = ? " +
					"and sign2id = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, planet.getId());
			ps.setLong(2, sign.getId());
			ps.setLong(3, sign2.getId());
			rs = ps.executeQuery();
			if (rs.next()) 
				init(rs, model);
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
