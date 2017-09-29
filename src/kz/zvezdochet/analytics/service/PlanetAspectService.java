package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.PlanetAspectText;
import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.AspectService;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.PlanetService;

/**
 * Сервис аспектов планет
 * @author Nataly Didenko
 */
public class PlanetAspectService extends GenderTextModelService {

	public PlanetAspectService() {
		tableName = "planetaspects";
	}

	/**
	 * Поиск толкования аспекта
	 * @param planet1 первая планета
	 * @param planet2 вторая планета
	 * @param aspect аспект
	 * @return аспект между планетами
	 * @throws DataAccessException
	 */
	public Model find(Planet planet1, Planet planet2, Aspect aspect) throws DataAccessException {
        PlanetAspectText dict = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			AspectTypeService service = new AspectTypeService();
			String pcode = planet1.getCode();
			AspectType aspectType = aspect.getType();
			if (null == aspectType) {
				if (planet1.isDamaged() || planet1.isLilithed() || pcode.equals("Lilith"))
					aspectType = (AspectType)service.find("NEGATIVE");
				else
					aspectType = (AspectType)service.find("NEUTRAL");
			}
			if (aspectType.getCode().equals("NEUTRAL")) {
				String pcode2 = planet2.getCode();
				if (pcode.equals("Lilith") || pcode.equals("Kethu") ||
						pcode2.equals("Lilith") || pcode2.equals("Kethu"))
					aspectType = (AspectType)service.find("NEGATIVE");
				else if (pcode.equals("Selena") || pcode.equals("Sun")
						|| pcode.equals("Moon") || pcode.equals("Rakhu")
						|| pcode.equals("Mercury") || pcode.equals("Venus")
						|| pcode.equals("Jupiter") || pcode.equals("Proserpina"))
					aspectType = (AspectType)service.find("POSITIVE");
			}
			sql = "select * from " + tableName + 
				" where typeid = ?" +
					" and ((planet1id = ? and planet2id = ?)" +
						" or (planet1id = ? and planet2id = ?))" +
					" and (aspectid is null" +
						" or aspectid = ?)";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, aspectType.getId());
			ps.setLong(2, planet1.getId());
			ps.setLong(3, planet2.getId());
			ps.setLong(4, planet2.getId());
			ps.setLong(5, planet1.getId());
			ps.setLong(6, aspect.getId());
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
				PlanetAspectText dict = init(rs, null);
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
		PlanetAspectText dict = (PlanetAspectText)model;
		//dict.setGenderText((TextGender)new TextGenderService().save(dict.getGenderTexts()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(text, genderid, planet1id, planet2id, typeid) " +
					"values(?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"planet1id = ?, " +
					"planet2id = ?, " +
					"typeid = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, dict.getText());
//			if (dict.getGenderTexts() != null)
//				ps.setLong(2, dict.getGenderTexts().getId());
//			else
//				ps.setLong(2, java.sql.Types.NULL);
			ps.setLong(3, dict.getPlanet1().getId());
			ps.setLong(4, dict.getPlanet2().getId());
			ps.setLong(5, dict.getType().getId());
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
	public PlanetAspectText init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		PlanetAspectText dict = (model != null) ? (PlanetAspectText)model : (PlanetAspectText)create();
		dict = (PlanetAspectText)super.init(rs, model);
		PlanetService service = new PlanetService();
		dict.setPlanet1((Planet)service.find(rs.getLong("Planet1ID")));
		dict.setPlanet2((Planet)service.find(rs.getLong("Planet2ID")));
		dict.setType((AspectType)new AspectTypeService().find(rs.getLong("TypeID")));
		dict.setAspect((Aspect)new AspectService().find(rs.getLong("aspectID")));
		return dict;
	}

	@Override
	public Model create() {
		return new PlanetAspectText();
	}

	/**
	 * Поиск толкования аспекта
	 * @param planet1 первая планета
	 * @param planet2 вторая планета
	 * @param aspect аспект
	 * @return аспект между планетами
	 * @throws DataAccessException
	 */
	public List<Model> finds(Planet planet1, Planet planet2, Aspect aspect, AspectType type) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + 
				" where typeid = ?" +
					" and ((planet1id = ? and planet2id = ?)" +
						" or (planet1id = ? and planet2id = ?))" +
					" and (aspectid is null" +
						" or aspectid = ?)";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, type.getId());
			ps.setLong(2, planet1.getId());
			ps.setLong(3, planet2.getId());
			ps.setLong(4, planet2.getId());
			ps.setLong(5, planet1.getId());
			ps.setLong(6, aspect.getId());
//			System.out.println(ps);
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
select * from synastryaspects
where typeid = 2
and ((planet1id = 19 and planet2id = 33) or (planet1id = 33 and planet2id = 19))
and (aspectid is null or aspectid = 4)
 */
	}
}
