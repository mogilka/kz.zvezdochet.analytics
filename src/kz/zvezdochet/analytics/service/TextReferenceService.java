package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.bean.TextReference;
import kz.zvezdochet.core.bean.BaseEntity;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.EntityService;
import kz.zvezdochet.core.service.IReferenceService;
import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Реализация сервиса справочников с расширенной текстовой информацией
 * @author nataly
 *
 * @see EntityService Реализация интерфейса сервиса управления объектами на уровне БД  
 * @see IReferenceService Интерфейс управления справочниками на уровне БД  
 */
public class TextReferenceService extends ReferenceService implements IReferenceService {

	@Override
	public BaseEntity getEntityByCode(String code) throws DataAccessException {
        TextReference type = new TextReference();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " where code like '" + code + "'";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			if (rs.next()) 
				type = initEntity(rs);
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
		return type;
	}

	@Override
	public BaseEntity getEntityById(Long id) throws DataAccessException {
        TextReference type = new TextReference();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " where id = " + id;
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			if (rs.next()) 
				type = initEntity(rs);
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
		return type;
	}

	@Override
	public BaseEntity saveEntity(BaseEntity element) throws DataAccessException {
		TextReference reference = (TextReference)element;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + "(code, name, description, text) values(?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"text = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setString(1, reference.getCode());
			ps.setString(2, reference.getName());
			ps.setString(3, reference.getDescription());
			ps.setString(4, reference.getText());
			result = ps.executeUpdate();
			if (result == 1) {
				if (element.getId() == null) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        element.setId(autoIncKeyFromApi);
//					    System.out.println("inserted " + tableName + "\t" + autoIncKeyFromApi);
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
	public TextReference initEntity(ResultSet rs) throws DataAccessException, SQLException {
		TextReference type = (TextReference)super.initEntity(rs);
		type.setText(rs.getString("Text"));
		return type;
	}

	@Override
	public BaseEntity createEntity() {
		// TODO Auto-generated method stub
		return null;
	}
}
