package kz.zvezdochet.analytics.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.analytics.bean.Moonday;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;

/**
 * Сервис Лунных дней
 * @author Natalie Didenko
 */
public class MoondayService extends ModelService {

	public MoondayService() {
		tableName = "moonday";
	}

	@Override
	public Model create() {
		return new Moonday();
	}

	@Override
	public Moonday init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		Moonday dict = (model != null) ? (Moonday)model : (Moonday)create();
		dict.setId(rs.getLong("id"));
		dict.setSymbol(rs.getString("symbol"));
		dict.setMineral(rs.getString("mineral"));
		dict.setBirth(rs.getString("birth"));
		dict.setPositive(rs.getBoolean("positive"));
		return dict;
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
}
