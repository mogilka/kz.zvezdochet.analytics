package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.core.bean.Base;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.BaseService;
import kz.zvezdochet.core.service.IReferenceService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Реализация сервиса толкований для мужчин и женщин
 * @author Nataly Didenko
 *
 * @see BaseService Реализация интерфейса сервиса управления объектами на уровне БД  
 * @see IReferenceService Интерфейс управления справочниками на уровне БД  
 */
public class GenderTextService extends BaseService implements IReferenceService {

	public GenderTextService() {
		tableName = "textgender";
	}

	@Override
	public Base find(String code) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Base find(Long id) throws DataAccessException {
		GenderText genderText = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " where id = " + id;
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) 
				genderText = init(rs);
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
		return genderText;
	}

	@Override
	public List<Base> getList() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Base save(Base element) throws DataAccessException {
		GenderText reference = (GenderText)element;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + "(male, female) values(?,?)";
			else
				query = "update " + tableName + " set " +
					"male = ?, " +
					"female = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setString(1, reference.getMaletext());
			ps.setString(2, reference.getFemaletext());
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
		}
		return reference;
	}

	@Override
	public GenderText init(ResultSet rs) throws DataAccessException, SQLException {
		GenderText genderText = new GenderText();
		genderText.setId(Long.parseLong(rs.getString("ID")));
		genderText.setMaletext(rs.getString("Male"));
		genderText.setFemaletext(rs.getString("Female"));
		return genderText;
	}

	@Override
	public Base create() {
		// TODO Auto-generated method stub
		return null;
	}
}
