package kz.zvezdochet.analytics.exporter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kz.zvezdochet.analytics.bean.CrossSign;
import kz.zvezdochet.analytics.service.CrossSignService;
import kz.zvezdochet.bean.Cross;
import kz.zvezdochet.bean.Element;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.Halfsphere;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.bean.SkyPoint;
import kz.zvezdochet.bean.Square;
import kz.zvezdochet.bean.YinYang;
import kz.zvezdochet.bean.Zone;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.util.CoreUtil;
import kz.zvezdochet.service.CrossService;
import kz.zvezdochet.service.ElementService;
import kz.zvezdochet.service.HalfsphereService;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.SignService;
import kz.zvezdochet.service.SquareService;
import kz.zvezdochet.service.YinYangService;
import kz.zvezdochet.service.ZoneService;

/**
 * Набор статистических данных события
 * @author Natalie Didenko
 *
 */
public class EventStatistics {
	private Event event;
	private Map<String, Double> planetSigns;
	private Map<Long, Double> planetHouses;
	private Map<String, Double> planetElements;
	private Map<String, Double> planetYinYangs;
	private Map<String, Double> planetHalfspheres;
	private Map<String, Double> planetSquares;
	private Map<String, Double> planetCrosses;
	private Map<String, Double> planetZones;
	private Map<String, Integer> signPlanets;

	private Map<String, Double> houseElements;
	private Map<String, Double> houseYinYangs;
	private Map<String, Double> houseHalfspheres;
	private Map<String, Double> houseSquares;
	private Map<String, Double> houseCrosses;
	private Map<String, Double> houseZones;
	
	public EventStatistics(Event event) {
		this.event = event;
	}

