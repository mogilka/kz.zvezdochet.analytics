package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.bean.TextReference;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.service.IReferenceService;
import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Реализация сервиса справочников с расширенной текстовой информацией
 * @author Nataly Didenko
 *
 * @see ModelService Реализация интерфейса сервиса управления объектами на уровне БД  
 * @see IReferenceService Интерфейс управления справочниками на уровне БД  
 */
public class TextReferenceService extends ReferenceService implements IReferenceService {

	@Override
	public Model save(Model model) throws DataAccessException {
		TextReference reference = (TextReference)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (model.getId() == null) 
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
				if (model.getId() == null) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        model.setId(autoIncKeyFromApi);
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
			update();
		}
		return reference;
	}

	@Override
	public TextReference init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		TextReference type = (model != null) ? (TextReference)model : (TextReference)create();
		super.init(rs, type);
		type.setText(rs.getString("Text"));
		return type;
	}

	@Override
	public Model create() {
		return new TextReference();
	}
}
