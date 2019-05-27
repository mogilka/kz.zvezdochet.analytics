package kz.zvezdochet.analytics.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.analytics.bean.SynastryAspectText;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;

/**
 * Сервис синастрических аспектов
 * @author Natalie Didenko
 */
public class SynastryAspectService extends PlanetAspectService {

	public SynastryAspectService() {
		tableName = "synastryaspects";
	}

	@Override
	public SynastryAspectText init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		SynastryAspectText dict = (model != null) ? (SynastryAspectText)model : (SynastryAspectText)create();
		dict = (SynastryAspectText)super.init(rs, model);
		dict.setRoles(rs.getString("roles"));
		return dict;
	}

	@Override
	public Model create() {
		return new SynastryAspectText();
	}
}
