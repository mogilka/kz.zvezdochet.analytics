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
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.PlanetService;

/**
 * Реализация сервиса справочника Аспекты планет
 * @author Nataly Didenko
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
	public Model find(Planet planet1, Planet planet2, AspectType aspectType) throws DataAccessException {
        PlanetAspectTextReference reference = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + 
				" where typeid = " + aspectType.getId() +
				" and planet1id = " + planet1.getId() +
				" and planet2id = " + planet2.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next())
				reference = init(rs, null);
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
	public List<Model> getList() throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + " order by planet1ID, planet2ID, TypeID";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				PlanetAspectTextReference reference = init(rs, null);
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
	public Model save(Model model) throws DataAccessException {
		PlanetAspectTextReference reference = (PlanetAspectTextReference)model;
		reference.setGenderText((GenderText)new GenderTextService().save(reference.getGenderText()));
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (model.getId() == null) 
				sql = "insert into " + tableName + 
					"(text, genderid, code, name, description, planet1id, planet2id, typeid) " +
					"values(?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"text = ?, " +
					"genderid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"planet1id = ?, " +
					"planet2id = ?, " +
					"typeid = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
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
				if (model.getId() == null) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        model.setId(autoIncKeyFromApi);
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
	public PlanetAspectTextReference init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		PlanetAspectTextReference reference = (model != null) ? (PlanetAspectTextReference)model : (PlanetAspectTextReference)create();
		super.init(rs, reference);
		PlanetService service = new PlanetService();
		reference.setPlanet1((Planet)service.find(rs.getLong("Planet1ID")));
		reference.setPlanet2((Planet)service.find(rs.getLong("Planet2ID")));
		reference.setType((AspectType)new AspectTypeService().find(rs.getLong("TypeID")));
		return reference;
	}

	@Override
	public Model create() {
		return new PlanetAspectTextReference();
	}
}
