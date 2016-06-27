package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.PlanetHouseText;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.PlanetService;

/**
 * Сервис планет в астрологических домах
 * @author Nataly Didenko
 */
public class PlanetHouseService extends GenderTextModelService {

	public PlanetHouseService() {
		tableName = "planethouses";
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
			if (planet.isDamaged() || planet.getCode().equals("Lilith"))
				aspectType = (AspectType)service.find("NEGATIVE");
			else
				aspectType = (AspectType)service.find("POSITIVE");
		} else {
			if (planet.getCode().equals("Lilith") && aspectType.getCode().equals("NEUTRAL"))
				aspectType = (AspectType)service.find("NEGATIVE");			
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

	@Override
	public List<Model> getList() throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + " order by planetID, houseID";
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
		PlanetHouseText dict = (PlanetHouseText)model;
		//dict.setGenderText((TextGender)new TextGenderService().save(dict.getGenderTexts()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(text, genderid, planetid, houseid, typeid) " +
					"values(?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"planetid = ?, " +
					"houseid = ?, " +
					"typeid = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, dict.getText());
//			if (dict.getGenderTexts() != null)
//				ps.setLong(2, dict.getGenderTexts().getId());
//			else
//				ps.setLong(2, java.sql.Types.NULL);
			ps.setLong(3, dict.getPlanet().getId());
			ps.setLong(4, dict.getHouse().getId());
			ps.setLong(5, dict.getAspectType().getId());
			result = ps.executeUpdate();
			if (result == 1) {
				if (null == model.getId()) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        model.setId(autoIncKeyFromApi);
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
	public PlanetHouseText init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		PlanetHouseText dict = (model != null) ? (PlanetHouseText)model : (PlanetHouseText)create();
		dict = (PlanetHouseText)super.init(rs, model);
		dict.setPlanet((Planet)new PlanetService().find(rs.getLong("PlanetID")));
		dict.setHouse((House)new HouseService().find(rs.getLong("HouseID")));
		dict.setAspectType((AspectType)new AspectTypeService().find(rs.getLong("TypeID")));
		return dict;
	}

	@Override
	public Model create() {
		return new PlanetHouseText();
	}
}
