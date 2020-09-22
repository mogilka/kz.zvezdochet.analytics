package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.SynastryAspectText;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.AspectTypeService;

/**
 * Сервис синастрических аспектов
 * @author Natalie Didenko
 */
public class SynastryAspectService extends PlanetAspectService {

	public SynastryAspectService() {
		tableName = "synastryaspects";
	}

	@Override
	public SynastryAspectText init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		SynastryAspectText dict = (model != null) ? (SynastryAspectText)model : (SynastryAspectText)create();
		dict = (SynastryAspectText)super.init(rs, model);
		dict.setRoles(rs.getString("roles"));
		dict.setLevel(rs.getInt("level"));
		return dict;
	}

	@Override
	public Model create() {
		return new SynastryAspectText();
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
			AspectType type = aspect.checkType(false);
			if (aspect.getAspect().getType().getCode().equals("NEUTRAL")
					&& !planet2.getCode().equals("Kethu")
					&& !planet2.getCode().equals("Lilith")) {
				if (planet.isLilithed()	|| planet2.isLilithed()) {
					type = (AspectType)new AspectTypeService().find("NEGATIVE");
				}
			}
			String acode = aspect.getAspect().getCode();

			sql = "select * from " + tableName + 
				" where (typeid = ?)" +
					" and (planet1id = ? and planet2id = ?)" +
					" and (aspectid is null" +
						" or aspectid = ?)"
				+ "order by aspectid";
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
}
