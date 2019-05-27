package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.Element;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.PlanetService;

/**
 * Сервис взаимодействия с БД
 * @author Natalie Didenko
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

	//TODO продумать метод
	public Element getElementByGroup(String[] codes) throws DataAccessException {
        Element element = new Element();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql =	
				"select Elements.*, TextGender.Male, TextGender.Female " +
	            "from Elements " +
	            "left join TextGender on Elements.GenderID = TextGender.ID " +
	            "where Elements.Name like '" + codes[0] + "' " +
				"order by Types.Priority)"; //TODO проверить синтаксис
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			int columns = rs.getMetaData().getColumnCount();
			while (rs.next()) { 
		        List<Object> sublist = new ArrayList<Object>();
				for (int i = 0; i < columns; i++)  
			        sublist.add(rs.getString(i));
				//list.add(sublist);
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
		return element;
	}
}
