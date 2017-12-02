package kz.zvezdochet.analytics.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.analytics.bean.Degree;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.TextGenderDictionaryService;

/**
 * Сервис Зодиакальных градусов
 * @author Nataly Didenko
 */
public class DegreeService extends TextGenderDictionaryService {

	public DegreeService() {
		tableName = "degrees";
	}

	@Override
	public Model create() {
		return new Degree();
	}

	@Override
	public Degree init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		Degree dict = (model != null) ? (Degree)model : (Degree)create();
		dict = (Degree)super.init(rs, model);
		dict.setPositive(rs.getBoolean("positive"));
		dict.setRoyal(rs.getBoolean("royal"));
		dict.setDestructive(rs.getBoolean("destructive"));
		return dict;
	}
}
