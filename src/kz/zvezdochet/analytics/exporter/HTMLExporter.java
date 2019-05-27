package kz.zvezdochet.analytics.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.itextpdf.text.DocumentException;

import html.Tag;
import kz.zvezdochet.analytics.bean.Category;
import kz.zvezdochet.analytics.bean.CrossSign;
import kz.zvezdochet.analytics.bean.HouseSignText;
import kz.zvezdochet.analytics.bean.PlanetAspectText;
import kz.zvezdochet.analytics.bean.PlanetHouseText;
import kz.zvezdochet.analytics.bean.PlanetSignText;
import kz.zvezdochet.analytics.bean.PlanetText;
import kz.zvezdochet.analytics.bean.Rule;
import kz.zvezdochet.analytics.service.CardTypeService;
import kz.zvezdochet.analytics.service.CategoryService;
import kz.zvezdochet.analytics.service.CrossSignService;
import kz.zvezdochet.analytics.service.DegreeService;
import kz.zvezdochet.analytics.service.HouseSignService;
import kz.zvezdochet.analytics.service.PlanetAspectService;
import kz.zvezdochet.analytics.service.PlanetHouseService;
import kz.zvezdochet.analytics.service.PlanetSignService;
import kz.zvezdochet.analytics.service.PlanetTextService;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Cross;
import kz.zvezdochet.bean.Element;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.Halfsphere;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.bean.SkyPoint;
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.bean.Square;
import kz.zvezdochet.bean.YinYang;
import kz.zvezdochet.bean.Zone;
import kz.zvezdochet.core.bean.ITextGender;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.bean.TextGender;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.export.bean.Bar;
import kz.zvezdochet.export.util.HTMLUtil;
import kz.zvezdochet.export.util.PDFUtil;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.CrossService;
import kz.zvezdochet.service.ElementService;
import kz.zvezdochet.service.EventService;
import kz.zvezdochet.service.HalfsphereService;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.service.SignService;
import kz.zvezdochet.service.SquareService;
import kz.zvezdochet.service.YinYangService;
import kz.zvezdochet.service.ZoneService;

/**
 * Генератор HTML-отчёта
 * @author Natalie Didenko
 *
 */
@SuppressWarnings("unchecked")
public class HTMLExporter {
	private HTMLUtil util;
	private boolean child = false;
	private boolean female = false;

	public HTMLExporter() {
		util = new HTMLUtil();
	}

