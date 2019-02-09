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
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.util.CoreUtil;
import kz.zvezdochet.service.CrossService;
import kz.zvezdochet.service.ElementService;
import kz.zvezdochet.service.HalfsphereService;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.SignService;
import kz.zvezdochet.service.SquareService;
import kz.zvezdochet.service.YinYangService;
import kz.zvezdochet.service.ZoneService;
import kz.zvezdochet.util.Configuration;

/**
 * Набор статистических данных события
 * @author Nataly Didenko
 *
 */
public class EventStatistics {
	private Configuration conf;
	private Map<String, Double> planetSigns;
	private Map<String, Double> planetHouses;
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
	
	public EventStatistics(Configuration conf) {
		this.conf = conf;
	}

	/**
	 * Вычисление выраженных знаков Зодиака
	 * @param main признак того, что нужно учитывать только минорные планеты
	 * @return карта приоритетных знаков
	 * @throws DataAccessException 
	 */
	public Map<String, Double> getPlanetSigns(boolean main) throws DataAccessException {
		if (conf.getPlanets() != null) {
			conf.initPlanetSigns(true);
			planetSigns = new HashMap<String, Double>();
			signPlanets = new HashMap<String, Integer>();
			Collection<Planet> planets = conf.getPlanets().values();
			for (Planet planet : planets) {
				if (main && !planet.isMain())
					continue;
				double value = 0.0;
				Object object = planetSigns.get(planet.getSign().getCode());
				if (object != null)
					value = (Double)object;
				value += planet.getScore();
				planetSigns.put(planet.getSign().getCode(), value);

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
		if (conf.getPlanets() != null) {
			planetHouses = new HashMap<String, Double>();
			List<Model> houses = conf.getHouses();
			Collection<Planet> planets = conf.getPlanets().values();
			for (Planet planet : planets) {
				for (int i = 0; i < houses.size(); i++) {
					House house1 = (House)houses.get(i);
					int j = (i == houses.size() - 1) ? 0 : i + 1;
					House house2 = (House)houses.get(j);
					if (SkyPoint.getHouse(house1.getCoord(), house2.getCoord(), planet.getCoord())) { 
						planet.setHouse(house1);
						double value = 0.0;
						Object object = planetHouses.get(house1.getCode());
						if (object != null)
							value = (Double)object;
						value += planet.getScore();
						planetHouses.put(house1.getCode(), value);
					}
				}
			}
		}
	}

	/**
	 * Поиск астрологического дома конфигурации по коду
	 * @param code код дома
	 * @return астрологический дом конфигурации
	 * @throws DataAccessException 
	 */
	public House getHouse(String code) throws DataAccessException {
		for (Model model : conf.getHouses())
			if (((House)model).getCode().equals(code))
				return (House)model;
		return null;
	}

	/**
	 * Вычисление выраженных зон Зодиака
	 * @return карта приоритетных зон
	 * @throws DataAccessException 
	 */
	public void initPlanetDivisions() throws DataAccessException {
		if (planetSigns != null) {
			planetElements = new HashMap<String, Double>();
			planetYinYangs = new HashMap<String, Double>();
			planetHalfspheres = new HashMap<String, Double>();
			planetSquares = new HashMap<String, Double>();
			planetCrosses = new HashMap<String, Double>();
			planetZones = new HashMap<String, Double>();
			
			Iterator<Map.Entry<String, Double>> iterator = planetSigns.entrySet().iterator();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Sign sign = (Sign)new SignService().find(entry.getKey());
				double value = 0.0;
				
				//выделенность стихий
				ModelService service = new ElementService();
				Element element = (Element)service.find(sign.getElementId());
		    	String division = element.getCode();
				Object object = planetElements.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetElements.put(division, value);

				//выделенность инь-ян
				service = new YinYangService();
				value = 0.0;
				YinYang yinYang = (YinYang)service.find(sign.getYinyangId());
				division = yinYang.getCode();
				object = planetYinYangs.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetYinYangs.put(division, value);

				//выделенность полусфер
				service = new HalfsphereService();
				value = 0.0;
				Halfsphere halfsphere = (Halfsphere)service.find(sign.getVerticalHalfSphereId());
				division = halfsphere.getCode();
				object = planetHalfspheres.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetHalfspheres.put(division, value);

				value = 0.0;
				halfsphere = (Halfsphere)service.find(sign.getHorizontalalHalfSphereId());
				division = halfsphere.getCode();
				object = planetHalfspheres.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetHalfspheres.put(division, value);

				//выделенность квадратов
				service = new SquareService();
				value = 0.0;
				Square square = (Square)service.find(sign.getSquareId());
				division = square.getCode();
				object = planetSquares.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetSquares.put(division, value);

				//выделенность крестов
				service = new CrossService();
				value = 0.0;
				Cross cross = (Cross)service.find(sign.getCrossId());
				division = cross.getCode();
				object = planetCrosses.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				planetCrosses.put(division, value);

				//выделенность зон
				service = new ZoneService();
				value = 0.0;
				Zone zone = (Zone)service.find(sign.getZoneId());
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

	public Map<String, Double> getPlanetHouses() {
		return planetHouses;
	}

	/**
	 * Вычисление выраженных зон домов
	 * @return карта приоритетных зон
	 * @throws DataAccessException 
	 */
	public void initHouseDivisions() throws DataAccessException {
		if (planetHouses != null) {
			houseElements = new HashMap<String, Double>();
			houseYinYangs = new HashMap<String, Double>();
			houseHalfspheres = new HashMap<String, Double>();
			houseSquares = new HashMap<String, Double>();
			houseCrosses = new HashMap<String, Double>();
			houseZones = new HashMap<String, Double>();
			
			Iterator<Map.Entry<String, Double>> iterator = planetHouses.entrySet().iterator();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	House house = (House)new HouseService().find(entry.getKey());
				double value = 0.0;
				
				//выделенность стихий
				Element element = (Element)new ElementService().find(house.getElementId());
		    	String division = element.getCode();
				Object object = houseElements.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseElements.put(division, value);

				//выделенность инь-ян
				value = 0.0;
				YinYang yinYang = (YinYang)new YinYangService().find(house.getYinyangId());
				division = yinYang.getCode();
				object = houseYinYangs.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseYinYangs.put(division, value);

				//выделенность полусфер
				value = 0.0;
				Halfsphere halfsphere = (Halfsphere)new HalfsphereService().find(house.getVerticalHalfSphereId());
				division = halfsphere.getCode();
				object = houseHalfspheres.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseHalfspheres.put(division, value);

				value = 0.0;
				halfsphere = (Halfsphere)new HalfsphereService().find(house.getHorizontalalHalfSphereId());
				division = halfsphere.getCode();
				object = houseHalfspheres.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseHalfspheres.put(division, value);

				//выделенность квадратов
				value = 0.0;
				Square square = (Square)new SquareService().find(house.getSquareId());
				division = square.getCode();
				object = houseSquares.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseSquares.put(division, value);

				//выделенность крестов
				value = 0.0;
				Cross cross = (Cross)new CrossService().find(house.getCrossId());
				division = cross.getCode();
				object = houseCrosses.get(division);
				if (object != null)
					value = (Double)object;
				value += entry.getValue();
				houseCrosses.put(division, value);

				//выделенность зон
				value = 0.0;
				Zone zone = (Zone)new ZoneService().find(house.getZoneId());
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
	public Map<String, Double> getMainPlanetHouses() throws DataAccessException {
		Map<String, Double> houses = new HashMap<String, Double>();
		if (planetHouses != null) {
			Iterator<Map.Entry<String, Double>> iterator = planetHouses.entrySet().iterator();
			HouseService service = new HouseService();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
				//по индексу трети определяем дом, в котором она находится
		    	House house = (House)service.find(entry.getKey());
				double value = entry.getValue();
				int index;
				if (CoreUtil.isArrayContainsNumber(new int[] {1,4,7,10,13,16,19,22,25,28,31,34}, house.getNumber()))
					index = house.getNumber();
				else
					index = (house.getNumber() % 3 == 0) ? house.getNumber() - 2 : house.getNumber() - 1;
		    	house = service.getHouse(index);

				Object object = houses.get(house.getCode());
				if (object != null)
					value += (Double)object;
				houses.put(house.getCode(), value);
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
					Double value = planetHouses.get(house.getCode());
					if (value != null)
						sum += value; 
				}
				types.put(crossSign.getCode(), sum);
			}
		}
		return types;
	}
}
