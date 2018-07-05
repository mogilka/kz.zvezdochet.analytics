package kz.zvezdochet.analytics.exporter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kz.zvezdochet.analytics.bean.CardKind;
import kz.zvezdochet.analytics.bean.Degree;
import kz.zvezdochet.analytics.bean.Rule;
import kz.zvezdochet.analytics.service.RuleService;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
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
		String pcode = planet.getCode();
		if (pcode.equals("Venus")) {
			if (!planet.inMine() && !planet.isBroken() && !planet.isDamaged() && !planet.isRetrograde()
					&& !planet.isBelt() && !planet.isSignExile() && !planet.isSignDeclined())
				return (Rule)service.find(3L);
		} else if (pcode.equals("Mars")) {
			if (planet.isNeutral()) {
				long id = female ? 67L : 66L;
				return (Rule)service.find(id);
			}
		} else if (pcode.equals("Saturn")) {
			if (planet.isDamaged())
				return (Rule)service.find(108L);
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
		if (planet.getCode().equals("Moon")) {
			if (house.getCode().equals("II")) {
				if (spa.getAspect().getType().getCode().equals("POSITIVE")) {
					if (planet.getSign().getCode().equals("Taurus"))
						return (Rule)service.find(105L);
				}
			}
		}
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
		RuleService service = new RuleService();
		if (planet.getCode().equals("Neptune")) {
			if (planet.isBroken() || planet.isKethued())
				return (Rule)service.find(96L);
			else if (planet.isDamaged())
				return (Rule)service.find(97L);
		}
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

	/**
	 * Нахождение дома в знаке
	 * @param house дом
	 * @param sign знак
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule ruleHouseSign(House house, Sign sign, Event event) throws DataAccessException {
		RuleService service = new RuleService();
		String hcode = house.getCode();
		String scode = sign.getCode();

		if (hcode.equals("I")) {
			if (scode.equals("Sagittarius")) {
				List<Model> planets = event.getConfiguration().getPlanets();
				for (Model model : planets) {
					Planet planet = (Planet)model;
					if (planet.getCode().equals("Jupiter")) {
						String code = planet.getSign().getCode();
						if (code.equals("Aries"))
							return (Rule)service.find(71L);
						else if (code.equals("Taurus"))
							return (Rule)service.find(72L);
						else if (code.equals("Gemini"))
							return (Rule)service.find(73L);
						else if (code.equals("Cancer"))
							return (Rule)service.find(74L);
						else if (code.equals("Leo"))
							return (Rule)service.find(75L);
						else if (code.equals("Virgo"))
							return (Rule)service.find(76L);
						else if (code.equals("Libra"))
							return (Rule)service.find(77L);
						else if (code.equals("Scorpio"))
							return (Rule)service.find(78L);
						else if (code.equals("Ophiuchus"))
							return (Rule)service.find(79L);
						else if (code.equals("Sagittarius"))
							return (Rule)service.find(83L);
						else if (code.equals("Capricornus"))
							return (Rule)service.find(80L);
						else if (code.equals("Aquarius"))
							return (Rule)service.find(81L);
						else if (code.equals("Pisces"))
							return (Rule)service.find(82L);
					}
				}
			}
		} else if (hcode.equals("VII")) {
			if (scode.equals("Leo")) {
				if (event.isFemale())
					return (Rule)service.find(109L);
			}
		}
		return null;
	}

	/**
	 * Вид космограммы
	 * @param planet планета
	 * @return правило
	 * @throws DataAccessException 
	 */
	public static Rule ruleCardKind(Planet planet) throws DataAccessException {
		RuleService service = new RuleService();
		if (planet.getCode().equals("Chiron")) {
			if (planet.isNegative())
				return (Rule)service.find(84L);
			else
				return (Rule)service.find(85L);
		}
		return null;
	}

	/**
	 * Градус рождения
	 * @param planet планета
	 * @return правило
	 * @throws DataAccessException 
	 */
	public static Rule ruleDegree(Degree degree, Event event, Map<String, Double> signMap) throws DataAccessException {
		RuleService service = new RuleService();
		if (147 == degree.getId()) {
			List<Model> planets = event.getConfiguration().getPlanets();
			for (Model model : planets) {
				Planet planet = (Planet)model;
				String pcode = planet.getCode();
				if (pcode.equals("Mars") || pcode.equals("Uranus")) {
					if (planet.isLord() || planet.isRakhued())
						return (Rule)service.find(95L);
					else {
						Iterator<Map.Entry<String, Double>> iterator = signMap.entrySet().iterator();
					    while (iterator.hasNext()) {
					    	Entry<String, Double> entry = iterator.next();
					    	String scode = entry.getKey();
					    	if (scode.equals("Scorpio") || scode.equals("Ophiuchus"))
					    		return (Rule)service.find(95L);
					    }
					}
				}				
			}
		}
		return null;
	}

	/**
	 * Селена и Лилит в одном доме
	 * @param house дом
	 * @return правило
	 * @throws DataAccessException 
	 */
	public static Rule ruleMoonsHouse(House house) throws DataAccessException {
		RuleService service = new RuleService();
		if (house.getCode().equals("I"))
			return (Rule)service.find(106L);
		return null;
	}

	/**
	 * Нахождение планеты в знаке
	 * @param planet планета
	 * @param sign знак
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule rulePlanetSign(Planet planet, Sign sign, Event event) throws DataAccessException {
		RuleService service = new RuleService();
		String pcode = planet.getCode();
		String scode = sign.getCode();

		if (pcode.equals("Venus")) {
			if (scode.equals("Taurus")) {
				if (planet.isDamaged() && event.isFemale())
					return (Rule)service.find(107L);
			}
		}
		return null;
	}

	/**
	 * Вид космограммы
	 * @param kind вид космограммы
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule ruleCardKind(CardKind kind) throws DataAccessException {
		RuleService service = new RuleService();
		if (6 == kind.getId()) {
			String dir = kind.getDirection();
			if (dir.equals("up"))
				return (Rule)service.find(110L);
			else if (dir.equals("down"))
				return (Rule)service.find(111L);
			else if (dir.equals("right"))
				return (Rule)service.find(112L);
			else if (dir.equals("left"))
				return (Rule)service.find(113L);
		}
		return null;
	}
}
