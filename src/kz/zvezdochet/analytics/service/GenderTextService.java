package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.service.IReferenceService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Реализация сервиса толкований для мужчин и женщин
 * @author Nataly Didenko
 *
 * @see ModelService Реализация интерфейса сервиса управления объектами на уровне БД  
 * @see IReferenceService Интерфейс управления справочниками на уровне БД  
 */
public class GenderTextService extends ModelService implements IReferenceService {

	public GenderTextService() {
		tableName = "textgender";
	}

	@Override
	public Model find(String code) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model save(Model element) throws DataAccessException {
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
	public GenderText init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		GenderText genderText = (model != null) ? (GenderText)model : (GenderText)create();
		genderText.setId(Long.parseLong(rs.getString("ID")));
		genderText.setMaletext(rs.getString("Male"));
		genderText.setFemaletext(rs.getString("Female"));
		return genderText;
	}

	@Override
	public Model create() {
		return new GenderText();
	}
}
