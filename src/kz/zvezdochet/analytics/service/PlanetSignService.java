package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.Category;
import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.analytics.bean.PlanetSignTextDictionary;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.service.SignService;

/**
 * Сервис планет в знаках Зодиака
 * @author Nataly Didenko
 */
public class PlanetSignService extends GenderTextDictionaryService {

	public PlanetSignService() {
		tableName = "planetsigns";
	}

	@Override
	public List<Model> getList() throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + " order by signID";
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
		PlanetSignTextDictionary dict = (PlanetSignTextDictionary)model;
		dict.setGenderText((GenderText)new GenderTextService().save(dict.getGenderText()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (model.getId() == null) 
				sql = "insert into " + tableName + 
					"(signid, typeid, text, genderid, code, name, description) " +
					"values(?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"signid = ?, " +
					"typeid = ?, " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, dict.getSign().getId());
			ps.setLong(2, dict.getCategory().getId());
			ps.setString(3, dict.getText());
			if (dict.getGenderText() != null)
				ps.setLong(4, dict.getGenderText().getId());
			else
				ps.setLong(4, java.sql.Types.NULL);
			ps.setString(5, dict.getCode());
			ps.setString(6, dict.getName());
			ps.setString(7, dict.getDescription());
			result = ps.executeUpdate();
			if (result == 1) {
				if (model.getId() == null) { 
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
	public PlanetSignTextDictionary init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		PlanetSignTextDictionary dict = (model != null) ? (PlanetSignTextDictionary)model : (PlanetSignTextDictionary)create();
		super.init(rs, dict);
		dict.setSign((Sign)new SignService().find(rs.getLong("SignID")));
		dict.setCategory((Category)new CategoryService().find(rs.getLong("TypeID")));
		dict.setPlanet((Planet)new PlanetService().find(dict.getCategory().getObjectId()));
		return dict;
	}

	@Override
	public Model create() {
		return new PlanetSignTextDictionary();
	}
}
