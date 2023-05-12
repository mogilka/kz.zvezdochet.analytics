package kz.zvezdochet.analytics.exporter;

import java.util.ArrayList;
import java.util.Arrays;
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
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.util.CalcUtil;

/**
 * Набор правил толкования события
 * @author Natalie Didenko
 *
 */
public class EventRules {

	/**
	 * Планета-меч
	 * @param planet планета
	 * @param event персона
	 * @return правила
	 * @throws DataAccessException 
	 */
	public static List<Rule> rulePlanetSword(Planet planet, Event event) throws DataAccessException {
		List<Rule> rules = new ArrayList<>();
		RuleService service = new RuleService();
		String pcode = planet.getCode();
		if (pcode.equals("Venus")) {
			if (planet.isPositive() && planet.isInNeutralSign())
				rules.add((Rule)service.find(3L));

		} else if (pcode.equals("Mars")) {
			if (planet.isInNeutralSign()) {
				long id = event.isFemale() ? 24L : 23L;
				rules.add((Rule)service.find(id));
			}

		} else if (pcode.equals("Mercury")) {
			if (planet.isUnaspected())
				rules.add((Rule)service.find(7L));

			if (planet.getSign().getCode().equals("Gemini"))
				rules.add((Rule)service.find(6L));

			Planet sun = event.getPlanets().get(19L);
			if (Double.compare(planet.getLongitude(), sun.getLongitude()) > 8) 
				rules.add((Rule)service.find(8L));

		} else if (pcode.equals("Saturn")) {
			if (planet.isDamaged())
				rules.add((Rule)service.find(37L));

		} else if (pcode.equals("Chiron")) {
			if (planet.isDamaged())
				rules.add((Rule)service.find(32L));

		} else if (pcode.equals("Uranus")) {
			if (planet.isDamaged())
				rules.add((Rule)service.find(33L));
		}
		return rules;
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
	public static List<Rule> rulePlanetAspect(SkyPointAspect spa, boolean female) throws DataAccessException {
		List<Rule> rules = new ArrayList<>();
		RuleService service = new RuleService();
		Planet planet = (Planet)spa.getSkyPoint1();
		Planet planet2 = (Planet)spa.getSkyPoint2();
		String code = spa.getAspect().getType().getCode();

		if (planet.getCode().equals("Sun")) {
			if (planet2.getCode().equals("Moon")) {
				if (code.equals("NEUTRAL")) {
					if (planet.isRakhued())
						rules.add((Rule)service.find(13L));

					if (planet.getSign().getCode().equals("Pisces"))
						rules.add((Rule)service.find(1L));
				}
			} else if (planet2.getCode().equals("Mars")) {
				if (code.equals("NEGATIVE")) {
					String scode = planet.getSign().getCode();
					if (Arrays.asList(new String[] {"Aries", "Cancer", "Libra", "Capricornus"}).contains(scode))
						rules.add((Rule)service.find(14L));
					else if (Arrays.asList(new String[] {"Gemini", "Virgo", "Sagittarius", "Pisces"}).contains(scode))
						rules.add((Rule)service.find(15L));
					else if (Arrays.asList(new String[] {"Taurus", "Leo", "Scorpio", "Ophiuchus", "Aquarius"}).contains(scode))
						rules.add((Rule)service.find(16L));
				}
			}
		}

		if (planet.getCode().equals("Moon")) {
			if (planet2.getCode().equals("Pluto")) {
				if (code.equals("NEGATIVE")) {
					if (planet.isDamaged() || planet2.isDamaged())
						rules.add((Rule)service.find(10L));

				} else if (code.equals("NEUTRAL")) {
					Map<String, String> aspectMap = planet.getAspectMap();
					String pcode3 = "Neptune";
					if (aspectMap.containsKey(pcode3)) {
						String acode = aspectMap.get(pcode3);
						if (acode.equals("CONJUNCTION"))
							rules.add((Rule)service.find(19L));
					}
				}
			} else if (planet2.getCode().equals("Mars")) {
				if (code.equals("NEUTRAL")) {
					if (planet.getLongitude() > planet2.getLongitude()
							&& planet.getSign().getId().equals(planet2.getSign().getId()))
						rules.add((Rule)service.find(17L));
				}

			} else if (planet2.getCode().equals("Saturn")) {
				if (code.equals("NEUTRAL")) {
					if (planet.isDamaged())
						rules.add((Rule)service.find(18L));
				}

			} else if (planet2.getCode().equals("Venus")) {
				if (code.equals("POSITIVE")) {
					Map<String, String> aspectMap = planet.getAspectMap();
					String pcode3 = "Neptune";
					if (aspectMap.containsKey(pcode3)) {
						String acode = aspectMap.get(pcode3);
						if (Arrays.asList(new String[] {"SEXTILE", "TRIN"}).contains(acode))
							rules.add((Rule)service.find(20L));
					}
				}
			}
		}
		if (planet.getCode().equals("Venus")) {
			if (planet2.getCode().equals("Mars")) {
				if (code.equals("NEUTRAL")) {
					if (planet.isDamaged() || planet2.isDamaged())
						rules.add((Rule)service.find(37L));
				}
			}
		}
		return rules;
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

			} else if (pcode.equals("Kethu")) {
				if (planet.isNegative())
					rules.add((Rule)service.find(79L));
			}

		} else if (hcode.equals("I_3")) {
			if (pcode.equals("Sun")) {
				if (planet.getSign().getCode().equals("Aquarius"))
					rules.add((Rule)service.find(26L));
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

		} else if (hcode.equals("X")) {
			if (pcode.equals("Uranus")) {
				if (planet.isRetrograde())
					rules.add((Rule)service.find(44L));
			}

		} else if (hcode.equals("XII_2")) {
			if (pcode.equals("Mars")) {
				if (planet.isWeak())
					rules.add((Rule)service.find(48L));
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
	public static List<Rule> rulePlanetShield(Planet planet, boolean female) throws DataAccessException {
		List<Rule> rules = new ArrayList<>();
		RuleService service = new RuleService();
		if (planet.getCode().equals("Neptune")) {
			if (planet.isBroken() || planet.isKethued())
				rules.add((Rule)service.find(29L));
			else if (planet.isDamaged())
				rules.add((Rule)service.find(30L));

		} else if (planet.getCode().equals("Mars")) {
			if (planet.getSign().getCode().equals("Taurus"))
				rules.add((Rule)service.find(31L));

			if (female && (planet.getSign().getCode().equals("Taurus")
					|| planet.getSign().getCode().equals("Libra")))
				rules.add((Rule)service.find(12L));

		} else if (planet.getCode().equals("Venus")) {
			if (!female &&
					(planet.getSign().getCode().equals("Taurus")
						|| planet.getSign().getCode().equals("Libra")))
				rules.add((Rule)service.find(2L));
		}
		return rules;
	}

	/**
	 * Синастрический аспект
	 * @param spa аспект
	 * @param event первый партнёр
	 * @param partner второй партнёр
	 * @return правило
	 * @throws DataAccessException
	 */
	public static List<Rule> ruleSynastryAspect(SkyPointAspect spa, Event event, Event partner) throws DataAccessException {
		List<Rule> rules = new ArrayList<>();
		RuleService service = new RuleService();
		Planet planet = (Planet)spa.getSkyPoint1();
		Planet planet2 = (Planet)spa.getSkyPoint2();
		String code = spa.getAspect().getType().getCode();

		Map<Long, Planet> planets2 = partner.getPlanets();
		if (planet.getCode().equals("Sun")) {
			if (planet2.getCode().equals("Jupiter")) {
				if (code.equals("NEUTRAL")) {
					if (planet.isStrong() && planet2.isWeak())
						rules.add((Rule)service.find(52L));

					Planet mercury = planets2.get(23L);
					if (mercury.isDominant())
						rules.add((Rule)service.find(69L));

				} else if (code.equals("NEGATIVE")
						&& event.isFemale()
						&& partner.isFemale())
					rules.add((Rule)service.find(28L));

			} else if (planet2.getCode().equals("Sun")) {
				if (code.equals("NEUTRAL")
						&& planet.isStrong()
						&& planet2.isWeak())
					rules.add((Rule)service.find(51L));

			} else if (planet2.getCode().equals("Mars")) {
				if (code.equals("POSITIVE")
						&& !event.isFemale()
						&& !partner.isFemale())
					rules.add((Rule)service.find(78L));
			}

		} else if (planet.getCode().equals("Moon")) {
			if (planet2.getCode().equals("Mercury")) {
				if (code.equals("POSITIVE")) {
					Planet mercury = event.getPlanets().get(23L);
					if (mercury.isDominant())
						rules.add((Rule)service.find(69L));

				} else if (code.equals("NEGATIVE")
						&& event.isFemale()
						&& partner.isFemale())
					rules.add((Rule)service.find(28L));

			} else if (planet2.getCode().equals("Venus")) {
				if (code.equals("NEGATIVE")
						&& event.isFemale()
						&& partner.isFemale())
					rules.add((Rule)service.find(69L));

				if (code.equals("NEUTRAL")
						&& planet2.isDamaged())
					rules.add((Rule)service.find(53L));

			} else if (planet2.getCode().equals("Mars")) {
				if (code.equals("NEUTRAL")
						&& planet.getSign().getCode().equals("Capricornus")
						&& planet.getSign().getId().equals(planet2.getSign().getId()))
					rules.add((Rule)service.find(54L));
			}

		} else if (planet.getCode().equals("Mercury")) {
			if (planet2.getCode().equals("Jupiter")) {
				if (code.equals("NEUTRAL")) {
					if (planet.isWeak())
						rules.add((Rule)service.find(55L));

					if (planet2.isStrong())
						rules.add((Rule)service.find(56L));
				}
			} else if (planet2.getCode().equals("Rakhu")) {
				if (code.equals("NEUTRAL")) {
					if (planet2.isWeak())
						rules.add((Rule)service.find(66L));
				}
			}

		} else if (planet.getCode().equals("Venus")) {
			if (planet2.getCode().equals("Venus")) {
				if (code.equals("NEUTRAL")
						&& event.isFemale()
						&& partner.isFemale())
					rules.add((Rule)service.find(68L));

			} else if (planet2.getCode().equals("Mars")) {
				if (code.equals("NEUTRAL")) {
					if (planet.isNegative() || planet2.isNegative())
						rules.add((Rule)service.find(49L));

					if (planet2.isStrong())
						rules.add((Rule)service.find(57L));

					if (planet.isWeak())
						rules.add((Rule)service.find(58L));
				}
			} else if (planet2.getCode().equals("Saturn")) {
				if (code.equals("POSITIVE")) {
					Planet saturn = event.getPlanets().get(29L);
					if (saturn.isDominant())
						rules.add((Rule)service.find(68L));
				}				
			}

		} else if (planet.getCode().equals("Mars")) {
			if (planet2.getCode().equals("Saturn")) {
				if (code.equals("NEUTRAL")) {
					if (planet.isDamaged())
						rules.add((Rule)service.find(50L));

					if (planet2.isPositive())
						rules.add((Rule)service.find(64L));

					if (planet.isPositive())
						rules.add((Rule)service.find(65L));
				}
			} else if (planet2.getCode().equals("Mars")) {
				if (code.equals("NEUTRAL")) {
					if (planet.isStrong())
						rules.add((Rule)service.find(59L));

					if (planet2.isStrong())
						rules.add((Rule)service.find(60L));
				}

			} else if (planet2.getCode().equals("Jupiter")) {
				if (code.equals("NEUTRAL")) {
					if (planet2.isStrong())
						rules.add((Rule)service.find(61L));

					if (planet2.isDamaged())
						rules.add((Rule)service.find(62L));

					if (planet.isDamaged())
						rules.add((Rule)service.find(63L));
				}

			} else if (planet2.getCode().equals("Rakhu")) {
				if (code.equals("NEUTRAL")) {
					if (planet.isDamaged()
							&& !event.isFemale()
							&& partner.isFemale())
						rules.add((Rule)service.find(67L));
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
			Planet planet = event.getPlanets().get(24l);
			List<SkyPointAspect> aspects = planet.getAspectHouseList();
			for (SkyPointAspect spa : aspects) {
				if (spa.getSkyPoint2().getId() > 142)
					continue;

				long aid = spa.getAspect().getTypeid();
				if (1 == aid || 3 == aid)
					return (Rule)service.find(11L);
			}

		} else if (96 == id) {
			EventStatistics statistics = new EventStatistics(event);
			statistics.initPlanetDivisions();
			Map<String, Double> planetMap = statistics.getPlanetElements();
			double val = planetMap.get("air");
			if (val > 4)
				return (Rule)service.find(5L);

		} else if (110 == id) {
			EventStatistics statistics = new EventStatistics(event);
			statistics.initPlanetDivisions();
			Map<String, Double> planetMap = statistics.getPlanetElements();
			double water = planetMap.get("water");
			double earth = planetMap.get("earth");
			if (water > 4 || earth > 4)
				return (Rule)service.find(70L);

		} else if (147 == id) {
			Planet mars = event.getPlanets().get(25L);
			Planet uranus = event.getPlanets().get(31L);
			if (mars.isDominant() || mars.isRakhued()
					|| uranus.isDominant() || uranus.isRakhued())
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
	 * Планета в шахте
	 * @param planet планета
	 * @param event персона
	 * @return правило
	 * @throws DataAccessException 
	 */
	public static Rule rulePlanetUnaspected(Planet planet, Event event) throws DataAccessException {
		RuleService service = new RuleService();
		String pcode = planet.getCode();
		if (pcode.equals("Rakhu")) {
			House house = planet.getHouse();
			if (house.getCode().equals("X_2")) 
				return (Rule)service.find(4L);

		} else if (pcode.equals("Sun")) {
			boolean strong = false;
			if (planet.getSign().getCode().equals("Leo"))
				strong = true;
			else {
				if (event.isHousable()) {
					House house = planet.getHouse();
					if (house.getCode().equals("V") || house.getCode().equals("V_2") || house.getCode().equals("V_3"))
						strong = true;
				}
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

	/**
	 * Нахождение куспида в знаке
	 * @param house астрологический дом
	 * @param sign знак
	 * @param event персона
	 * @return правило
	 * @throws DataAccessException
	 */
	public static Rule ruleHouseSign(House house, Sign sign, Event event) throws DataAccessException {
		RuleService service = new RuleService();
		String hcode = house.getCode();
		String scode = sign.getCode();

		if (hcode.equals("I")) {
			if (scode.equals("Capricornus")) {
				Planet sun = event.getPlanets().get(19L);
				Planet saturn = event.getPlanets().get(29L);
				double res = CalcUtil.getDifference(sun.getLongitude(), saturn.getLongitude());
				if (res > 30)
					return (Rule)service.find(38L);
				else if (res > 150)
					return (Rule)service.find(47L);
			}
		}
		return null;
	}
}
