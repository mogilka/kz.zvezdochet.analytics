package kz.zvezdochet.analytics.exporter;

import java.util.List;

import kz.zvezdochet.analytics.bean.Rule;
import kz.zvezdochet.analytics.service.RuleService;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;

/**
 * Набор правил толкования события
 * @author Nataly Didenko
 *
 */
public class EventRules {

	/**
	 * Планета-меч
	 * @param planet планета
	 * @param female true|false женский|мужской
	 * @return правило
	 * @throws DataAccessException 
	 */
	public static Rule rulePlanetSword(Planet planet, boolean female) throws DataAccessException {
		RuleService service = new RuleService();
		if (planet.getCode().equals("Venus")) {
			if (!planet.inMine() && !planet.isBroken() && !planet.isDamaged() && !planet.isRetrograde()
					&& !planet.isBelt() && !planet.isSignExile() && !planet.isSignDeclined())
				return (Rule)service.find(3L);
		} else if (planet.getCode().equals("Mars")) {
			if (planet.isNeutral()) {
				long id = female ? 67L : 66L;
				return (Rule)service.find(id);
			}
		}
		return null;
	}

	/**
	 * Дирекция планеты к дому
	 * @param spa аспект
	 * @param female true|false женский|мужской
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule ruleHouseDirection(SkyPointAspect spa, boolean female) throws DataAccessException {
		RuleService service = new RuleService();
		Planet planet = (Planet)spa.getSkyPoint1();
		House house = (House)spa.getSkyPoint2();
//		if (planet.getCode().equals("Moon") && house.getCode().equals("VII")) {
//			if (spa.getAspect().getType().getCode().equals("NEGATIVE")) {
//				return female ? (Rule)service.find(8L) : (Rule)service.find(9L);
//			}
//		}
		return null;
	}

	/**
	 * Аспект планет
	 * @param spa аспект
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule rulePlanetAspect(SkyPointAspect spa) throws DataAccessException {
		RuleService service = new RuleService();
		Planet planet = (Planet)spa.getSkyPoint1();
		Planet planet2 = (Planet)spa.getSkyPoint2();
		String code = spa.getAspect().getType().getCode();

		if (planet.getCode().equals("Moon") && planet2.getCode().equals("Pluto")) {
			if (code.equals("NEGATIVE")) {
				if (planet.isDamaged() || planet2.isDamaged())
					return (Rule)service.find(10L);
			}
		}
		if (planet.getCode().equals("Venus") && planet2.getCode().equals("Mars")) {
			if (code.equals("NEUTRAL")) {
				if (planet.isDamaged() || planet2.isDamaged())
					return (Rule)service.find(37L);
			}
		}
		return null;
	}

	/**
	 * Нахождение планеты в доме
	 * @param planet планета
	 * @param house дом
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule rulePlanetHouse(Planet planet, House house, boolean female) throws DataAccessException {
		RuleService service = new RuleService();
		String hcode = house.getCode();
		String pcode = planet.getCode();
		if (hcode.equals("I_3")) {
			if (pcode.equals("Sun")) {
				if (planet.getSign().getCode().equals("Aquarius"))
					return (Rule)service.find(26L);
			}
		} else if (hcode.equals("VII")) {
			if (pcode.equals("Rakhu")) {
				if (female && !planet.isNegative())
					return (Rule)service.find(70L);
			}
		}
		return null;
	}

	/**
	 * Планета-щит
	 * @param planet планета
	 * @param female true|false женский|мужской
	 * @return правило
	 * @throws DataAccessException 
	 */
	public static Rule rulePlanetShield(Planet planet, boolean female) throws DataAccessException {
//		RuleService service = new RuleService();
//		if (planet.getCode().equals("Venus")) {
//			if (!planet.inMine() && !planet.isBroken() && !planet.isDamaged() && !planet.isRetrograde()
//					&& !planet.isBelt() && !planet.isSignExile() && !planet.isSignDeclined())
//				return (Rule)service.find(3L);
//		} else if (planet.getCode().equals("Mars")) {
//			if (planet.isNeutral()) {
//				long id = female ? 67L : 66L;
//				return (Rule)service.find(id);
//			}
//		}
		return null;
	}

	/**
	 * Синастрический аспект
	 * @param spa аспект
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule ruleSynastryAspect(SkyPointAspect spa, Event partner) throws DataAccessException {
		RuleService service = new RuleService();
		Planet planet = (Planet)spa.getSkyPoint1();
		Planet planet2 = (Planet)spa.getSkyPoint2();
		String code = spa.getAspect().getType().getCode();

		List<Model> planets = partner.getConfiguration().getPlanets();
		if (planet.getCode().equals("Venus") && planet2.getCode().equals("Saturn")) {
			if (code.equals("POSITIVE")) {
				for (Model model : planets) {
					Planet p = (Planet)model;
					if (p.getCode().equals("Saturn") && p.isLord())
						return (Rule)service.find(68L);
				}
			}
		} else if (planet.getCode().equals("Moon") && planet2.getCode().equals("Mercury")) {
			if (code.equals("POSITIVE")) {
				for (Model model : planets) {
					Planet p = (Planet)model;
					if (p.getCode().equals("Mercury") && p.isLord())
						return (Rule)service.find(69L);
				}
			}
		}
		return null;
	}
}