	/**
	 * Генерация индивидуального гороскопа
	 * @param event событие
	 * @return html-текст
	 */
	public String generate(Event event) {
		child = event.isChild();
		female = event.isFemale();
		try {
			Tag html = new Tag("html");
			Tag head = new Tag("head");
			head.add(new Tag("meta", "http-equiv=Content-Type content=text/html; charset=UTF-8"));
//			head.add(new Tag("link", "href=horoscope_files/horoscope.css rel=stylesheet type=text/css"));
//			head.add(new Tag("link", "href=horoscope_files/chart.css rel=stylesheet type=text/css"));
			html.add(head);

			Tag body = new Tag("body");
	
			EventStatistics statistics = new EventStatistics(event);
			statistics.initPlanetHouses();

			//градус рождения
			if (!child)
				printDegree(event, body);
			
			//планеты в знаках
			printPlanetSign(event, body);
			
			//тип космограммы
			Map<String, Integer> signPlanetsMap = statistics.getSignPlanets();
			printCardType(event, body, signPlanetsMap);
			
			//планеты
			printPlanetStrong(event, body);
			printPlanetWeak(event, body);
			
			//аспекты
			Tag tag = new Tag("h1");
			tag.add("Аспекты планет");
			body.add(tag);
			printAspect(event, body, "Позитивные сочетания", "POSITIVE");
			printAspect(event, body, "Негативные сочетания", "NEGATIVE");

			//планеты в домах
			Map<String, Double> houseMap = statistics.getPlanetHouses();
			printPlanetHouse(event, body, houseMap);

//			//стихии
//			statistics.initPlanetDivisions();
//			statistics.initHouseDivisions();
//			generateElements(event, body, statistics);
//	
//			//инь-ян
//			generateYinYang(event, body, statistics);
//			
//			//полусферы
//			generateHalfSpheres(event, body, statistics);
//			
//			//квадраты
//			generateSquares(event, body, statistics, signMap);
//			
//			//кресты
//			generateCrosses(event, body, statistics);
//			
//			//зоны
//			generateZones(event, body, statistics);

			html.add(body);
//			if (html != null)
//				System.out.println(html);
			return html.toString();
		} catch(Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Ошибка", e.getMessage());
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Генерация планет в домах
	 * @param event событие
	 * @param body тег-контейнер для вложенных тегов
	 * @param houseMap карта домов
	 */
	private void printPlanetHouse(Event event, Tag body, Map<String, Double> houseMap) {
		List<Model> houses = event.getHouses();
		Collection<Planet> cplanets = event.getPlanets().values();
		if (null == houses) return;
		try {
			Tag div = new Tag("div");
			for (Model hmodel : houses) {
				House house = (House)hmodel;
				/*
				 * Игнорируем 3 треть 1 дома, т.к. она неверно толкует внешность.
				 * TODO в таблицу домов в знаках прописать туда внешности. А в планетах в домах описывать физ. данные
				 * TODO описание внешности должно содержать всё до мелочей от цвета глаз до роста!
				 * отдельная тема для исследования
				 */
//				if (house.getCode().equals("I_3")) continue;

				//Определяем количество планет в доме
				List<Planet> planets = new ArrayList<Planet>();
				for (Model pmodel : cplanets) {
					Planet planet = (Planet)pmodel;
					if (planet.getCode().equals("Kethu"))
						continue;
					if (planet.getHouse().getId().equals(house.getId()))
						planets.add(planet);
				}
				//Создаем информационный блок, только если дом не пуст
				Tag tag = null;
				if (planets.size() > 0) {
					tag = new Tag("h2");
					PlanetHouseService service = new PlanetHouseService();
					for (Planet planet : planets) {
						PlanetHouseText dict = (PlanetHouseText)service.find(planet, house, null);
						if (dict != null) {
							String sign = planet.isDamaged() || planet.isLilithed() ? "-" : "+";
							tag.add(planet.getShortName() + " " + sign + " " + house.getName());
							div.add(tag);
							div.add(dict.getText());
							printGender(div, dict, female, child, true);
							div.add(new Tag("/br"));
						}
					}
				}

				//добавляем информацию о доме в знаке
				if (!house.isExportOnSign())
					continue;

				Sign sign = SkyPoint.getSign(house.getLongitude(), event.getBirthYear());
				HouseSignText dict = (HouseSignText)new HouseSignService().find(house, sign);
				if (dict != null) {
					if (null == tag)
						tag = new Tag("h2");
					tag.add(house.getName() + " + " + sign.getDescription());
					div.add(tag);
					div.add(dict.getText());
					printGender(div, dict, female, child, true);
					tag.add(new Tag("/br"));
				}
			}
			body.add(div);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация сильных планет
	 * @param event событие
	 * @param body тег-контейнер для вложенных тегов
	 */
	private void printPlanetStrong(Event event, Tag body) {
		try {
			Tag div = new Tag("div");
			Tag tag = new Tag("h1");
			tag.add("Сильные стороны");
			div.add(tag);

			tag = new Tag("h2");
			PlanetTextService service = new PlanetTextService();
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				PlanetText planetText = null;
				if (planet.isSword()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "sword");
					if (planetText != null) {
						tag.add(planet.getShortName() + "-меч");
						div.add(tag);
						div.add(planetText.getText());

						Rule rule = EventRules.rulePlanetSword(planet, female);
						if (rule != null)
							div.add(PDFUtil.html2pdf(rule.getText(), null));

						printGender(div, planetText, female, child, true);

					}
				} else if (planet.isShield()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "shield");
					if (planetText != null) {
						tag.add(planet.getShortName() + "-щит");
						div.add(tag);
						div.add(planetText.getText());
						printGender(div, planetText, female, child, true);
					}
				}
				if (planet.isPerfect() && !planet.isBroken()) {
					if (planet.inMine()) {
						Tag p = new Tag("p");
						p.add("Планета " + planet.getName() + " не вызывает напряжения, так что вы сумеете проработать недостатки, описанные в разделе «" + planet.getShortName() + " в шахте»");
						div.add(p);
					}
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "perfect");
					if (planetText != null) {
						tag.add(planet.getShortName() + "-гармония");
						div.add(tag);
						div.add(planetText.getText());
						printGender(div, planetText, female, child, true);
					}
				}
			}
			body.add(div);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация статистики типов аспектов
	 * @param event событие
	 * @param cell тег-контейнер для вложенных тегов
	 */
	private void generateAspectTypes(Event event, Tag cell) {
		try {
//			event.getConfiguration().initPlanetAspects();
			Collection<Planet> planets = event.getPlanets().values();
			//фильтрация списка типов аспектов
			List<Model> aspectTypes = new AspectTypeService().getList();
			List<AspectType> types = new ArrayList<AspectType>();
		    for (Model model : aspectTypes) {
		    	AspectType type = (AspectType)model;
		    	if (type.getCode() != null &&
		    			!type.getCode().equals("COMMON") &&
		    			type.getName() != null)
		    		types.add(type);
		    }			
			
			Bar[] bars = new Bar[types.size()];
			int i = -1;
		    for (AspectType type : types) {
		    	Bar bar = new Bar();
		    	bar.setName(type.getName());
		    	int value = 0;
		    	for (Model model : planets) {
		    		Planet planet = (Planet)model;
					value += planet.getAspectCountMap().get(type.getCode());
		    	}
		    	bar.setValue(value / 2);
				bar.setColor(type.getDimColor());
		    	bars[++i] = bar;
		    }
			
			Tag tr = new Tag("tr");
			Tag td = new Tag("td", "class=header id=aspects");
			td.add("Соотношение аспектов планет");
			tr.add(td);
			cell.add(tr);
	
			tr = new Tag("tr");
			td = new Tag("td");
			Tag p = new Tag("p");
			Tag span = new Tag("span", "style=color:#903");
			span.add("Больше гармоничных &mdash; меньше препятствий<br>");
			p.add(span);
			
			span = new Tag("span", "style=color:#063");
			span.add("Больше творческих &mdash; меньше ограничений<br>");
			p.add(span);
			
			span = new Tag("span", "style=color:#630");
			span.add("Больше нейтральных &mdash; больше планет проявляются в симбиозе, а не поодиночке<br>");
			p.add(span);
			
			span = new Tag("span");
			span.add("Больше негармоничных &mdash; больше резких и тяжёлых ситуаций<br>");
			p.add(span);
			
			span = new Tag("span", "style=color:#666");
			span.add("Больше скрытых &mdash; больше событий происходит за кулисами жизни<br>");
			p.add(span);
			
			span = new Tag("span", "style=color:#369");
			span.add("Больше кармических &mdash; больше тупиковых ситуаций, которые нужно преодолеть<br>");
			p.add(span);
			
			span = new Tag("span", "style=color:#399");
			span.add("Больше прогрессивных аспектов (искушения) &mdash; большим испытаниям вы подвергнуты<br>");
			p.add(span);
			
			span = new Tag("span", "style=color:#609");
			span.add("Чем больше духовных (ДАО, порабощение, магические аспекты) &mdash; тем более высокого уровня развития вы достигли");
			p.add(span);
			td.add(p);
			Tag chart = util.getCss3Chart(bars, null);
			td.add(chart);
			tr.add(td);
			cell.add(tr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация аспектов планет
	 * @param event событие
	 * @param body тег-контейнер для вложенных тегов
	 * @param header заголовок блока информации
	 * @param aspectType код типа аспектов
	 */
	private void printAspect(Event event, Tag body, String header, String aspectType) {
		try {
			Tag div = new Tag("div");
			Tag tag = new Tag("h2");
			tag.add(header);
			div.add(tag);
	
			PlanetAspectService service = new PlanetAspectService();
			AspectType damaged = (AspectType)new AspectTypeService().find("NEGATIVE");

			Collection<Planet> planets = event.getPlanets().values();
			for (Planet p : planets) {
				List<SkyPointAspect> aspects = p.getAspectList();
				if (null == aspects)
					continue;

				for (SkyPointAspect aspect : aspects) {
					Planet planet1 = (Planet)aspect.getSkyPoint1();
					if (!planet1.isMain())
						continue;
					long asplanetid = aspect.getAspect().getPlanetid();
					if (asplanetid > 0 && asplanetid != planet1.getId())
						continue;
					Planet planet2 = (Planet)aspect.getSkyPoint2();
					if (planet1.getNumber() > planet2.getNumber())
						continue;

					AspectType type = aspect.checkType(true);
					boolean match = false;
					//аспект соответствует заявленному (негативному или позитивному)
					if (type.getCode().equals(aspectType))
						match = true;
					//в позитивные добавляем ядро Солнца
					else if	(aspectType.equals("POSITIVE") &&
							type.getCode().equals("NEUTRAL_KERNEL"))
						match = true;
					//в негативные добавляем пояс Солнца
					else if (aspectType.equals("NEGATIVE") &&
							type.getCode().equals("NEGATIVE_BELT"))
						match = true;

					if (match) {
						PlanetAspectText dict = (PlanetAspectText)service.find(aspect, true);
						if (dict != null) {
							tag = new Tag("h5");
							tag.add(dict.getPlanet1().getShortName() + " " + 
								type.getSymbol() + " " + 
								dict.getPlanet2().getShortName());
							div.add(tag);
							div.add(dict.getText());
							printGender(div, dict, female, child, true);
							div.add(new Tag("/br"));
						}
					}
				}
			}
			body.add(div);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Генерация зон
	 * @param event событие
	 * @param cell тег-контейнер для вложенных тегов
	 * @param statistics объект статистики
	 */
	private void generateZones(Event event, Tag cell, EventStatistics statistics) {
		try {
			Tag tr = new Tag("tr");
			Tag td = new Tag("td", "class=header id=zones");
			td.add("Развитие духа в сознании");
			tr.add(td);
			cell.add(tr);
			
			Map<String, Double> zoneMap = statistics.getPlanetZones();
			Bar[] bars = new Bar[zoneMap.size()];
			Iterator<Map.Entry<String, Double>> iterator = zoneMap.entrySet().iterator();
			int i = -1;
			Zone zone = null;
			double score = 0.0;
			ZoneService service = new ZoneService();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Zone element = (Zone)service.find(entry.getKey());
		    	bar.setName(element.getName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		zone = element;
		    	}
		    }
		    if (zone != null) {
				tr = new Tag("tr");
				td = new Tag("td");
				td.add(util.getBoldTaggedString(zone.getDescription()));
				td.add(zone.getText());
				printGender(cell, zone, female, child, true);

				Tag p = new Tag("p");
				p.add("Диаграмма показывает, какие приоритеты вы ставите в своём развитии.");
				td.add(p);
				Tag chart = util.getCss3Chart(bars, null);
				td.add(chart);
				tr.add(td);
				cell.add(tr);

			    tr = new Tag("tr");
				td = new Tag("td", "class=header");
				td.add("Развитие духа в действии");
				tr.add(td);
				cell.add(tr);
				
				zoneMap = statistics.getHouseZones();
				bars = new Bar[zoneMap.size()];
				iterator = zoneMap.entrySet().iterator();
				i = -1;
				zone = null;
			    while (iterator.hasNext()) {
			    	Entry<String, Double> entry = iterator.next();
			    	Bar bar = new Bar();
			    	Zone element = (Zone)service.find(entry.getKey());
			    	bar.setName(element.getName());
			    	bar.setValue(entry.getValue());
			    	bar.setColor(element.getColor());
			    	bars[++i] = bar;
			    }
				
			    tr = new Tag("tr");
				td = new Tag("td");
				p = new Tag("p");
				p.add("Диаграмма показывает, как на событийном уровне, в действии меняются ваши приоритеты развития по сравнению с предыдущей моделью.");
				td.add(p);
				chart = util.getCss3Chart(bars, null);
				td.add(chart);
				tr.add(td);
				cell.add(tr);
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация крестов
	 * @param event событие
	 * @param cell тег-контейнер для вложенных тегов
	 * @param statistics объект статистики
	 */
	private void generateCrosses(Event event, Tag cell, EventStatistics statistics) {
		try {
			Tag tr = new Tag("tr");
			Tag td = new Tag("td", "class=header id=crosses");
			td.add("Стратегия в сознании");
			tr.add(td);
			cell.add(tr);
			
			Map<String, Double> crossMap = statistics.getPlanetCrosses();
			Bar[] bars = new Bar[crossMap.size()];
			Iterator<Map.Entry<String, Double>> iterator = crossMap.entrySet().iterator();
			int i = -1;
			Cross cross = null;
			double score = 0.0;
			CrossService service = new CrossService();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Cross element = (Cross)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		cross = element;
		    	}
		    }
		    if (cross != null) {
				tr = new Tag("tr");
				td = new Tag("td");
				td.add(util.getBoldTaggedString(cross.getDescription()));
				td.add(cross.getText());
				printGender(cell, cross, female, child, true);

				Tag p = new Tag("p");
				p.add("Диаграмма показывает, какой тип стратегии наиболее присущ вам в мыслях и принятии решений.");
				td.add(p);
				Tag chart = util.getCss3Chart(bars, null);
				td.add(chart);
				tr.add(td);
				cell.add(tr);
		    }

			//знаки
			crossMap = statistics.getCrossSigns();
			bars = new Bar[crossMap.size()];
			iterator = crossMap.entrySet().iterator();
			i = -1;
			CrossSignService service2 = new CrossSignService();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	CrossSign element = (CrossSign)service2.find(entry.getKey());
		    	bar.setName(element.getName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bars[++i] = bar;
		    }
			
			tr = new Tag("tr");
			td = new Tag("td");
			Tag p = new Tag("p");
			p.add("Диаграмма показывает, в каких качествах выражается стратегия ваших намерений.");
			td.add(p);
			Tag chart = util.getCss3Chart(bars, null);
			td.add(chart);
			tr.add(td);
			cell.add(tr);

			//
			tr = new Tag("tr");
			td = new Tag("td", "class=header");
			td.add("Стратегия в поступках");
			tr.add(td);
			cell.add(tr);
			
			crossMap = statistics.getHouseCrosses();
			bars = new Bar[crossMap.size()];
			iterator = crossMap.entrySet().iterator();
			i = -1;
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Cross element = (Cross)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bars[++i] = bar;
		    }
			
			tr = new Tag("tr");
			td = new Tag("td");
			p = new Tag("p");
			p.add("Диаграмма показывает, как ваша стратегия меняется в действии (на событийном уровне, в социуме).");
			td.add(p);
			chart = util.getCss3Chart(bars, null);
			td.add(chart);
			td.add(new Tag("/br"));
			tr.add(td);
			cell.add(tr);
			
			//дома
			crossMap = statistics.getCrossHouses();
			bars = new Bar[crossMap.size()];
			iterator = crossMap.entrySet().iterator();
			i = -1;
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	CrossSign element = (CrossSign)service2.find(entry.getKey());
		    	bar.setName(element.getName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bars[++i] = bar;
		    }
			
			tr = new Tag("tr");
			td = new Tag("td");
			p = new Tag("p");
			p.add("Диаграмма показывает, в каких качествах выражается стратегия ваших действий.");
			td.add(p);
			chart = util.getCss3Chart(bars, null);
			td.add(chart);
			tr.add(td);
			cell.add(tr);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация квадратов
	 * @param event событие
	 * @param cell тег-контейнер для вложенных тегов
	 * @param statistics объект статистики
	 */
	private void generateSquares(Event event, Tag cell, EventStatistics statistics, Map<String, Double> signMap) {
		try {
			Tag tr = new Tag("tr");
			Tag td = new Tag("td", "class=header id=squares");
			td.add("Зрелость в сознании");
			tr.add(td);
			cell.add(tr);
			
			Map<String, Double> squareMap = statistics.getPlanetSquares();
			Bar[] bars = new Bar[squareMap.size()];
			Iterator<Map.Entry<String, Double>> iterator = squareMap.entrySet().iterator();
			int i = -1;
			Square square = null;
			double score = 0.0;
			SquareService service = new SquareService();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Square element = (Square)service.find(entry.getKey());
		    	bar.setName(element.getName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		square = element;
		    	}
		    }
		    if (square != null) {
				tr = new Tag("tr");
				td = new Tag("td");
				td.add(util.getBoldTaggedString(square.getDescription()));
				td.add(square.getText());
				printGender(cell, square, female, child, true);
				
				Tag p = new Tag("p");
				p.add("Диаграмма показывает, как в ваших мыслях и намерениях выражены качества разных возрастных групп.");
				td.add(p);
				Tag chart = util.getCss3Chart(bars, null);
				td.add(chart);
				tr.add(td);
				cell.add(tr);

				//знаки
				bars = new Bar[signMap.size()];
				iterator = signMap.entrySet().iterator();
				i = -1;
				SignService service2 = new SignService();
			    while (iterator.hasNext()) {
			    	Entry<String, Double> entry = iterator.next();
			    	Bar bar = new Bar();
			    	Sign element = (Sign)service2.find(entry.getKey());
			    	bar.setName(element.getDiaName());
			    	bar.setValue(entry.getValue());
			    	bar.setColor(element.getColor());
			    	bars[++i] = bar;
			    }
				
			    tr = new Tag("tr");
				td = new Tag("td");
				p = new Tag("p");
				p.add("Диаграмма показывает, в каких качествах выражается зрелость мыслей.");
				td.add(p);
				chart = util.getCss3Chart(bars, null);
				td.add(chart);
				tr.add(td);
				cell.add(tr);

				//
			    tr = new Tag("tr");
				td = new Tag("td", "class=header");
				td.add("Зрелость в поступках");
				tr.add(td);
				cell.add(tr);
				
				squareMap = statistics.getHouseSquares();
				bars = new Bar[squareMap.size()];
				iterator = squareMap.entrySet().iterator();
				i = -1;
			    while (iterator.hasNext()) {
			    	Entry<String, Double> entry = iterator.next();
			    	Bar bar = new Bar();
			    	Square element = (Square)service.find(entry.getKey());
			    	bar.setName(element.getName());
			    	bar.setValue(entry.getValue());
			    	bar.setColor(element.getColor());
			    	bars[++i] = bar;
			    }
				
			    tr = new Tag("tr");
				td = new Tag("td");
				p = new Tag("p");
				p.add("Диаграмма показывает, как на событийном уровне, в социуме меняется зрелость ваших намерений по сравнению с предыдущей моделью.");
				td.add(p);
				chart = util.getCss3Chart(bars, null);
				td.add(chart);
				tr.add(td);
				cell.add(tr);
				
				//дома
				Map<String, Double> houseMap = statistics.getMainPlanetHouses(); //TODO найти более оптимальный вариант, мат.формулу
				bars = new Bar[houseMap.size()];
				iterator = houseMap.entrySet().iterator();
				i = -1;
				HouseService hservice = new HouseService();
			    while (iterator.hasNext()) {
			    	Entry<String, Double> entry = iterator.next();
			    	Bar bar = new Bar();
					//по индексу трети определяем дом, в котором она находится
			    	House element = (House)hservice.find(entry.getKey());
			    	bar.setName(element.getDiaName());
			    	bar.setValue(entry.getValue());
			    	bar.setColor(element.getColor());
			    	bars[++i] = bar;
			    }
				
			    tr = new Tag("tr");
				td = new Tag("td");
				p = new Tag("p");
				p.add("Диаграмма показывает, в каких качествах выражается зрелость поступков.");
				td.add(p);
				chart = util.getCss3Chart(bars, null);
				td.add(chart);
				tr.add(td);
				cell.add(tr);
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация полусфер
	 * @param event событие
	 * @param cell тег-контейнер для вложенных тегов
	 * @param statistics объект статистики
	 */
	private void generateHalfSpheres(Event event, Tag cell, EventStatistics statistics) {
		try {
			Tag tr = new Tag("tr");
			Tag td = new Tag("td", "class=header id=halfspheres");
			td.add("Экстраверсия и интроверсия в сознании");
			tr.add(td);
			cell.add(tr);
			
			Map<String, Double> sphereMap = statistics.getPlanetHalfspheres();
			List<Bar> bars = new ArrayList<Bar>();
			List<Bar> bars2 = new ArrayList<Bar>();
			Iterator<Map.Entry<String, Double>> iterator = sphereMap.entrySet().iterator();
			Halfsphere sphere = null;
			double score = 0.0;
			HalfsphereService service = new HalfsphereService();
			String[] extra = new String[] { "North", "West" };
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Halfsphere element = (Halfsphere)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	if (Arrays.asList(extra).contains(element.getCode()))
		    		bars.add(bar);
		    	else
		    		bars2.add(bar);
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		sphere = element;
		    	}
		    }
		    if (sphere != null) {
				tr = new Tag("tr");
				td = new Tag("td");
				td.add(util.getBoldTaggedString(sphere.getDescription()));
				td.add(sphere.getText());
				printGender(cell, sphere, female, child, true);

				Tag p = new Tag("p");
				p.add("Диаграммы показывают баланс открытости и закрытости ваших мыслей.");
				td.add(p);
				Tag chart = util.getCss3Chart(bars.toArray(new Bar[2]), "Открытость");
				td.add(chart);
				chart = util.getCss3Chart(bars2.toArray(new Bar[2]), "Закрытость");
				td.add(chart);
				tr.add(td);
				cell.add(tr);

			    tr = new Tag("tr");
				td = new Tag("td", "class=header");
				td.add("Экстраверсия и интроверсия в поступках");
				tr.add(td);
				cell.add(tr);
				
				sphereMap = statistics.getHouseHalfspheres();
				bars = new ArrayList<Bar>();
				bars2 = new ArrayList<Bar>();
				iterator = sphereMap.entrySet().iterator();
				sphere = null;
			    while (iterator.hasNext()) {
			    	Entry<String, Double> entry = iterator.next();
			    	Bar bar = new Bar();
			    	Halfsphere element = (Halfsphere)service.find(entry.getKey());
			    	bar.setName(element.getDiaName());
			    	bar.setValue(entry.getValue());
			    	bar.setColor(element.getColor());
			    	if (Arrays.asList(extra).contains(element.getCode()))
			    		bars.add(bar);
			    	else
			    		bars2.add(bar);
			    }
				
			    tr = new Tag("tr");
				td = new Tag("td");
				p = new Tag("p");
				p.add("Диаграммы показывают, как на практике меняется ваш баланс поведения в социуме.");
				td.add(p);
				chart = util.getCss3Chart(bars.toArray(new Bar[2]), "Открытость");
				td.add(chart);
				chart = util.getCss3Chart(bars2.toArray(new Bar[2]), "Закрытость");
				td.add(chart);
				tr.add(td);
				cell.add(tr);
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация инь-ян
	 * @param event событие
	 * @param cell тег-контейнер для вложенных тегов
	 * @param statistics объект статистики
	 */
	private void generateYinYang(Event event, Tag cell, EventStatistics statistics) {
		try {
			Tag tr = new Tag("tr");
			Tag td = new Tag("td", "class=header id=yinyang");
			td.add("Гармония мужского и женского начала в сознании");
			tr.add(td);
			cell.add(tr);
			
			Map<String, Double> yinYangMap = statistics.getPlanetYinYangs();
			Bar[] bars = new Bar[yinYangMap.size()];
			Iterator<Map.Entry<String, Double>> iterator = yinYangMap.entrySet().iterator();
			int i = -1;
			YinYang yinyang = null;
			double score = 0.0;
			YinYangService service = new YinYangService();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	YinYang element = (YinYang)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		yinyang = element;
		    	}
		    }
		    if (yinyang != null) {
				tr = new Tag("tr");
				td = new Tag("td");
				td.add(util.getBoldTaggedString(yinyang.getDescription()));
				td.add(yinyang.getText());
				printGender(cell, yinyang, female, child, true);

				Tag p = new Tag("p");
				p.add("Диаграмма показывает, насколько активны ваши намерения, когда вы мыслите, принимаете решения, находясь наедине с самим собой.");
				td.add(p);
				Tag chart = util.getCss3Chart(bars, null);
				td.add(chart);
				tr.add(td);
				cell.add(tr);

			    tr = new Tag("tr");
				td = new Tag("td", "class=header");
				td.add("Гармония мужского и женского начала в поступках");
				tr.add(td);
				cell.add(tr);
				
				yinYangMap = statistics.getHouseYinYangs();
				bars = new Bar[yinYangMap.size()];
				iterator = yinYangMap.entrySet().iterator();
				i = -1;
				yinyang = null;
			    while (iterator.hasNext()) {
			    	Entry<String, Double> entry = iterator.next();
			    	Bar bar = new Bar();
			    	YinYang element = (YinYang)service.find(entry.getKey());
			    	bar.setName(element.getDiaName());
			    	bar.setValue(entry.getValue());
			    	bar.setColor(element.getColor());
			    	bars[++i] = bar;
			    }
				
			    tr = new Tag("tr");
				td = new Tag("td");
				p = new Tag("p");
				p.add("Диаграмма показывает, как на событийном уровне, в социуме меняется активность ваших намерений и проявлений по сравнению с предыдущей идеальной моделью.");
				td.add(p);
				chart = util.getCss3Chart(bars, null);
				td.add(chart);
				tr.add(td);
				cell.add(tr);
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация содержания
	 * @param event событие
	 * @param cell ячейка-контейнер таблицы разметки
	 */
	private void generateContents(Event event, Tag cell) {
		try {
			//левая часть содержания
			Tag subtable = new Tag("table", "class=menu align=left");
			Tag tr = new Tag("tr");
			Tag td = new Tag("td");
			
			//характеристика личности
			Tag b = new Tag("h5");
			b.add("Характеристика личности");
			td.add(b);
			List<Model> list = new CategoryService().getList();
			for (Model model : list) {
				Category category = (Category)model;
				Tag a = new Tag("a", "href=#" + category.getCode());
				a.add(category.getName());
				td.add(a);
				td.add(new Tag("/br"));
			}
			td.add(new Tag("/br"));

			//общая информация
			Map<String, String> contents = new HashMap<String, String>();
			contents.put("celebrity", "Знаменитости");
			contents.put("signs", "Выраженные Знаки Зодиака");
			contents.put("dsigns", "Кредо вашей жизни");
			if (!child)
				contents.put("degree", "Символ рождения");

			b = new Tag("h5");
			b.add("Общая информация");
			td.add(b);
			for (Map.Entry<String, String> entry : contents.entrySet()) {
				Tag a = new Tag("a", "href=#" + entry.getKey());
				a.add(entry.getValue());
				td.add(a);
				td.add(new Tag("/br"));
			}
			td.add(new Tag("/br"));

			b = new Tag("h5");
			b.add("Диаграммы");
			td.add(b);

			contents = new HashMap<String, String>();
			contents.put("cardtype", "Самораскрытие");
			contents.put("elements", "Темперамент");
			contents.put("yinyang", "Мужское и женское начало");
			contents.put("halfspheres", "Экстраверсия");
			contents.put("squares", "Зрелость");
			contents.put("crosses", "Стратегия");
			contents.put("zones", "Развитие духа");

			for (Map.Entry<String, String> entry : contents.entrySet()) {
				Tag a = new Tag("a", "href=#" + entry.getKey());
				a.add(entry.getValue());
				td.add(a);
				td.add(new Tag("/br"));
			}
			td.add(new Tag("/br"));
			tr.add(td);
			subtable.add(tr);

			cell.add(subtable);

//---------------------------------------------------------------------------

			//правая часть содержания
			subtable = new Tag("table", "class=menu align=right");
			tr = new Tag("tr");
			td = new Tag("td");

			//описание космограммы
			contents = new HashMap<String, String>();
			contents.put("cosmogram", "Карта рождения");
			contents.put("cardkind", "Кармический потенциал");
			contents.put("planets", "Сильные и слабые стороны");
			contents.put("aspects", "Сильные и слабые сочетания");
			contents.put("configurations", "Комплексный анализ личности");
			
			b = new Tag("h5");
			b.add("Карта рождения");
			td.add(b);
			for (Map.Entry<String, String> entry : contents.entrySet()) {
				Tag a = new Tag("a", "href=#" + entry.getKey());
				a.add(entry.getValue());
				td.add(a);
				td.add(new Tag("/br"));
			}
			td.add(new Tag("/br"));

			b = new Tag("h5");
			b.add("Реализация личности");
			td.add(b);
			Tag a = new Tag("a", "href=#houses");
			a.add("Сферы жизни");
			td.add(a);
			td.add(new Tag("/br"));

			Collection<Planet> eplanets = event.getPlanets().values();
			List<Model> ehouses = event.getHouses();
			for (Model hmodel : ehouses) {
				House house = (House)hmodel;
				//Определяем количество планет в доме
				List<Planet> planets = new ArrayList<Planet>();
				for (Planet planet : eplanets) {
					if (planet.getCode().equals("Kethu")) continue;
					if (planet.getHouse().getId().equals(house.getId()))
						planets.add(planet);
				}
				//Создаем информационный блок только если дом не пуст
				if (planets.size() > 0 || house.isExportOnSign()) {
					a = new Tag("a", "href=#" + house.getLinkName());
					a.add(house.getName());
					td.add(a);
					td.add(new Tag("/br"));
				}
			}

			td.add(new Tag("/br"));
			tr.add(td);
			subtable.add(tr);
			cell.add(subtable);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Генерация знаменитостей
	 * @param date дата события
	 * @param cell тег-контейнер для вложенных тегов
	 */
	private void generateCelebrities(Date date, Tag cell) {
		try {
			List<Event> list = new EventService().findEphemeron(date);
			if (list != null && list.size() > 0) {
				Tag tr = new Tag("tr");
				Tag td = new Tag("td", "class=header id=celebrity");
				td.add("Однодневки");
				tr.add(td);
				cell.add(tr);
	
				tr = new Tag("tr");
				td = new Tag("td");
				td.add(util.getNormalTaggedString("В один день с вами родились такие известные люди:"));
				Tag p = new Tag("p");
				
				for (Model model : list) {
					Event event = (Event)model;
					p.add(util.getSmallTaggedString(DateUtil.formatDate(event.getBirth())));
					Tag a = new Tag("a", "target=_blank href=" + event.getUrl());
					a.add(event.getName());
					p.add(a);
					p.add(util.getSmallTaggedString("&nbsp;&nbsp;&nbsp;" + event.getDescription()));
					p.add(new Tag("/br"));
				}
				td.add(p);
				tr.add(td);
				cell.add(tr);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация диаграмм знаков
	 * @param cell тег-контейнер для вложенных тегов
	 * @param signMap карта знаков
	 */
	private void generateSignChart(Tag cell, Map<String, Double> signMap) {
		try {
			//выраженные знаки
			Tag tr = new Tag("tr");
			Tag td = new Tag("td", "class=header id=signs");
			td.add("Выраженные знаки Зодиака");
			tr.add(td);
			cell.add(tr);
			
			Bar[] bars = new Bar[signMap.size()];
			Bar[] bars2 = new Bar[signMap.size()];
			Iterator<Map.Entry<String, Double>> iterator = signMap.entrySet().iterator();
			int i = -1;
			SignService service = new SignService();
		    while (iterator.hasNext()) {
		    	i++;
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Sign sign = (Sign)service.find(entry.getKey());
		    	bar.setName(sign.getName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(sign.getColor());
		    	bars[i] = bar;
	
		    	bar = new Bar();
		    	bar.setName(sign.getDescription());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(sign.getColor());
		    	bars2[i] = bar;
		    }
			tr = new Tag("tr");
			td = new Tag("td", "class=align-center");
			Tag chart = util.getCss3Chart(bars, null);
			td.add(chart);
			tr.add(td);
			cell.add(tr);
	
			//кредо
			tr = new Tag("tr");
			td = new Tag("td", "class=header id=credo");
			td.add("Кредо вашей жизни");
			tr.add(td);
			cell.add(tr);
	
			tr = new Tag("tr");
			td = new Tag("td");
			chart = util.getTaggedChart(17, bars2, null);
			td.add(chart);
			tr.add(td);
			cell.add(tr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация диаграмм домов
	 * @param statistics объект статистики события
	 * @param cell тег-контейнер для вложенных тегов
	 */
	private void generateHouseChart(EventStatistics statistics, Tag cell) {
		try {
			Tag tr = new Tag("tr");
			Tag td = new Tag("td", "class=header id=houses");
			td.add("Сферы жизни");
			tr.add(td);
			cell.add(tr);
	
			tr = new Tag("tr");
			td = new Tag("td");
			Tag p = new Tag("p");
			p.add("Сферы жизни отражают ваши врождённые возможности, багаж, с которым вы пришли в этот мир. "
					+ "Пригодится он вам или нет - покажет время. "
					+ "В любом случае, это отправная точка корабля событий, на котором вы поплывёте по морю жизни и реализуете свою миссию.");
			td.add(p);
			
			Map<String, Double> houses = statistics.getPlanetHouses();
			Bar[] bars = new Bar[houses.size()];
			Iterator<Map.Entry<String, Double>> iterator = houses.entrySet().iterator();
	    	int i = -1;
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	House house = statistics.getHouse(entry.getKey());
		    	bar.setName(house.getName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(house.getColor());
		    	bars[++i] = bar;
		    }
			Tag chart = util.getTaggedChart(17, bars, null);
			td.add(chart);
			tr.add(td);
			cell.add(tr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация градуса рождения
	 * @param event событие
	 * @param body тег-контейнер для вложенных тегов
	 */
	private void printDegree(Event event, Tag body) {
		try {
			if (event.getHouses() != null &&
					event.getHouses().size() > 0) {
				House house = (House)event.getHouses().get(0);
				if (null == house) return;
				int value = (int)house.getLongitude();
				Model model = new DegreeService().find(new Long(String.valueOf(value)));
			    if (model != null) {
			    	TextGenderDictionary degree = (TextGenderDictionary)model;
					Tag div = new Tag("div");
					Tag tag = new Tag("h1");
					tag.add("Символ рождения");
					div.add(tag);
					
					tag = new Tag("h2");
					tag.add(degree.getId() + "&#176; " + degree.getName() + ", " + degree.getCode());
					div.add(tag);
					
					if (degree.getDescription() != null) {
						tag = new Tag("i");
						tag.add(degree.getDescription());
						div.add(tag);
					}

					if (degree.getText() != null)
						div.add(degree.getText());
					body.add(div);
			    }
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация планет в знаках
	 * @param event событие
	 * @param body тег-контейнер для вложенных тегов
	 */
	private void printPlanetSign(Event event, Tag body) {
		try {
			if (event.getPlanets() != null) {
				PlanetSignService service = new PlanetSignService();
				Tag div = new Tag("div");
				Tag tag = new Tag("h1");
				tag.add("Общий типаж");
				div.add(tag);

				Collection<Planet> planets = event.getPlanets().values();
				for (Planet planet : planets) {
				    if (planet.isMain()) {
				    	List<PlanetSignText> list = service.find(planet, planet.getSign());
				    	if (list != null && list.size() > 0)
				    		for (PlanetSignText object : list) {
				    			Category category = object.getCategory();
				    			if (category.getCode().equals("childhood") && !child)
				    				continue;
								tag = new Tag("h2");
								tag.add(category.getName());
								div.add(tag);
								div.add(object.getText());

								printGender(div, object, female, child, true);
								div.add(new Tag("/br"));
				    		}
				    }
				}
				body.add(div);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация космограммы
	 * @param event событие
	 * @param cell тег-контейнер для вложенных тегов
	 */
	private void generateCard(Event event, Tag cell) {
		try {
			//космограмма
			Tag tr = new Tag("tr");
			Tag td = new Tag("td", "class=header id=cosmogram");
			td.add("Карта рождения");
			tr.add(td);
			cell.add(tr);
	
			tr = new Tag("tr");
			td = new Tag("td");
			Tag p = new Tag("p", "class=shot");
			Tag object = new Tag("img",
				"src=horoscope_files/card.png " +
				"id=cosmogram " +
				"align=center");
			p.add(object);
			td.add(p);

			p = new Tag("p");
			p.add("Карта рождения (натальная карта) - это уникальный отпечаток положения планет на небе в момент вашего рождения. Планеты расположены так, как если бы вы смотрели на них с Земли:");
			td.add(p);
			Tag ul = new Tag("ul");
			p = new Tag("li");
			p.add("ближе к точке ASC расположены планеты, восходящие над горизонтом");
			ul.add(p);
			p = new Tag("li");
			p.add("ближе к MC - планеты в зените");
			ul.add(p);
			p = new Tag("li");
			p.add("ближе к DSC - планеты, заходящие за горизонт");
			ul.add(p);
			p = new Tag("li");
			p.add("ближе к IC - планеты в надире");
			ul.add(p);
			td.add(ul);

			p = new Tag("p");
			p.add("Планеты в знаках Зодиака и астрологических домах:");
			td.add(p);
			p = new Tag("table", "class=legend-list");
			int i = -1;
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				String trstyle = (++i % 2 > 0) ? "odd" : "";
				Tag tr2 = new Tag("tr", "class=" + trstyle);
				Tag td2 = new Tag("td");
				Tag img = new Tag("img", "src=horoscope_files/planet/" + planet.getCode() + ".png");
				td2.add(img);
				td2.add(planet.getName());
				tr2.add(td2);

				td2 = new Tag("td");
				td2.add(planet.getDescription());
				tr2.add(td2);
				
				td2 = new Tag("td");
				td2.add(CalcUtil.roundTo(planet.getLongitude(), 2) + "&#176;");
				tr2.add(td2);
				
				Sign sign = planet.getSign();
				Color color = sign.getColor();
				String cattr = "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");";
				td2 = new Tag("td");
				img = new Tag("b", "style=color:" + cattr);
				img.add(sign.getSymbol());
				td2.add(img);
				td2.add(sign.getName());
				tr2.add(td2);

				House house = planet.getHouse();
				color = house.getColor();
				cattr = "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");";
				td2 = new Tag("td");
				img = new Tag("span", "style=color:" + cattr);
				img.add(house.getDesignation());
				td2.add(img);
				tr2.add(td2);
				td2 = new Tag("td");
				td2.add(house.getName());
				tr2.add(td2);
				p.add(tr2);
			}
			td.add(p);
			tr.add(td);
			cell.add(tr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация типа космограммы на основе положения Солнца и Луны
	 * @param event событие
	 * @param body тег-контейнер для вложенных тегов
	 * @param signMap карта знаков
	 */
	private void printCardType(Event event, Tag body, Map<String, Integer> signMap) {
		try {
			if (event.getPlanets() != null) {
				String type = "";
				Planet sun = (Planet)event.getPlanets().get(0);
				Planet moon = (Planet)event.getPlanets().get(1);
				
				if (sun.getSign().getId().equals(moon.getSign().getId()))
					type = "centered";
				else {
					int sunSign = signMap.get(sun.getSign().getCode());
					int moonSign = signMap.get(moon.getSign().getCode());
					if (sunSign > 1 & moonSign == 1)
						type = "solar";
					else if (sunSign == 1 & moonSign > 1)
						type = "lunar";
					else if (sunSign > 1 & moonSign > 1) {
						if (sunSign == moonSign)
							type = "equivalent";
						else
							type = (sunSign > moonSign) ? "solar_lunar" : "lunar_solar";
					} else if (sunSign == 1 & moonSign == 1) {
						//определяем знак, в котором больше всего планет
						int max = 0;
						for (Iterator<Integer> iterator = signMap.values().iterator(); iterator.hasNext();) {
							int value = iterator.next();
							if (max < value) max = value;
						}
						type = (max > 2) ? "planetary" : "scattered";
					}
				}
			
				if (type.length() > 0) {
				    Model model = new CardTypeService().find(type);
				    if (model != null) {
				    	TextGenderDictionary cardType = (TextGenderDictionary)model;
						Tag div = new Tag("div");
						Tag tag = new Tag("h1");
						tag.add("Самораскрытие");
						div.add(tag);
	
						tag = new Tag("h2");
						tag.add("Тип космограммы: " + cardType.getName());
						div.add(tag);

						tag = new Tag("i");
						tag.add(cardType.getDescription());
						div.add(tag);
						div.add(cardType.getText());
						body.add(div);
				    }
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Генерация стихий
	 * @param event событие
	 * @param cell тег-контейнер для вложенных тегов
	 * @param statistics объект статистики
	 */
	private void generateElements(Event event, Tag cell, EventStatistics statistics) {
		try {
			Tag tr = new Tag("tr");
			Tag td = new Tag("td", "class=header id=elements");
			td.add("Темперамент в сознании");
			tr.add(td);
			cell.add(tr);
			
			//определение выраженной стихии
			Map<String, Double> elementMap = statistics.getPlanetElements();
			String[] elements = new String[elementMap.size()];
			Bar[] bars = new Bar[elementMap.size()];
			Iterator<Map.Entry<String, Double>> iterator = elementMap.entrySet().iterator();
			int i = -1;
			ElementService service = new ElementService();
		    while (iterator.hasNext()) {
		    	i++;
		    	Entry<String, Double> entry = iterator.next();
		    	elements[i] = entry.getKey();
		    	Bar bar = new Bar();
		    	Element element = (Element)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bars[i] = bar;
		    }
		    Element element = null;
		    for (Model model : service.getList()) {
		    	element = (Element)model;
		    	String[] codes = element.getCode().split("_");
		    	if (codes.length == elements.length) {
		    		boolean match = true;
		    		for (String code : codes)
		    			if (!Arrays.asList(elements).contains(code)) {
		    				match = false;
		    				break;
		    			}
		    		if (match)
		    			break;
		    		else
		    			continue;
		    	}
		    }
			
		    if (element != null) {
				tr = new Tag("tr");
				td = new Tag("td");
				Tag tag = util.getBoldTaggedString("Выраженные стихии: " + element.getName());
				td.add(tag);
				td.add(element.getText());
				printGender(cell, element, female, child, true);

				Tag p = new Tag("p");
				p.add("Диаграмма показывает, на чём мысленно вы сконцентрированы, какие проявления для вас важны, необходимы, естественны.");
				td.add(p);
				Tag chart = util.getCss3Chart(bars, element.getTemperament());
				td.add(chart);
				tr.add(td);
				cell.add(tr);
		    }

		    tr = new Tag("tr");
			td = new Tag("td", "class=header");
			td.add("Темперамент в поступках");
			tr.add(td);
			cell.add(tr);
			
			elementMap = statistics.getHouseElements();
			bars = new Bar[elementMap.size()];
			iterator = elementMap.entrySet().iterator();
			i = -1;
		    while (iterator.hasNext()) {
		    	i++;
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	element = (Element)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bars[i] = bar;
		    }
			
			tr = new Tag("tr");
			td = new Tag("td");
			Tag p = new Tag("p");
			p.add("Диаграмма показывает, как на событийном уровне, в социуме меняются ваши приоритеты по сравнению с предыдущей идеальной моделью.");
			td.add(p);
			Tag chart = util.getCss3Chart(bars, null);
			td.add(chart);
			tr.add(td);
			cell.add(tr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Генерация слабых планет
	 * @param event событие
	 * @param body тег-контейнер для вложенных тегов
	 */
	private void printPlanetWeak(Event event, Tag body) {
		try {
			Tag div = new Tag("div");
			Tag tag = new Tag("h1");
			tag.add("Слабые стороны");
			div.add(tag);

			tag = new Tag("h2");
			PlanetTextService service = new PlanetTextService();
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				PlanetText planetText = null;
				if (planet.inMine()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "mine");
					if (planetText != null) {
						tag.add(planet.getShortName() + " в шахте");
						div.add(tag);
						div.add(planetText.getText());

						PlanetService planetService = new PlanetService();
						Planet ruler = planetService.getRuler(planet.getSign(), true);
						if (ruler != null) {
							planetText = (PlanetText)service.findByPlanet(ruler.getId(), "positive");
							if (planetText != null) {
								tag = new Tag("p");
								tag.add("В этой ситуации вам помогут следующие сферы жизни:");
								div.add(tag);
								div.add(planetText.getText());
							}
						}
						printGender(div, planetText, female, child, true);
					}
				}
				if (planet.isDamaged()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "damaged");
					if (planetText != null) {
						tag.add(planet.getShortName() + "-дисгармония");
						div.add(tag);
						div.add(planetText.getText());
						printGender(div, planetText, female, child, true);
					}
				}
				if (planet.isRetrograde()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "retro");
					if (planetText != null) {
						tag.add(planet.getShortName() + "-ретроград");
						div.add(tag);
						div.add(planetText.getText());
						printGender(div, planetText, female, child, true);
					}
				}
			}
			body.add(div);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация гендерного толкования
	 * @param body подраздел
	 * @param dict справочник
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	private void printGender(Tag body, ITextGender dict, boolean female, boolean child, boolean health) {
		if (dict != null) {
			List<TextGender> genders = dict.getGenderTexts(female, child);
			for (TextGender gender : genders) {
				if (!health && gender.getType().equals("health"))
					continue;
				Tag tag = new Tag("h5");
				tag.add(PDFUtil.getGenderHeader(gender.getType()));
				body.add(tag);
				body.add(gender.getText());
			};
		}
	}
}
