package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.Category;
import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.analytics.bean.PlanetSignTextReference;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Base;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.service.SignService;

/**
 * Реализация сервиса справочника "Планеты в знаках Зодиака"
 * @author Nataly Didenko
 *
 * @see GenderTextReferenceService Реализация сервиса справочников  
 */
public class PlanetSignService extends GenderTextReferenceService {

	public PlanetSignService() {
		tableName = "planetsigns";
	}

	@Override
	public Base find(String code) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Base find(Long id) throws DataAccessException {
		PlanetSignTextReference reference = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " where id = " + id;
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			if (rs.next()) 
				reference = init(rs);
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
	public List<Base> getList() throws DataAccessException {
        List<Base> list = new ArrayList<Base>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " order by signID";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				PlanetSignTextReference reference = init(rs);
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
	public Base save(Base element) throws DataAccessException {
		PlanetSignTextReference reference = (PlanetSignTextReference)element;
		reference.setGenderText((GenderText)new GenderTextService().save(reference.getGenderText()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(signid, typeid, text, genderid, code, name, description) " +
					"values(?,?,?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"signid = ?, " +
					"typeid = ?, " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setLong(1, reference.getSign().getId());
			ps.setLong(2, reference.getCategory().getId());
			ps.setString(3, reference.getText());
			if (reference.getGenderText() != null)
				ps.setLong(4, reference.getGenderText().getId());
			else
				ps.setLong(4, java.sql.Types.NULL);
			ps.setString(5, reference.getCode());
			ps.setString(6, reference.getName());
			ps.setString(7, reference.getDescription());
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
	public PlanetSignTextReference init(ResultSet rs) throws DataAccessException, SQLException {
		PlanetSignTextReference reference = (PlanetSignTextReference)super.init(rs);
		reference.setSign((Sign)new SignService().find(Long.parseLong(rs.getString("SignID"))));
		reference.setCategory((Category)new CategoryService().find(Long.parseLong(rs.getString("TypeID"))));
		reference.setPlanet((Planet)new PlanetService().find(reference.getCategory().getObjectId()));
		return reference;
	}

	@Override
	public Base create() {
		return new PlanetSignTextReference();
	}
}
