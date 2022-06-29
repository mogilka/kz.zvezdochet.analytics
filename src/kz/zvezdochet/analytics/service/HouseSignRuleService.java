package kz.zvezdochet.analytics.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.analytics.bean.HouseSignRule;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.service.SignService;

/**
 * Сервис правил домов в знаках Зодиака
 * @author Natalie Didenko
 */
public class HouseSignRuleService extends GenderTextModelService {

	public HouseSignRuleService() {
		tableName = "housesignrule";
	}

	@Override
	public HouseSignRule init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		HouseSignRule dict = (model != null) ? (HouseSignRule)model : (HouseSignRule)create();
		dict = (HouseSignRule)super.init(rs, model);
		dict.setSign2((Sign)new SignService().find(rs.getLong("sign2id")));
		dict.setPlanet((Planet)new PlanetService().find(rs.getLong("planetid")));
		return dict;
	}

	@Override
	public Model create() {
		return new HouseSignRule();
	}

	public List<HouseSignRule> find(House house, Sign sign) throws DataAccessException {
		List<HouseSignRule> list = new ArrayList<HouseSignRule>();
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
}
