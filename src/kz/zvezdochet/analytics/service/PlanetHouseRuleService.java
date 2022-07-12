package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.PlanetHouseRule;
import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.AspectService;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.service.SignService;

/**
 * Сервис планет в астрологических домах
 * @author Natalie Didenko
 */
public class PlanetHouseRuleService extends PlanetHouseService {

	public PlanetHouseRuleService() {
		tableName = "planethouserule";
	}

	/**
	 * Поиск толкования планет в домах
	 * @param planet планета
	 * @param house астрологический дом
	 * @return описание позиции планеты в доме
	 * @throws DataAccessException
	 */
	public List<PlanetHouseRule> find(Planet planet, House house) throws DataAccessException {
		List<PlanetHouseRule> list = new ArrayList<PlanetHouseRule>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + 
				" where planetid = " + planet.getId() +
				" and houseid = " + house.getId() +
				" order by typeid desc";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
//			System.out.println(planet + " " + house);
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
	public PlanetHouseRule init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		PlanetHouseRule dict = (model != null) ? (PlanetHouseRule)model : (PlanetHouseRule)create();
		dict = (PlanetHouseRule)super.init(rs, model);
		dict.setPlanet((Planet)new PlanetService().find(rs.getLong("PlanetID")));
		dict.setHouse((House)new HouseService().find(rs.getLong("HouseID")));
		dict.setAspectType((AspectType)new AspectTypeService().find(rs.getLong("TypeID")));
		dict.setPlanet2((Planet)new PlanetService().find(rs.getLong("Planet2ID")));
		dict.setHouse2((House)new HouseService().find(rs.getLong("House2ID")));
		dict.setAspect((Aspect)new AspectService().find(rs.getLong("aspectid")));
		dict.setSign((Sign)new SignService().find(rs.getLong("SignID")));
		dict.setSignOwner(rs.getInt("sign"));
		return dict;
	}

	@Override
	public Model create() {
		return new PlanetHouseRule();
	}
}
