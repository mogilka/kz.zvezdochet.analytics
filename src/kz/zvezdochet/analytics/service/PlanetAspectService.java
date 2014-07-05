package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.analytics.bean.PlanetAspectTextReference;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.BaseEntity;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;

/**
 * Реализация сервиса справочника Аспекты планет
 * @author nataly
 *
 * @see GenderTextReferenceService Реализация сервиса простого справочника  
 */
public class PlanetAspectService extends GenderTextReferenceService {

	public PlanetAspectService() {
		tableName = "planetaspects";
	}

	/**
	 * Поиск толкования аспекта
	 * @param planet1 первая планета
	 * @param planet2 вторая планета
	 * @param aspectType тип аспекта
	 * @return аспект между планетами
	 * @throws DataAccessException
	 */
	public BaseEntity getEntity(Planet planet1, Planet planet2, AspectType aspectType) throws DataAccessException {
        PlanetAspectTextReference reference = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + 
				" where typeid = " + aspectType.getId() +
				" and planet1id = " + planet1.getId() +
				" and planet2id = " + planet2.getId();
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
        PlanetAspectTextReference reference = null;
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
			query = "select * from " + tableName + " order by planet1ID, planet2ID, TypeID";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				PlanetAspectTextReference reference = initEntity(rs);
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
		PlanetAspectTextReference reference = (PlanetAspectTextReference)element;
		reference.setGenderText((GenderText)new GenderTextService().saveEntity(reference.getGenderText()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(text, genderid, code, name, description, planet1id, planet2id, typeid) " +
					"values(?,?,?,?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"planet1id = ?, " +
					"planet2id = ?, " +
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
			ps.setLong(6, reference.getPlanet1().getId());
			ps.setLong(7, reference.getPlanet2().getId());
			ps.setLong(8, reference.getType().getId());
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
	public PlanetAspectTextReference initEntity(ResultSet rs) throws DataAccessException, SQLException {
		PlanetAspectTextReference reference = (PlanetAspectTextReference)super.initEntity(rs);
		reference.setPlanet1((Planet)Planet.getService().getEntityById(Long.parseLong(rs.getString("Planet1ID"))));
		reference.setPlanet2((Planet)Planet.getService().getEntityById(Long.parseLong(rs.getString("Planet2ID"))));
		reference.setType((AspectType)AspectType.getService().getEntityById(Long.parseLong(rs.getString("TypeID"))));
		return reference;
	}

	@Override
	public BaseEntity createEntity() {
		return new PlanetAspectTextReference();
	}
}
