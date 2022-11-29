/**
 * 
 */
package kz.zvezdochet.analytics.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import kz.zvezdochet.analytics.bean.Rule;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;

/**
 * @author Natalie Didenko
 *
 */
public class RuleService extends ModelService {

	public RuleService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "rules" : "us_rules";
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model init(ResultSet rs, Model base) throws DataAccessException, SQLException {
		Rule type = (base != null) ? (Rule)base : (Rule)create();
		type.setId(rs.getLong("id"));
		type.setDescription(rs.getString("description"));
		type.setText(rs.getString("text"));
		return type;
	}

	@Override
	public Model create() {
		return new Rule();
	}
}
