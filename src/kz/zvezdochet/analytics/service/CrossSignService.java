package kz.zvezdochet.analytics.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.analytics.bean.CrossSign;
import kz.zvezdochet.core.bean.DiagramObject;
import kz.zvezdochet.core.bean.IColorizedObject;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Сервис подкатегорий крестов
 * @author Natalie Didenko
 */
public class CrossSignService extends DictionaryService {
	
	public CrossSignService() {
		tableName = "cross_sign";
	}

	@Override
	public Model create() {
		return new CrossSign();
	}

	@Override
	public DiagramObject init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		DiagramObject type = (model != null) ? (DiagramObject)model : (DiagramObject)create();
		super.init(rs, type);
		((IColorizedObject)type).setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		return type;
	}
}
