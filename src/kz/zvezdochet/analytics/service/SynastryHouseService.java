package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.analytics.bean.SynastryHouseText;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.AspectTypeService;

/**
 * Сервис планет в синастрических домах
 * @author Natalie Didenko
 */
public class SynastryHouseService extends PlanetHouseService {

	public SynastryHouseService() {
		tableName = "synastryhouses";
	}

	@Override
	public Model create() {
		return new SynastryHouseText();
	}

	@Override
	public SynastryHouseText init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		SynastryHouseText dict = (model != null) ? (SynastryHouseText)model : (SynastryHouseText)create();
		dict = (SynastryHouseText)super.init(rs, model);
		dict.setLevel(rs.getInt("level"));
		return dict;
	}

	/**
	 * Поиск толкования планеты в доме
	 * @param planet планета
	 * @param house астрологический дом
	 * @param aspectType тип аспекта
	 * @return описание позиции планеты в доме
	 * @throws DataAccessException
	 */
	public Model find(Planet planet, House house, AspectType aspectType) throws DataAccessException {
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;

		AspectTypeService service = new AspectTypeService();
		if (null == aspectType) {
			if (planet.getCode().equals("Lilith") || planet.getCode().equals("Kethu"))
				aspectType = (AspectType)service.find("NEGATIVE");
			else
				aspectType = (AspectType)service.find("POSITIVE");
		}		
		try {
			sql = "select * from " + tableName + 
				" where typeid = " + aspectType.getId() +
				" and planetid = " + planet.getId() +
				" and houseid = " + house.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
//			System.out.println(planet + " " + house);
			rs = ps.executeQuery();
			if (rs.next())
				return init(rs, create());
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
		return null;
	}
}