	/**
	 * Вычисление выраженных знаков Зодиака
	 * @param main признак того, что нужно учитывать только минорные планеты
	 * @return карта приоритетных знаков
	 * @throws DataAccessException 
	 */
	public Map<String, Double> getPlanetSigns(boolean main) throws DataAccessException {
		if (event.getPlanets() != null) {
			event.initSigns();
			planetSigns = new HashMap<String, Double>();
			signPlanets = new HashMap<String, Integer>() {
				private static final long serialVersionUID = -4875478645892262769L;
				{
			        put("Aries", 0);
			        put("Taurus", 0);
			        put("Gemini", 0);
			        put("Cancer", 0);
			        put("Leo", 0);
			        put("Virgo", 0);
			        put("Libra", 0);
			        put("Scorpio", 0);
			        put("Ophiuchus", 0);
			        put("Sagittarius", 0);
			        put("Capricornus", 0);
			        put("Aquarius", 0);
			        put("Pisces", 0);
			    }
			};
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				if (main && !planet.isMain())
					continue;
				double value = 0.0;
				Object object = planetSigns.get(planet.getSign().getCode());
				if (object != null)
					value = (Double)object;
				value += planet.getScore();
				planetSigns.put(planet.getSign().getCode(), value);

				if (planet.isBad())
					continue;
				object = signPlanets.get(planet.getSign().getCode());
				value = object != null ? (Integer)object : 0;
				signPlanets.put(planet.getSign().getCode(), (int)++value);
			}
		}
		return planetSigns;
	}

	/**
	 * Вычисление выраженных домов Зодиака
	 * @throws DataAccessException 
	 */
	public void initPlanetHouses() throws DataAccessException {
		if (event.getPlanets() != null) {
			planetHouses = new HashMap<Long, Double>();
			Map<Long, House> houses = event.getHouses();
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				for (House house1 : houses.values()) {
					long j = (house1.getNumber() == houses.size()) ? 142 : house1.getId() + 1;
					House house2 = houses.get(j);
					if (SkyPoint.getHouse(house1.getLongitude(), house2.getLongitude(), planet.getLongitude())) { 
						planet.setHouse(house1);
						double value = 0.0;
						Object object = planetHouses.get(house1.getId());
						if (object != null)
							value = (Double)object;
						value += planet.getScore();
						planetHouses.put(house1.getId(), value);
					}
				}
			}
		}
	}

	/**
	 * Поиск астрологического дома конфигурации по идентификатору
	 * @param id идентификатор дома
	 * @return астрологический дом конфигурации
	 * @throws DataAccessException 
	 */
	public House getHouse(long id) throws DataAccessException {
		for (House house : event.getHouses().values())
			if (house.getId().equals(id))
				return house;
		return null;
	}

	/**
	 * Вычисление выраженных зон Зодиака
	 * @return карта приоритетных зон
	 * @throws DataAccessException 
	 */
	public void initPlanetDivisions() throws DataAccessException {
		if (planetSigns != null) {
			planetElements = Element.getMap();
			planetYinYangs = YinYang.getMap();
			planetHalfspheres = Halfsphere.getMap();
			planetSquares = Square.getMap();
			planetCrosses = Cross.getMap();
			planetZones = Zone.getMap();

			SignService signService = new SignService();
			ElementService elementService = new ElementService();
			YinYangService yinYangService = new YinYangService();
			HalfsphereService halfsphereService = new HalfsphereService();
			SquareService squareService = new SquareService();
			CrossService crossService = new CrossService();
			ZoneService zoneService = new ZoneService();

			Iterator<Map.Entry<String, Double>> iterator = planetSigns.entrySet().iterator();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Sign sign = (Sign)signService.find(entry.getKey());
				double value = 0.0;
				
				//выделенность стихий
				Element element = (Element)elementService.find(sign.getElementId());
		    	String division = element.getCode();
				Object object = planetElements.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetElements.put(division, value);

				//выделенность инь-ян
				value = 0.0;
				YinYang yinYang = (YinYang)yinYangService.find(sign.getYinyangId());
				division = yinYang.getCode();
				object = planetYinYangs.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetYinYangs.put(division, value);

				//выделенность полусфер
				value = 0.0;
				Halfsphere halfsphere = (Halfsphere)halfsphereService.find(sign.getVerticalHalfSphereId());
				division = halfsphere.getCode();
				object = planetHalfspheres.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetHalfspheres.put(division, value);

				value = 0.0;
				halfsphere = (Halfsphere)halfsphereService.find(sign.getHorizontalalHalfSphereId());
				division = halfsphere.getCode();
				object = planetHalfspheres.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetHalfspheres.put(division, value);

				//выделенность квадратов
				value = 0.0;
				Square square = (Square)squareService.find(sign.getSquareId());
				division = square.getCode();
				object = planetSquares.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetSquares.put(division, value);

				//выделенность крестов
				value = 0.0;
				Cross cross = (Cross)crossService.find(sign.getCrossId());
				division = cross.getCode();
				object = planetCrosses.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetCrosses.put(division, value);

				//выделенность зон
				value = 0.0;
				Zone zone = (Zone)zoneService.find(sign.getZoneId());
				division = zone.getCode();
				object = planetZones.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetZones.put(division, value);
		    }
		}
	}

	public Map<String, Integer> getSignPlanets() {
		return signPlanets;
	}

	public Map<String, Double> getPlanetElements() {
		return planetElements;
	}

	public Map<String, Double> getPlanetSigns() {
		return planetSigns;
	}

	public Map<String, Double> getPlanetYinYangs() {
		return planetYinYangs;
	}

	public Map<String, Double> getPlanetHalfspheres() {
		return planetHalfspheres;
	}

	public Map<String, Double> getPlanetSquares() {
		return planetSquares;
	}

	public Map<String, Double> getPlanetCrosses() {
		return planetCrosses;
	}

	public Map<String, Double> getPlanetZones() {
		return planetZones;
	}

	public Map<Long, Double> getPlanetHouses() {
		return planetHouses;
	}

	/**
	 * Вычисление выраженных зон домов
	 * @return карта приоритетных зон
	 * @throws DataAccessException 
	 */
	public void initHouseDivisions() throws DataAccessException {
		if (planetHouses != null) {
			houseElements = Element.getMap();
			houseYinYangs = YinYang.getMap();
			houseHalfspheres = Halfsphere.getMap();
			houseSquares = Square.getMap();
			houseCrosses = Cross.getMap();
			houseZones = Zone.getMap();

			ElementService elementService = new ElementService();
			YinYangService yinYangService = new YinYangService();
			HalfsphereService halfsphereService = new HalfsphereService();
			SquareService squareService = new SquareService();
			CrossService crossService = new CrossService();
			ZoneService zoneService = new ZoneService();

			Iterator<Map.Entry<Long, Double>> iterator = planetHouses.entrySet().iterator();
		    while (iterator.hasNext()) {
		    	Entry<Long, Double> entry = iterator.next();
		    	House house = event.getHouses().get(entry.getKey());
				double value = 0.0;
				
				//выделенность стихий
				Element element = (Element)elementService.find(house.getElementId());
		    	String division = element.getCode();
				Object object = houseElements.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseElements.put(division, value);

				//выделенность инь-ян
				value = 0.0;
				YinYang yinYang = (YinYang)yinYangService.find(house.getYinyangId());
				division = yinYang.getCode();
				object = houseYinYangs.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseYinYangs.put(division, value);

				//выделенность полусфер
				value = 0.0;
				Halfsphere halfsphere = (Halfsphere)halfsphereService.find(house.getVerticalHalfSphereId());
				division = halfsphere.getCode();
				object = houseHalfspheres.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseHalfspheres.put(division, value);

				value = 0.0;
				halfsphere = (Halfsphere)halfsphereService.find(house.getHorizontalalHalfSphereId());
				division = halfsphere.getCode();
				object = houseHalfspheres.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseHalfspheres.put(division, value);

				//выделенность квадратов
				value = 0.0;
				Square square = (Square)squareService.find(house.getSquareId());
				division = square.getCode();
				object = houseSquares.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseSquares.put(division, value);

				//выделенность крестов
				value = 0.0;
				Cross cross = (Cross)crossService.find(house.getCrossId());
				division = cross.getCode();
				object = houseCrosses.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseCrosses.put(division, value);

				//выделенность зон
				value = 0.0;
				Zone zone = (Zone)zoneService.find(house.getZoneId());
				division = zone.getCode();
				object = houseZones.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseZones.put(division, value);
		    }
		}
	}

	public Map<String, Double> getHouseElements() {
		return houseElements;
	}

	public Map<String, Double> getHouseYinYangs() {
		return houseYinYangs;
	}

	public Map<String, Double> getHouseHalfspheres() {
		return houseHalfspheres;
	}

	public Map<String, Double> getHouseSquares() {
		return houseSquares;
	}

	public Map<String, Double> getHouseCrosses() {
		return houseCrosses;
	}

	public Map<String, Double> getHouseZones() {
		return houseZones;
	}

	/**
	 * Вычисление выраженных основных домов Зодиака
	 * @return карта главных домов
	 * @throws DataAccessException 
	 */
	public Map<Long, Double> getMainPlanetHouses() throws DataAccessException {
		Map<Long, Double> houses = new HashMap<Long, Double>();
		if (planetHouses != null) {
			Iterator<Map.Entry<Long, Double>> iterator = planetHouses.entrySet().iterator();
			HouseService service = new HouseService();
		    while (iterator.hasNext()) {
		    	Entry<Long, Double> entry = iterator.next();
				//по индексу трети определяем дом, в котором она находится
		    	House house = event.getHouses().get(entry.getKey());
				double value = entry.getValue();
				int index;
				if (CoreUtil.isArrayContainsNumber(new int[] {1,4,7,10,13,16,19,22,25,28,31,34}, house.getNumber()))
					index = house.getNumber();
				else
					index = (house.getNumber() % 3 == 0) ? house.getNumber() - 2 : house.getNumber() - 1;
		    	house = service.getHouse(index);

				Object object = houses.get(house.getId());
				if (object != null)
					value += (Double)object;
				houses.put(house.getId(), value);
			}
		}
		return houses;
	}

	/**
	 * Вычисление выраженных подкатегорий крестов знаков Зодиака
	 * @return карта знаков
	 * @throws DataAccessException
	 * @todo сохранить в базу значения
	 */
	public Map<String, Double> getCrossSigns() throws DataAccessException {
		Map<String, Double> types = new HashMap<String, Double>();
		if (planetSigns != null) {
			List<Model> crossSigns = new CrossSignService().getList();
			SignService service = new SignService();
			for (Model model : crossSigns) {
				double sum = 0.0;
				CrossSign crossSign = (CrossSign)model;
				List<Model> signs = service.findByCross(model.getId());
				for (Model smodel : signs) {
					Sign sign = (Sign)smodel;
					Double value = planetSigns.get(sign.getCode());
					if (value != null)
						sum += value; 
				}
				types.put(crossSign.getCode(), sum);
			}
		}
		return types;
	}

	/**
	 * Вычисление выраженных подкатегорий крестов астрологических домов
	 * @return карта домов
	 * @throws DataAccessException 
	 * @todo сохранить в базу значения
	 */
	public Map<String, Double> getCrossHouses() throws DataAccessException {
		Map<String, Double> types = new HashMap<String, Double>();
		if (planetHouses != null) {
			List<Model> crossSigns = new CrossSignService().getList();
			HouseService service = new HouseService();
			for (Model model : crossSigns) {
				double sum = 0.0;
				CrossSign crossSign = (CrossSign)model;
				List<Model> houses = service.findByCross(model.getId());
				for (Model smodel : houses) {
					House house = (House)smodel;
					Double value = planetHouses.get(house.getId());
					if (value != null)
						sum += value; 
				}
				types.put(crossSign.getCode(), sum);
			}
		}
		return types;
	}
}
