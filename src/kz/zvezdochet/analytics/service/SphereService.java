package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.Sphere;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.HouseService;

/**
 * Сервис сфер жизни
 * @author Nataly Didenko
 */
public class SphereService extends DictionaryService {

	public SphereService() {
		tableName = "spheres";
	}

	@Override
	public Model create() {
		return new Sphere();
	}

	public List<Model> getHouses(long id) throws DataAccessException {
        List<Model> list = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			HouseService houseService = new HouseService();
			String h = houseService.getTableName();
			String sh = getTableHouseName();

			String sql = "select h.* from " + h + " h" +
				" inner join " + sh + " sh on h.id = sh.houseid" +
				" where sh.sphereid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, id);
			rs = ps.executeQuery();
			while (rs.next())
				list.add(houseService.init(rs, new House()));
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

	public String getTableHouseName() {
		return "sphere_house";
	}
}
