package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.BaseEntity;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.DateUtil;

/**
 * Класс, обеспечивающий взаимодействие с БД
 * @author nataly
 */
public class AnalyticsService {

	/**
	 * Поиск известных событий, 
	 * произошедших в указанную дату
	 * @param date дата
	 * @return список людей
	 * @throws DataAccessException
	 */
	public List<Event> getCelebrities(String date) throws DataAccessException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtil.getDate(date));
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
/*
select surname, callname, initialDate, comment
from events 
where cast(initialDate as char) like '%-08-07%'
and celebrity = true
order by initialDate		
 */
        List<Event> list = new ArrayList<Event>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = 
				"select surname, callname, initialDate, comment " +
				"from events " +
				"where celebrity = true " +
				"and cast(initialDate as char) like '%-" + 
					DateUtil.formatDateNumber(month) + "-" + 
					DateUtil.formatDateNumber(day) + "%' " + 
				"order by initialDate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Event event = new Event();
				if (rs.getString("Callname") != null)
					event.setName(rs.getString("Callname"));
				if (rs.getString("Surname") != null)
					event.setSurname(rs.getString("Surname"));
				event.setBirth(DateUtil.getDatabaseDateTime(rs.getString("initialdate")));
				if (rs.getString("Comment") != null)
					event.setDescription(rs.getString("Comment"));
				list.add(event);
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

	/**
	 * Поиск известных событий, 
	 * произошедших в указанную дату
	 * @param date дата
	 * @return список людей
	 * @throws DataAccessException
	 */
	public List<Event> getTwins(List<BaseEntity> planets) throws DataAccessException {
		if (planets == null) return null;
		double sunInitial = 0, sunFinal = 0,
			moonInitial = 0, moonFinal = 0,
			mercuryInitial = 0, mercuryFinal = 0,
			venusInitial = 0, venusFinal = 0,
			marsInitial = 0, marsFinal = 0;
		
		for (BaseEntity entity : planets) {
			Planet planet = (Planet)entity;
			if (planet.getSign() != null) {
				if (planet.getCode().equals("Sun")) {
					sunInitial = planet.getSign().getInitialPoint();
					sunFinal = planet.getSign().getCoord();
				} else if (planet.getCode().equals("Moon")) {
					moonInitial = planet.getSign().getInitialPoint();
					moonFinal = planet.getSign().getCoord();
				} else if (planet.getCode().equals("Mercury")) {
					mercuryInitial = planet.getSign().getInitialPoint();
					mercuryFinal = planet.getSign().getCoord();
				} else if (planet.getCode().equals("Venus")) {
					venusInitial = planet.getSign().getInitialPoint();
					venusFinal = planet.getSign().getCoord();
				} else if (planet.getCode().equals("Mars")) {
					marsInitial = planet.getSign().getInitialPoint();
					marsFinal = planet.getSign().getCoord();
				}
			}
		}
/*
select e.surname, e.callname, e.initialDate, e.comment
from events e
inner join clientplanets cp on (cp.code = e.id)
where e.celebrity = true
and abs(cp.sun) between abs(91.43) and abs(112.26)
and abs(cp.moon) between abs(91.43) and abs(112.26)
and abs(cp.mercury) between abs(112.26) and abs(148.56)
and abs(cp.venus) between abs(112.26) and abs(148.56)
and abs(cp.mars) between abs(24.39) and abs(62.08);
order by e.initialDate		
 */
        List<Event> list = new ArrayList<Event>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = 
				"select e.surname, e.callname, e.initialDate, e.comment " +
				"from events e " +
				"inner join eventplanets cp on (cp.eventid = e.id) " +
				"where e.celebrity = true " +
				"and abs(cp.sun) between ? and ? " + 
				"and abs(cp.moon) between ? and ? " + 
				"and abs(cp.mercury) between ? and ? " + 
				"and abs(cp.venus) between ? and ? " + 
				"and abs(cp.mars) between ? and ? " + 
				"order by e.initialDate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setDouble(1, sunInitial);
			ps.setDouble(2, sunFinal);
			ps.setDouble(3, moonInitial);
			ps.setDouble(4, moonFinal);
			ps.setDouble(5, mercuryInitial);
			ps.setDouble(6, mercuryFinal);
			ps.setDouble(7, venusInitial);
			ps.setDouble(8, venusFinal);
			ps.setDouble(9, marsInitial);
			ps.setDouble(10, marsFinal);
			rs = ps.executeQuery();
			while (rs.next()) {
				Event event = new Event();
				if (rs.getString("Callname") != null)
					event.setName(rs.getString("Callname"));
				if (rs.getString("Surname") != null)
					event.setSurname(rs.getString("Surname"));
				event.setBirth(DateUtil.getDatabaseDateTime(rs.getString("initialdate")));
				if (rs.getString("Comment") != null)
					event.setDescription(rs.getString("Comment"));
				list.add(event);
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

	/**
	 * Поиск знаков Зодиака
	 * @param planet планета
	 * @param type тип позиции планеты в знаке
	 * @return список знаков Зодиака
	 * @throws DataAccessException
	 */
	public List<Sign> getSigns(Planet planet, String type) throws DataAccessException {
        List<Sign> list = new ArrayList<Sign>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select p.signid from planetsignposition p " +
					"inner join positionType t on p.typeid = t.id " +
					"where p.planetid = ? " +
					"and t.code like ?";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setLong(1, planet.getId());
			ps.setString(2, type);
			rs = ps.executeQuery();
			while (rs.next()) {
				Long id = Long.parseLong(rs.getString(0));
				Sign sign = (Sign)Sign.getService().getEntityById(id);
				list.add(sign);
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
			String query = 
					"select p.planetid from planetsignposition p " +
					"inner join positiontype t on p.typeid = t.id " +
					"where p.signid = ? " +
					"and t.code like ?";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setLong(1, sign.getId());
			ps.setString(2, type);
			rs = ps.executeQuery();
			while (rs.next()) {
				Long id = Long.parseLong(rs.getString(1));
				planet = (Planet)Planet.getService().getEntityById(id);
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
