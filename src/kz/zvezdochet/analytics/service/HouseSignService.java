package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.HouseSignText;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.SignService;

/**
 * Сервис домов в знаках Зодиака
 * @author Nataly Didenko
 */
public class HouseSignService extends GenderTextModelService {

	public HouseSignService() {
		tableName = "housesigns";
	}

	@Override
	public List<Model> getList() throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + " order by signid";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next())
				list.add(init(rs, null));
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
	public HouseSignText init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		HouseSignText dict = (model != null) ? (HouseSignText)model : (HouseSignText)create();
		dict = (HouseSignText)super.init(rs, model);
		dict.setSign((Sign)new SignService().find(rs.getLong("signid")));
		dict.setHouse((House)new HouseService().find(rs.getLong("houseid")));
		return dict;
	}

	@Override
	public Model create() {
		return new HouseSignText();
	}

	public HouseSignText find(House house, Sign sign) throws DataAccessException {
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName +
				" where signid = ? " +
					"and houseid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, sign.getId());
			ps.setLong(2, house.getId());
			rs = ps.executeQuery();
			while (rs.next())
				return init(rs, null);
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
		return null;
	}
}
