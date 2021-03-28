package kz.zvezdochet.analytics.exporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kz.zvezdochet.analytics.bean.Category;
import kz.zvezdochet.analytics.bean.Degree;
import kz.zvezdochet.analytics.bean.Rule;
import kz.zvezdochet.analytics.service.RuleService;
import kz.zvezdochet.bean.AspectConfiguration;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;

/**
 * Набор правил толкования события
 * @author Natalie Didenko
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
			if (planet.isPositive() && planet.isInNeutralSign())
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
//		} else if (spa.getAspect().getType().getCode().equals("NEUTRAL") //нереалистично
//				&& house.getCode().equals("II_2")) {
//			HouseSignText text = new HouseSignService().find(house, planet.getSign());
//			if (text != null) {
//				Rule rule = new Rule();
//				rule.setText(text.getText());
//				return rule;
//			}
		}
		return null;
	}

	/**
	 * Аспект планет
	 * @param spa аспект
	 * @param female true|false женский|мужской
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule rulePlanetAspect(SkyPointAspect spa, boolean female) throws DataAccessException {
		RuleService service = new RuleService();
		Planet planet = (Planet)spa.getSkyPoint1();
		Planet planet2 = (Planet)spa.getSkyPoint2();
		String code = spa.getAspect().getType().getCode();

		if (planet.getCode().equals("Sun") && planet2.getCode().equals("Moon")) {
			String acode = spa.getAspect().getCode();
			if (acode != null)
				if (female && spa.getAspect().getCode().equals("BELT")) {
					Map<String, Integer> map = planet2.getAspectCountMap();
					if (map.size() < 2) 
						return (Rule)service.find(144L);
				}
		}

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
		if (hcode.equals("I")) {
			if (pcode.equals("Mercury")) {
				if (planet.isWeak())
					return (Rule)service.find(186L);
				else if (planet.isDamaged())
					return (Rule)service.find(187L);
			}
		} else if (hcode.equals("I_3")) {
			if (pcode.equals("Sun")) {
				if (planet.getSign().getCode().equals("Aquarius"))
					return (Rule)service.find(26L);
			}
		} else if (hcode.equals("VI_2")) {
			if (pcode.equals("Mars")) {
				String scode = planet.getSign().getCode();
				if (scode.equals("Aries"))
					return (Rule)service.find(193L);
				else if (scode.equals("Taurus"))
					return (Rule)service.find(194L);
				else if (scode.equals("Gemini"))
					return (Rule)service.find(195L);
				else if (scode.equals("Cancer"))
					return (Rule)service.find(196L);
				else if (scode.equals("Leo"))
					return (Rule)service.find(197L);
				else if (scode.equals("Virgo"))
					return (Rule)service.find(198L);
				else if (scode.equals("Libra"))
					return (Rule)service.find(199L);
				else if (scode.equals("Scorpio"))
					return (Rule)service.find(200L);
				else if (scode.equals("Ophiuchus"))
					return (Rule)service.find(205L);
				else if (scode.equals("Sagittarius"))
					return (Rule)service.find(201L);
				else if (scode.equals("Capricornus"))
					return (Rule)service.find(202L);
				else if (scode.equals("Aquarius"))
					return (Rule)service.find(203L);
				else if (scode.equals("Pisces"))
					return (Rule)service.find(204L);
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
		} else if (planet.getCode().equals("Mars")) {
			if (planet.getSign().getCode().equals("Taurus"))
				return (Rule)service.find(96L);
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

		Collection<Planet> planets = partner.getPlanets().values();
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
			if (scode.equals("Aries")) {
				Collection<Planet> planets = event.getPlanets().values();
				for (Planet planet : planets) {
					if (planet.getCode().equals("Mars")) {
						String code = planet.getSign().getCode();
						if (code.equals("Aries"))
							return (Rule)service.find(132L);
						else if (code.equals("Taurus"))
							return (Rule)service.find(133L);
						else if (code.equals("Gemini"))
							return (Rule)service.find(134L);
						else if (code.equals("Cancer"))
							return (Rule)service.find(135L);
						else if (code.equals("Leo"))
							return (Rule)service.find(136L);
						else if (code.equals("Virgo"))
							return (Rule)service.find(137L);
						else if (code.equals("Libra"))
							return (Rule)service.find(138L);
						else if (code.equals("Scorpio"))
							return (Rule)service.find(139L);
						else if (code.equals("Sagittarius"))
							return (Rule)service.find(140L);
						else if (code.equals("Capricornus"))
							return (Rule)service.find(141L);
						else if (code.equals("Aquarius"))
							return (Rule)service.find(142L);
						else if (code.equals("Pisces"))
							return (Rule)service.find(143L);
					}
				}
			} else if (scode.equals("Sagittarius")) {
				Collection<Planet> planets = event.getPlanets().values();
				for (Planet planet : planets) {
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
			} else if (scode.equals("Capricornus")) {
				Collection<Planet> planets = event.getPlanets().values();
				for (Planet planet : planets) {
					if (planet.getCode().equals("Saturn")) {
						String code = planet.getSign().getCode();
						if (code.equals("Aries"))
							return (Rule)service.find(114L);
						else if (code.equals("Taurus"))
							return (Rule)service.find(115L);
						else if (code.equals("Gemini"))
							return (Rule)service.find(116L);
						else if (code.equals("Cancer"))
							return (Rule)service.find(117L);
						else if (code.equals("Leo"))
							return (Rule)service.find(118L);
						else if (code.equals("Virgo"))
							return (Rule)service.find(119L);
						else if (code.equals("Libra"))
							return (Rule)service.find(121L);
						else if (code.equals("Scorpio"))
							return (Rule)service.find(122L);
						else if (code.equals("Sagittarius"))
							return (Rule)service.find(123L);
						else if (code.equals("Capricornus"))
							return (Rule)service.find(124L);
						else if (code.equals("Aquarius"))
							return (Rule)service.find(125L);
						else if (code.equals("Pisces"))
							return (Rule)service.find(126L);
					}
				}
			}
		} else if (hcode.equals("VII")) {
			if (scode.equals("Leo")) {
				if (!event.isFemale())
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
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
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
	 * Селена, Лилит и лунные узлы в одном доме
	 * @param house дом
	 * @return правило
	 * @throws DataAccessException 
	 */
	public static Rule ruleMoonsHouse(House house) throws DataAccessException {
		RuleService service = new RuleService();
		if (house.isSelened()) {
			if (house.getCode().equals("I"))
				return (Rule)service.find(106L);
			else if (house.getCode().equals("III_2"))
				return (Rule)service.find(214L);
			else if (house.getCode().equals("X_3"))
				return (Rule)service.find(219L);
		}
		if (house.isRakhued()) {
			if (house.getCode().equals("V"))
				return (Rule)service.find(215L);
			else if (house.getCode().equals("XII_2"))
				return (Rule)service.find(216L);
			else if (house.getCode().equals("XII"))
				return (Rule)service.find(224L);
		}
		return null;
	}

	/**
	 * Нахождение планеты в знаке
	 * @param planet планета
	 * @param sign знак
	 * @param event гороскоп
	 * @param category категория
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule rulePlanetSign(Planet planet, Sign sign, Event event, Category category) throws DataAccessException {
		RuleService service = new RuleService();
		String pcode = planet.getCode();
		String scode = sign.getCode();

		if (pcode.equals("Venus")) {
			if (scode.equals("Taurus")) {
				if (planet.isDamaged() && event.isFemale())
					if (category.getCode().equals("love"))
						return (Rule)service.find(107L);
			}
		} else if (pcode.equals("Mars")) {
			if (scode.equals("Pisces")) {
				if (planet.isDamaged() && event.isFemale())
					if (category.getCode().equals("male"))
						return (Rule)service.find(185L);
			}
		}
		return null;
	}

	/**
	 * Дирекция планет
	 * @param spa аспект
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule ruleDirectionAspect(SkyPointAspect spa) throws DataAccessException {
		RuleService service = new RuleService();
		Planet planet = (Planet)spa.getSkyPoint1();
		Planet planet2 = (Planet)spa.getSkyPoint2();
		String code = spa.getAspect().getType().getCode();

		//TODO а откуда мы узнаем, что дирекционные планеты плохие?
		if (code.equals("NEGATIVE") &&
				(planet.getCode().equals("Moon") && planet2.getCode().equals("Venus"))
					|| (planet.getCode().equals("Venus") && planet2.getCode().equals("Moon"))) {
			if (planet.isDamaged() && planet2.isDamaged())
				return (Rule)service.find(129L);
		}
		return null;
	}

	/**
	 * Планета в шахте
	 * @param planet планета
	 * @param female true|false женский|мужской
	 * @return правило
	 * @throws DataAccessException 
	 */
	public static Rule rulePlanetMine(Planet planet, boolean female) throws DataAccessException {
		RuleService service = new RuleService();
		String pcode = planet.getCode();
		if (pcode.equals("Moon")) {
			Map<String, Integer> map = planet.getAspectCountMap();
			if (map.size() < 2) 
				return (Rule)service.find(144L);
		}
		return null;
	}

	/**
	 * Нахождение планеты партнёра в доме
	 * @param planet планета второго партнёра
	 * @param house дом первого партнёра
	 * @param female true - партнёр женского пола
	 * @return список правил
	 * @throws DataAccessException
	 */
	public static List<Rule> ruleSynastryPlanetHouse(Planet planet, House house, boolean female) throws DataAccessException {
		List<Rule> rules = new ArrayList<>();
		RuleService service = new RuleService();
		String hcode = house.getCode();
		String pcode = planet.getCode();
		if (hcode.equals("I")) {
			if (pcode.equals("Venus")) {
				if (planet.isKethued() || planet.isBroken())
					rules.add((Rule)service.find(150L));
				if (!female)
					rules.add((Rule)service.find(151L));
			}
		}
		return rules;
	}

	/**
	 * Конфигурация аспектов
	 * @param conf конфигурация
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule ruleConfiguration(AspectConfiguration conf) throws DataAccessException {
		Rule rule = null;
		RuleService service = new RuleService();
		String code = conf.getCode();
		if (code.equals("home")) {
			for (Planet planet : conf.getVertex()) {
				if (Arrays.asList(new Long[] {19L, 20L, 23L, 24L, 25L, 28L, 29L, 31L}).contains(planet.getId()))
					return (Rule)service.find(223L);
			}
		}
		return rule;
	}
}
