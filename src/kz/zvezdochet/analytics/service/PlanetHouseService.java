package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.analytics.bean.PlanetHouseTextReference;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.BeanUtil;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.PlanetService;

/**
 * Реализация сервиса справочника "Планеты в астрологических домах"
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Реализация сервиса простого справочника  
 */
public class PlanetHouseService extends GenderTextReferenceService {

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
        PlanetHouseTextReference reference = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;

		 //TODO доработать с учетом других признаков
		AspectTypeService service = new AspectTypeService();
		if (aspectType == null)
			aspectType = (AspectType)BeanUtil.getReferenceByCode(
					service.getList(), "NEUTRAL");
		if (planet.isDamaged())
			aspectType = (AspectType)BeanUtil.getReferenceByCode(
					service.getList(), "NEGATIVE");
		
		try {
			sql = "select * from " + tableName + 
				" where typeid = " + aspectType.getId() +
				" and planetid = " + planet.getId() +
				" and houseid = " + house.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next())
				reference = init(rs, null);
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
		return reference;
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
			while (rs.next()) {
				PlanetHouseTextReference reference = init(rs, null);
				list.add(reference);
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
	public Model save(Model element) throws DataAccessException {
		PlanetHouseTextReference reference = (PlanetHouseTextReference)element;
		reference.setGenderText((GenderText)new GenderTextService().save(reference.getGenderText()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (element.getId() == null) 
				sql = "insert into " + tableName + 
					"(text, genderid, code, name, description, planetid, houseid, typeid) " +
					"values(?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"planetid = ?, " +
					"houseid = ?, " +
					"typeid = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, reference.getText());
			if (reference.getGenderText() != null)
				ps.setLong(2, reference.getGenderText().getId());
			else
				ps.setLong(2, java.sql.Types.NULL);
			ps.setString(3, reference.getCode());
			ps.setString(4, reference.getName());
			ps.setString(5, reference.getDescription());
			ps.setLong(6, reference.getPlanet().getId());
			ps.setLong(7, reference.getHouse().getId());
			ps.setLong(8, reference.getAspectType().getId());
			result = ps.executeUpdate();
			if (result == 1) {
				if (element.getId() == null) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        element.setId(autoIncKeyFromApi);
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
			update();
		}
		return reference;
	}

	@Override
	public PlanetHouseTextReference init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		PlanetHouseTextReference reference = (model != null) ? (PlanetHouseTextReference)model : (PlanetHouseTextReference)create();
		super.init(rs, reference);
		reference.setPlanet((Planet)new PlanetService().find(rs.getLong("PlanetID")));
		reference.setHouse((House)new HouseService().find(rs.getLong("HouseID")));
		reference.setAspectType((AspectType)new AspectTypeService().find(rs.getLong("TypeID")));
		return reference;
	}

	@Override
	public Model create() {
		return new PlanetHouseTextReference();
	}
}
