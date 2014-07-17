package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.GenderText;
import kz.zvezdochet.analytics.bean.TextGenderReference;
import kz.zvezdochet.core.bean.Base;
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
	public Base find(Long id) throws DataAccessException {
		TextGenderReference reference = null;
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
			query = "select * from " + tableName + " order by name";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				TextGenderReference reference = init(rs);
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
	public TextGenderReference init(ResultSet rs) throws DataAccessException, SQLException {
		TextGenderReference type = (TextGenderReference)super.init(rs);
		type.setText(rs.getString("Text"));
		if (rs.getString("GenderID") != null) {
			GenderText genderText = (GenderText)new GenderTextService().find(Long.parseLong(rs.getString("GenderID")));
			if (genderText != null)
				type.setGenderText(genderText);
		}
		return type;
	}

	@Override
	public Base create() {
		return new TextGenderReference();
	}
}
