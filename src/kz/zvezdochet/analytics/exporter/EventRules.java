package kz.zvezdochet.analytics.exporter;

import kz.zvezdochet.analytics.bean.Rule;
import kz.zvezdochet.analytics.service.RuleService;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.SkyPointAspect;
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

	public static Rule ruleHouseDirection(SkyPointAspect spa, boolean female) throws DataAccessException {
		RuleService service = new RuleService();
		Planet planet = (Planet)spa.getSkyPoint1();
		House house = (House)spa.getSkyPoint2();
		if (planet.getCode().equals("Moon") && house.getCode().equals("VII")) {
			if (spa.getAspect().getType().getCode().equals("NEGATIVE")) {
				return female ? (Rule)service.find(8L) : (Rule)service.find(9L);
			}
		}
		return null;
	}

	public static Rule rulePlanetAspect(SkyPointAspect spa) throws DataAccessException {
		RuleService service = new RuleService();
		Planet planet = (Planet)spa.getSkyPoint1();
		Planet planet2 = (Planet)spa.getSkyPoint2();
		if (planet.getCode().equals("Moon") && planet2.getCode().equals("Pluto")) {
			if (spa.getAspect().getType().getCode().equals("NEGATIVE")) {
				if (planet.isDamaged() || planet2.isDamaged())
					return (Rule)service.find(10L);
			}
		}
		return null;
	}
}
