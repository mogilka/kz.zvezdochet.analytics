package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kz.zvezdochet.analytics.bean.Category;
import kz.zvezdochet.analytics.bean.PlanetSignText;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.service.SignService;

/**
 * Сервис планет в знаках Зодиака
 * @author Natalie Didenko
 */
public class PlanetSignService extends GenderTextModelService {

	public PlanetSignService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "planetsigns" : "us_planetsigns";
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
		PlanetSignText dict = (PlanetSignText)model;
//		dict.setGenderText((TextGender)new TextGenderService().save(dict.getGenderTexts()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(signid, typeid, text, genderid) " +
					"values(?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"signid = ?, " +
					"typeid = ?, " +
					"text = ?, " +
					"genderid = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, dict.getSign().getId());
			ps.setLong(2, dict.getCategory().getId());
			ps.setString(3, dict.getText());
//			if (dict.getGenderTexts() != null)
//				ps.setLong(4, dict.getGenderTexts().getId());
//			else
//				ps.setLong(4, java.sql.Types.NULL);
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
	public PlanetSignText init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		PlanetSignText dict = (model != null) ? (PlanetSignText)model : (PlanetSignText)create();
		dict = (PlanetSignText)super.init(rs, model);
		dict.setSign((Sign)new SignService().find(rs.getLong("SignID")));
		dict.setCategory((Category)new CategoryService().find(rs.getLong("TypeID")));
		dict.setPlanet((Planet)new PlanetService().find(dict.getCategory().getObjectId()));
		return dict;
	}

	@Override
	public Model create() {
		return new PlanetSignText();
	}

	/**
	 * Поиск толкования планеты в знаке Зодиака
	 * @param planet планета
	 * @param sign знак
	 * @return толкование
	 * @throws DataAccessException
	 */
	public List<PlanetSignText> find(Planet planet, Sign sign) throws DataAccessException {
        List<PlanetSignText> list = new ArrayList<PlanetSignText>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName + " ps " +
				"inner join " + new CategoryService().getTableName() + " c on ps.typeid = c.id " +
				"where ps.signid = ? " +
					"and c.objectid = ? " +
				"order by c.priority";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, sign.getId());
			ps.setLong(2, planet.getId());
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
/*
select categories.Name, categories.Code, 
planetsigns.Text, textgender.Male, textgender.Female, textgender.child
from planetsigns 
inner join categories on planetsigns.TypeID = categories.id
left join textgender on planetsigns.GenderID = textgender.id
where planetsigns.SignID = 6
and categories.ObjectID = 23
order by categories.Priority
*/
	}

	/**
	 * Поиск толкования планеты в знаке Зодиака
	 * @param category категория
	 * @param sign знак Зодиака
	 * @return толкование
	 * @throws DataAccessException
	 */
	public PlanetSignText find(Category category, Sign sign) throws DataAccessException {
        PlanetSignText model = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName + 
				" where signid = ? " +
					"and typeid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, sign.getId());
			ps.setLong(2, category.getId());
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
