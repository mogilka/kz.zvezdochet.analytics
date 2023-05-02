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
			if (planet.isInNeutralSign()) {
				long id = female ? 24L : 23L;
				return (Rule)service.find(id);
			}

		} else if (pcode.equals("Mercury")) {
			if (planet.getSign().getCode().equals("Gemini"))
				return (Rule)service.find(6L);
			if (planet.isUnaspected())
				return (Rule)service.find(7L);

		} else if (pcode.equals("Saturn")) {
			if (planet.isDamaged())
				return (Rule)service.find(37L);

		} else if (pcode.equals("Chiron")) {
			if (planet.isDamaged())
				return (Rule)service.find(32L);

		} else if (pcode.equals("Uranus")) {
			if (planet.isDamaged())
				return (Rule)service.find(33L);
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
						return (Rule)service.find(35L);
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

		if (planet.getCode().equals("Sun")) {
			if (planet2.getCode().equals("Moon")) {
				String acode = spa.getAspect().getCode();
				if (acode != null) {
					if (female && acode.equals("BELT")) {
						Map<String, Integer> map = planet2.getAspectCountMap();
						if (map.size() < 2) 
							return (Rule)service.find(144L);
					}
				}
				if (code.equals("NEUTRAL")) {
					if (planet.isRakhued())
						return (Rule)service.find(13L);
					if (planet.getSign().getCode().equals("Pisces"))
						return (Rule)service.find(1L);
				}
			} else if (planet2.getCode().equals("Mars")) {
				if (code.equals("NEGATIVE")) {
					String scode = planet.getSign().getCode();
					if (Arrays.asList(new String[] {"Aries", "Cancer", "Libra", "Capricornus"}).contains(scode))
						return (Rule)service.find(14L);
					if (Arrays.asList(new String[] {"Gemini", "Virgo", "Sagittarius", "Pisces"}).contains(scode))
						return (Rule)service.find(15L);
					if (Arrays.asList(new String[] {"Taurus", "Leo", "Scorpio", "Ophiuchus", "Aquarius"}).contains(scode))
						return (Rule)service.find(16L);
				}
			}
		}

		if (planet.getCode().equals("Moon")) {
			if (planet2.getCode().equals("Pluto")) {
				if (code.equals("NEGATIVE")) {
					if (planet.isDamaged() || planet2.isDamaged())
						return (Rule)service.find(10L);
				} else if (code.equals("NEUTRAL")) {
					Map<String, String> aspectMap = planet.getAspectMap();
					String pcode3 = "Neptune";
					if (aspectMap.containsKey(pcode3)) {
						String acode = aspectMap.get(pcode3);
						if (acode.equals("CONJUNCTION"))
							return (Rule)service.find(19L);
					}
				}
			} else if (planet2.getCode().equals("Mars")) {
				if (code.equals("NEUTRAL")) {
					if (planet.getLongitude() > planet2.getLongitude()
							&& planet.getSign().getId().equals(planet2.getSign().getId()))
						return (Rule)service.find(17L);
				}
			} else if (planet2.getCode().equals("Saturn")) {
				if (code.equals("NEUTRAL")) {
					if (planet.isDamaged())
						return (Rule)service.find(18L);
				}
			} else if (planet2.getCode().equals("Venus")) {
				if (code.equals("POSITIVE")) {
					Map<String, String> aspectMap = planet.getAspectMap();
					String pcode3 = "Neptune";
					if (aspectMap.containsKey(pcode3)) {
						String acode = aspectMap.get(pcode3);
						if (Arrays.asList(new String[] {"SEXTILE", "TRIN"}).contains(acode))
							return (Rule)service.find(20L);
					}
				}
			}
		}
		if (planet.getCode().equals("Venus")) {
			if (planet2.getCode().equals("Mars")) {
				if (code.equals("NEUTRAL")) {
					if (planet.isDamaged() || planet2.isDamaged())
						return (Rule)service.find(37L);
				}
			}
		}
		return null;
	}

	/**
	 * Нахождение планеты в доме
	 * @param planet планета
	 * @param house дом
	 * @return массив правил
	 * @throws DataAccessException
	 */
	public static List<Rule> rulePlanetHouse(Planet planet, House house, boolean female) throws DataAccessException {
		List<Rule> rules = new ArrayList<Rule>();
		RuleService service = new RuleService();
		String hcode = house.getCode();
		String pcode = planet.getCode();

		if (hcode.equals("I")) {
			if (pcode.equals("Uranus")) {
				if (planet.isRetrograde())
					rules.add((Rule)service.find(44L));
			}

		} else if (hcode.equals("I_3")) {
			if (pcode.equals("Sun")) {
				if (planet.getSign().getCode().equals("Aquarius"))
					rules.add((Rule)service.find(26L));
			}

		} else if (hcode.equals("II")) {
			if (pcode.equals("Moon")) {
				Map<String, String> aspectMap = planet.getAspectMap();
				String pcode2 = "Venus";
				if (aspectMap.containsKey(pcode2)) {
					String acode = aspectMap.get(pcode2);
					if (Arrays.asList(new String[] {"SEXTILE", "TRIN"}).contains(acode))
						rules.add((Rule)service.find(235L));
				}
				pcode2 = "Mercury";
				if (aspectMap.containsKey(pcode2)) {
					String acode = aspectMap.get(pcode2);
					if (Arrays.asList(new String[] {"OPPOSITION", "QUADRATURE"}).contains(acode))
						rules.add((Rule)service.find(236L));
				}
				pcode2 = "Saturn";
				if (aspectMap.containsKey(pcode2)) {
					String acode = aspectMap.get(pcode2);
					if (Arrays.asList(new String[] {"OPPOSITION", "QUADRATURE"}).contains(acode))
						rules.add((Rule)service.find(237L));
				}
			}

		} else if (hcode.equals("IV")) {
			if (pcode.equals("Moon")) {
				if (planet.getSign().getCode().equals("Capricornus"))
					rules.add((Rule)service.find(46L));
			}

		} else if (hcode.equals("IV_2")) {
			if (pcode.equals("Mars")) {
				if (planet.isPerfect())
					rules.add((Rule)service.find(22L));
			}

		} else if (hcode.equals("VII")) {
			if (pcode.equals("Rakhu")) {
				if (female && !planet.isNegative())
					rules.add((Rule)service.find(70L));
			}

		} else if (hcode.equals("VII_3")) {
			if (pcode.equals("Mercury")) {
				Map<String, String> aspectMap = planet.getAspectMap();
				String pcode2 = "Moon";
				if (aspectMap.containsKey(pcode2)) {
					String acode = aspectMap.get(pcode2);
					if (Arrays.asList(new String[] {"OPPOSITION", "QUADRATURE"}).contains(acode))
						rules.add((Rule)service.find(238L));
				}
			}

		} else if (hcode.equals("X")) {
			if (pcode.equals("Uranus")) {
				if (planet.isRetrograde())
					rules.add((Rule)service.find(44L));
			}
		}
		return rules;
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
				return (Rule)service.find(29L);
			else if (planet.isDamaged())
				return (Rule)service.find(30L);

		} else if (planet.getCode().equals("Mars")) {
			if (planet.getSign().getCode().equals("Taurus"))
				return (Rule)service.find(31L);
			if (female &&
					(planet.getSign().getCode().equals("Taurus")
						|| planet.getSign().getCode().equals("Libra")))
				return (Rule)service.find(12L);

		} else if (planet.getCode().equals("Venus")) {
			if (!female &&
					(planet.getSign().getCode().equals("Taurus")
						|| planet.getSign().getCode().equals("Libra")))
				return (Rule)service.find(2L);
		}
		return null;
	}

	/**
	 * Синастрический аспект
	 * @param spa аспект
	 * @param event первый партнёр
	 * @param partner второй партнёр
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule ruleSynastryAspect(SkyPointAspect spa, Event event, Event partner) throws DataAccessException {
		RuleService service = new RuleService();
		Planet planet = (Planet)spa.getSkyPoint1();
		Planet planet2 = (Planet)spa.getSkyPoint2();
		String code = spa.getAspect().getType().getCode();

		Collection<Planet> planets = partner.getPlanets().values();
		if (planet.getCode().equals("Moon")) {
			if (planet2.getCode().equals("Mercury")) {
				if (code.equals("POSITIVE")) {
					for (Model model : planets) {
						Planet p = (Planet)model;
						if (p.getCode().equals("Mercury") && p.isDominant())
							return (Rule)service.find(69L);
					}
				} else if (code.equals("NEGATIVE")
						&& event.isFemale()
						&& partner.isFemale())
					return (Rule)service.find(28L);
			} else if (planet2.getCode().equals("Venus")) {
				if (code.equals("NEGATIVE")
						&& event.isFemale()
						&& partner.isFemale())
					return (Rule)service.find(241L);
			}
		} else if (planet.getCode().equals("Venus")) {
			if (planet2.getCode().equals("Venus")) {
				if (code.equals("NEUTRAL")
						&& event.isFemale()
						&& partner.isFemale())
					return (Rule)service.find(240L);
			} else if (planet2.getCode().equals("Saturn")) {
				if (code.equals("POSITIVE")) {
					for (Model model : planets) {
						Planet p = (Planet)model;
						if (p.getCode().equals("Saturn") && p.isDominant())
							return (Rule)service.find(68L);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Вид космограммы
	 * @param code код конфигурации
	 * @param planet планета
	 * @param direction направление East|West|North|South
	 * @return правило
	 * @throws DataAccessException 
	 */
	public static Rule ruleCardKind(String code, Planet planet, String direction) throws DataAccessException {
		RuleService service = new RuleService();
		if (code.equals("sling")) {
			if (planet.getCode().equals("Chiron")) {
				if (planet.isNegative() && direction.equals("West"))
					return (Rule)service.find(25L);
				else
					return (Rule)service.find(26L);
			} else if (planet.isPositive() && direction.equals("East"))
				return (Rule)service.find(27L);
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
		long id = degree.getId();
		if (16 == id) {
			//ASC = Венера — способности в искусстве и естественных науках.
			//ASC + Венера — способности в искусстве и естественных науках.
			Map<Long, Planet> planets = event.getPlanets();
			Planet planet = planets.get(24l);
			List<SkyPointAspect> aspects = planet.getAspectHouseList();
			for (SkyPointAspect spa : aspects) {
				if (spa.getSkyPoint2().getId() > 142)
					continue;

				long aid = spa.getAspect().getTypeid();
				if (1 == aid || 3 == aid)
					return (Rule)service.find(11L);
			}
		} else if (147 == id) {
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				String pcode = planet.getCode();
				if (pcode.equals("Mars") || pcode.equals("Uranus")) {
					if (planet.isDominant() || planet.isRakhued())
						return (Rule)service.find(28L);
					else {
						Iterator<Map.Entry<String, Double>> iterator = signMap.entrySet().iterator();
					    while (iterator.hasNext()) {
					    	Entry<String, Double> entry = iterator.next();
					    	String scode = entry.getKey();
					    	if (scode.equals("Scorpio") || scode.equals("Ophiuchus"))
					    		return (Rule)service.find(28L);
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
			if (house.isLilithed()) {
				if (house.getCode().equals("I"))
					return (Rule)service.find(106L);
				else if (house.getCode().equals("III_2"))
					return (Rule)service.find(214L);
				else if (house.getCode().equals("X_3"))
					return (Rule)service.find(219L);
				else if (house.getCode().equals("XI"))
					return (Rule)service.find(225L);
				else if (house.getCode().equals("XI_2"))
					return (Rule)service.find(230L);
			} else if (house.isKethued()) {
				if (house.getCode().equals("III_2"))
					return (Rule)service.find(233L);
				if (house.getCode().equals("VI_2"))
					return (Rule)service.find(231L);
				else if (house.getCode().equals("IX"))
					return (Rule)service.find(228L);
				else if (house.getCode().equals("X_2"))
					return (Rule)service.find(232L);
			}
		}
		if (house.isRakhued()) {
			if (house.getCode().equals("III"))
				return (Rule)service.find(239L);
			else if (house.getCode().equals("V"))
				return (Rule)service.find(215L);
			else if (house.getCode().equals("IX_2"))
				return (Rule)service.find(234L);
			else if (house.getCode().equals("XI_2"))
				return (Rule)service.find(229L);
			else if (house.getCode().equals("XII"))
				return (Rule)service.find(224L);
			else if (house.getCode().equals("XII_2"))
				return (Rule)service.find(216L);
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
						return (Rule)service.find(36L);
			}
		} else if (pcode.equals("Mars")) {
			if (scode.equals("Pisces")) {
				if (planet.isDamaged()) {
					if (event.isFemale() && category.getCode().equals("male"))
						return (Rule)service.find(185L);
					else
						return (Rule)service.find(43L);
				}
			}

		} else if (pcode.equals("Moon")) {
			if (scode.equals("Cancer")) {
				Planet planet2 = event.getPlanets().get(23L);
				if (planet2.getSign().getCode().equals("Cancer"))
					return (Rule)service.find(21L);
			}

		} else if (pcode.equals("Mercury")) {
			if (scode.equals("Gemini")) {
				if (planet.isDamaged())
					return (Rule)service.find(42L);
			} else if (scode.equals("Pisces")) {
				if (planet.isDamaged())
					return (Rule)service.find(41L);
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
	 * @param event персона
	 * @return правило
	 * @throws DataAccessException 
	 */
	public static Rule rulePlanetUnaspected(Planet planet, Event event) throws DataAccessException {
		RuleService service = new RuleService();
		String pcode = planet.getCode();
		if (pcode.equals("Moon")) {
			Map<String, Integer> map = planet.getAspectCountMap();
			if (map.size() < 2) 
				return (Rule)service.find(144L);

		} else if (pcode.equals("Rakhu")) {
			House house = planet.getHouse();
			if (house.getCode().equals("X_2")) 
				return (Rule)service.find(4L);

		} else if (pcode.equals("Sun")) {
			boolean strong = false;
			if (planet.getSign().getCode().equals("Leo"))
				strong = true;
			else {
				House house = planet.getHouse();
				if (house.getCode().equals("V") || house.getCode().equals("V_2") || house.getCode().equals("V_3"))
					strong = true;
			}
			if (strong)
				return (Rule)service.find(9L);

		} else if (pcode.equals("Pluto")) {
			Planet sun = event.getPlanets().get(19L);
			Planet moon = event.getPlanets().get(20L);
			Planet mercury = event.getPlanets().get(23L);
			Planet venus = event.getPlanets().get(24L);
			Planet mars = event.getPlanets().get(25L);
			if (sun.isWeak() || moon.isWeak() || mercury.isWeak() || venus.isWeak() || mars.isWeak()) 
				return (Rule)service.find(39L);
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
				if (Arrays.asList(new Long[] {19L, 20L, 24L, 25L, 29L}).contains(planet.getId()))
					return (Rule)service.find(45L);
			}
		}
		return rule;
	}
}
