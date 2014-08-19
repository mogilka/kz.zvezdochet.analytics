package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.analytics.bean.PlanetAspectTextDictionary;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.PlanetService;

/**
 * Сервис аспектов планет
 * @author Nataly Didenko
 */
public class PlanetAspectService extends GenderTextDictionaryService {

	public PlanetAspectService() {
		tableName = "planetaspects";
	}

	/**
	 * Поиск толкования аспекта
	 * @param planet1 первая планета
	 * @param planet2 вторая планета
	 * @param aspectType тип аспекта
	 * @return аспект между планетами
	 * @throws DataAccessException
	 */
	public Model find(Planet planet1, Planet planet2, AspectType aspectType) throws DataAccessException {
        PlanetAspectTextDictionary dict = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + 
				" where typeid = " + aspectType.getId() +
				" and planet1id = " + planet1.getId() +
				" and planet2id = " + planet2.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next())
				dict = init(rs, null);
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
		return dict;
	}

	@Override
	public List<Model> getList() throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + " order by planet1ID, planet2ID, TypeID";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				PlanetAspectTextDictionary dict = init(rs, null);
				list.add(dict);
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
		PlanetAspectTextDictionary dict = (PlanetAspectTextDictionary)model;
		dict.setGenderText((GenderText)new GenderTextService().save(dict.getGenderText()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(text, genderid, code, name, description, planet1id, planet2id, typeid) " +
					"values(?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"planet1id = ?, " +
					"planet2id = ?, " +
					"typeid = ? " +
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
			ps.setLong(6, dict.getPlanet1().getId());
			ps.setLong(7, dict.getPlanet2().getId());
			ps.setLong(8, dict.getType().getId());
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
			update();
		}
		return dict;
	}

	@Override
	public PlanetAspectTextDictionary init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		PlanetAspectTextDictionary dict = (model != null) ? (PlanetAspectTextDictionary)model : (PlanetAspectTextDictionary)create();
		super.init(rs, dict);
		PlanetService service = new PlanetService();
		dict.setPlanet1((Planet)service.find(rs.getLong("Planet1ID")));
		dict.setPlanet2((Planet)service.find(rs.getLong("Planet2ID")));
		dict.setType((AspectType)new AspectTypeService().find(rs.getLong("TypeID")));
		return dict;
	}

	@Override
	public Model create() {
		return new PlanetAspectTextDictionary();
	}
}
