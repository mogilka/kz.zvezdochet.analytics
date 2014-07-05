package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.analytics.bean.SynastryTextReference;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.BaseEntity;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;

/**
 * Реализация сервиса справочника синастрий (Планета в знаках партнеров)
 * @author nataly
 *
 * @see GenderTextReferenceService Реализация сервиса справочников  
 */
public class SynastrySignService extends GenderTextReferenceService {

	public SynastrySignService() {
		tableName = "synastrysigns";
	}
	
	@Override
	public BaseEntity getEntityByCode(String code) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseEntity getEntityById(Long id) throws DataAccessException {
		SynastryTextReference reference = null;
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
			query = "select * from " + tableName + " order by planetID, sign1ID, sign2ID";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				SynastryTextReference reference = initEntity(rs);
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
		SynastryTextReference reference = (SynastryTextReference)element;
		reference.setGenderText((GenderText)new GenderTextService().saveEntity(reference.getGenderText()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(text, genderid, code, name, description, sign1id, sign2id, planetid) " +
					"values(?,?,?,?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"sign1id = ?, " +
					"sign2id = ?, " +
					"planetid = ? " +
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
			ps.setLong(6, reference.getSign1().getId());
			ps.setLong(7, reference.getSign2().getId());
			ps.setLong(8, reference.getPlanet().getId());
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
	public SynastryTextReference initEntity(ResultSet rs) throws DataAccessException, SQLException {
		SynastryTextReference reference = (SynastryTextReference)super.initEntity(rs);
		reference.setSign1((Sign)Sign.getService().getEntityById(Long.parseLong(rs.getString("Sign1ID"))));
		reference.setSign2((Sign)Sign.getService().getEntityById(Long.parseLong(rs.getString("Sign2ID"))));
		reference.setPlanet((Planet)Planet.getService().getEntityById(Long.parseLong(rs.getString("PlanetID"))));
		return reference;
	}

	@Override
	public BaseEntity createEntity() {
		return new SynastryTextReference();
	}
}
