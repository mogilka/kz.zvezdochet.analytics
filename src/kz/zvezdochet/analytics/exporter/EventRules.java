package kz.zvezdochet.analytics.exporter;

import kz.zvezdochet.analytics.bean.Rule;
import kz.zvezdochet.analytics.service.RuleService;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.service.DataAccessException;

/**
 * Набор правил толкования события
 * @author Nataly Didenko
 *
 */
public class EventRules {

	/**
	 * Вычисление выраженных знаков Зодиака
	 * @param main признак того, что нужно учитывать только минорные планеты
	 * @return карта приоритетных знаков
	 * @throws DataAccessException 
	 */
	public static Rule rulePlanetSword(Planet planet) throws DataAccessException {
		RuleService service = new RuleService();
		if (planet.getCode().equals("Venus")) {
			if (!planet.inMine() && !planet.isBroken() && !planet.isDamaged() && !planet.isRetrograde()
					&& !planet.isBelt() && !planet.isSignExile() && !planet.isSignDeclined())
				return (Rule)service.find(3L);
		}
		return null;
	}
}
