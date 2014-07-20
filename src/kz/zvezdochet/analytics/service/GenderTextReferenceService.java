package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.analytics.bean.TextGenderReference;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Прототип реализации сервиса простого справочника
 * @author Nataly Didenko
 *
 * @see ReferenceService Реализация сервиса справочников  
 */
public abstract class GenderTextReferenceService extends ReferenceService {

	@Override
	public Model save(Model element) throws DataAccessException {
		TextGenderReference reference = (TextGenderReference)element;
		reference.setGenderText((GenderText)new GenderTextService().save(reference.getGenderText()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(text, genderid, code, name, description) values(?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ? " +
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
	public TextGenderReference init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		if (null == model)
			model = (TextGenderReference)create();
		super.init(rs, model);
		((TextGenderReference)model).setText(rs.getString("Text"));
		if (rs.getString("GenderID") != null) {
			GenderText genderText = (GenderText)new GenderTextService().find(Long.parseLong(rs.getString("GenderID")));
			if (genderText != null)
				((TextGenderReference)model).setGenderText(genderText);
		}
		return (TextGenderReference)model;
	}

	@Override
	public Model create() {
		return new TextGenderReference();
	}
}
