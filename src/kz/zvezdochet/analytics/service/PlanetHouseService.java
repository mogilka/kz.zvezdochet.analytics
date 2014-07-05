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
import kz.zvezdochet.core.bean.BaseEntity;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.BeanUtil;

/**
 * Реализация сервиса справочника "Планеты в астрологических домах"
 * @author nataly
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
	public BaseEntity getEntity(Planet planet, House house, AspectType aspectType) throws DataAccessException {
        PlanetHouseTextReference reference = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;

		 //TODO доработать с учетом других признаков
		if (aspectType == null)
			aspectType = (AspectType)BeanUtil.getReferenceByCode(
				AspectType.getService().getList(), "NEUTRAL");
		if (planet.isDamaged())
			aspectType = (AspectType)BeanUtil.getReferenceByCode(
				AspectType.getService().getList(), "NEGATIVE");
		
		try {
			query = "select * from " + tableName + 
				" where typeid = " + aspectType.getId() +
				" and planetid = " + planet.getId() +
				" and houseid = " + house.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			if (rs.next())
				reference = initEntity(rs);
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
	public BaseEntity getEntityById(Long id) throws DataAccessException {
		PlanetHouseTextReference reference = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " where id = " + id;
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			if (rs.next())
				reference = initEntity(rs);
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
	public List<BaseEntity> getOrderedEntities() throws DataAccessException {
        List<BaseEntity> list = new ArrayList<BaseEntity>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " order by planetID, houseID";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				PlanetHouseTextReference reference = initEntity(rs);
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
	public BaseEntity saveEntity(BaseEntity element) throws DataAccessException {
		PlanetHouseTextReference reference = (PlanetHouseTextReference)element;
		reference.setGenderText((GenderText)new GenderTextService().saveEntity(reference.getGenderText()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(text, genderid, code, name, description, planetid, houseid, typeid) " +
					"values(?,?,?,?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"planetid = ?, " +
					"houseid = ?, " +
					"typeid = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
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
			updateDictionary();
		}
		return reference;
	}

	@Override
	public PlanetHouseTextReference initEntity(ResultSet rs) throws DataAccessException, SQLException {
		PlanetHouseTextReference reference = (PlanetHouseTextReference)super.initEntity(rs);
		reference.setPlanet((Planet)Planet.getService().getEntityById(Long.parseLong(rs.getString("PlanetID"))));
		reference.setHouse((House)House.getService().getEntityById(Long.parseLong(rs.getString("HouseID"))));
		reference.setAspectType((AspectType)AspectType.getService().getEntityById(Long.parseLong(rs.getString("TypeID"))));
		return reference;
	}

	@Override
	public BaseEntity createEntity() {
		return new PlanetHouseTextReference();
	}
}
