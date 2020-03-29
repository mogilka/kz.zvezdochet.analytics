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
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.AspectService;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.PlanetService;

/**
 * Сервис аспектов планет
 * @author Natalie Didenko
 */
public class PlanetAspectService extends GenderTextModelService {

	public PlanetAspectService() {
		tableName = "planetaspects";
	}

	/**
	 * Поиск толкования аспекта
	 * @param aspect аспект
	 * @param aspectid аспект, с учётом которого необходимо произвести поиск
	 * @param checkType проверка позитивных и негативных аспектов
	 * @return толкование аспекта между планетами
	 * @throws DataAccessException
	 */
	public Model find(SkyPointAspect aspect, long aspectid, boolean checkType) throws DataAccessException {
        PlanetAspectText dict = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			AspectType type = checkType ? aspect.checkType(false) : aspect.getAspect().getType();
			sql = "select * from " + tableName + 
				" where typeid = ?" +
					" and ((planet1id = ? and planet2id = ?)" +
						" or (planet1id = ? and planet2id = ?))";

			sql += (aspectid > 0)
				? " and aspectid = ?"
				: " and (aspectid is null or aspectid = ?)";

			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, type.getId());
			long pid1 = aspect.getSkyPoint1().getId();
			long pid2 = aspect.getSkyPoint2().getId();
			ps.setLong(2, pid1);
			ps.setLong(3, pid2);
			ps.setLong(4, pid2);
			ps.setLong(5, pid1);
			ps.setLong(6, (aspectid > 0) ? aspectid : aspect.getAspect().getId());
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
		dict.setDescription(rs.getString("description"));
		dict.setCode(rs.getString("code"));
		return dict;
	}

	@Override
	public Model create() {
		return new PlanetAspectText();
	}

	/**
	 * Поиск толкования аспекта
	 * @param aspect аспект
	 * @param reverse true порядок планет, указанных в аспекте не меняется
	 * @return список толкований аспектов между планетами
	 * @throws DataAccessException
	 */
	public List<Model> finds(SkyPointAspect aspect, boolean reverse) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			Planet planet = reverse ? (Planet)aspect.getSkyPoint2() : (Planet)aspect.getSkyPoint1();
			Planet planet2 = reverse ? (Planet)aspect.getSkyPoint1() : (Planet)aspect.getSkyPoint2();
			AspectType type = aspect.checkType(planet.isMain());
			String acode = aspect.getAspect().getCode();
			String wheretype = (acode != null) && acode.equals("KERNEL") ? "or typeid = 1" : "";

			sql = "select * from " + tableName + 
				" where (typeid = ? " + wheretype + ")" +
					" and (planet1id = ? and planet2id = ?)" +
					" and (aspectid is null" +
						" or aspectid = ?)";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, type.getId());
			ps.setLong(2, planet.getId());
			ps.setLong(3, planet2.getId());
			ps.setLong(4, null == acode ? java.sql.Types.NULL : aspect.getAspect().getId());
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

	/**
	 * Поиск толкования аспекта
	 * @param planet1 первая планета
	 * @param planet2 вторая планета
	 * @param aspect аспект
	 * @return толкование аспекта между планетами
	 * @throws DataAccessException
	 */
	public Model find(Planet planet1, Planet planet2, Aspect aspect) throws DataAccessException {
		SkyPointAspect a = new SkyPointAspect();
		a.setSkyPoint1(planet1);
		a.setSkyPoint2(planet2);
		a.setAspect(aspect);
		return find(a, 0, false);
	}
}
