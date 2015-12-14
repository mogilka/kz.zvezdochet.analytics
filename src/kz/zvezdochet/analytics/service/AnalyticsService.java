package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.PlanetService;

/**
 * Сервис взаимодействия с БД
 * @author Nataly Didenko
 */
public class AnalyticsService {

	/**
	 * Поиск планеты-управителя знака Зодиака
	 * @param sign знак Зодиака
	 * @param type тип позиции планеты в знаке (Обитель)
	 * @return планета
	 * @throws DataAccessException
	 */
	public Planet getSignPlanet(Sign sign, String type) throws DataAccessException {
        Planet planet = new Planet();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = 
					"select p.planetid from planetsignposition p " +
					"inner join positiontype t on p.typeid = t.id " +
					"where p.signid = ? " +
					"and t.code like ? " +
					"and p.day = 1";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, sign.getId());
			ps.setString(2, type);
			rs = ps.executeQuery();
			while (rs.next()) {
				Long id = Long.parseLong(rs.getString(1));
				planet = (Planet)new PlanetService().find(id);
				if (!planet.isFictitious())
					return planet;
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
		return null;
	}
}
