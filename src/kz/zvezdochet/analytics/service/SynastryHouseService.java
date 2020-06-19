package kz.zvezdochet.analytics.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.analytics.bean.SynastryHouseText;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;

/**
 * Сервис планет в синастрических домах
 * @author Natalie Didenko
 */
public class SynastryHouseService extends PlanetHouseService {

	public SynastryHouseService() {
		tableName = "synastryhouses";
	}

	@Override
	public Model create() {
		return new SynastryHouseText();
	}

	@Override
	public SynastryHouseText init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		SynastryHouseText dict = (model != null) ? (SynastryHouseText)model : (SynastryHouseText)create();
		dict = (SynastryHouseText)super.init(rs, model);
		dict.setLevel(rs.getInt("level"));
		return dict;
	}
}
