package kz.zvezdochet.analytics.util;

import kz.zvezdochet.analytics.bean.Cross;
import kz.zvezdochet.analytics.bean.Element;
import kz.zvezdochet.analytics.bean.Halfsphere;
import kz.zvezdochet.analytics.bean.InYan;
import kz.zvezdochet.analytics.bean.Square;
import kz.zvezdochet.analytics.bean.Zone;
import kz.zvezdochet.analytics.service.CrossService;
import kz.zvezdochet.analytics.service.ElementService;
import kz.zvezdochet.analytics.service.HalfsphereService;
import kz.zvezdochet.analytics.service.InYanService;
import kz.zvezdochet.analytics.service.SquareService;
import kz.zvezdochet.analytics.service.ZoneService;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.util.CalcUtil;

/**
 * Класс, предоставляющий методы для работы с объектами аналитики
 * @author Nataly Didenko
 *
 */
public class AnalyticsUtil {
	/**
	 * Определение стихии, к которой принадлежит знак Зодиака
	 * @param index порядковый номер знака
	 * @return код стихии
	 */
	public static String signToElement(int index) {
		switch (index) {
		case 1 : case 5 : case 9 : case 10 : return "FIRE";
		case 2 : case 6 : case 11 : return "EARTH";
		case 3 : case 7 : case 12 : return "AIR";
		default: return "WATER"; //4, 8, 13
		}
	}

	/**
	 * Определение зоны Инь-Ян, к которой принадлежит знак Зодиака
	 * @param index порядковый номер знака
	 * @return код зоны
	 */
	public static String signToInYan(int index) {
		switch (index) {
		case 1 : case 3 : case 5 : case 7 : case 9 : case 10 : case 12 : return "MALE";
		default: return "FEMALE";
		}
	}

	/**
	 * Определение вертикальной полусферы знака Зодиака
	 * @param index порядковый номер знака
	 * @return код полусферы
	 */
	public static String signToVerticalHalfSphere(int index) {
		switch (index) {
		case 1-6 : return "SOUTH";
		default: return "NORTH";
		}
	}

	/**
	 * Определение горизонтальной полусферы знака Зодиака
	 * @param index порядковый номер знака
	 * @return код полусферы
	 */
	public static String signToHorizontalHalfSphere(int index) {
		switch (index) {
		case 4-10 : return "WEST";
		default: return "EAST";
		}
	}

	/**
	 * Определение квадрата знака Зодиака
	 * @param index порядковый номер знака
	 * @return код квадрата
	 */
	public static String signToSquare(int index) {
		switch (index) {
		case 1 : case 2 : case 3 : return "CHILDHOOD";
		case 4-6 : return "YOUTH";
		case 7-10 : return "MATURITY";
		default: return "OLDAGE"; //11-13
		}
	}

	/**
	 * Определение креста знака Зодиака
	 * @param index порядковый номер знака
	 * @return код креста
	 */
	public static String signToCross(int index) {
		switch (index) {
		case 1 : case 4 : case 7 : case 11 : return "CARDINAL";
		case 2 : case 5 : case 8 : case 12 : return "FIXED";
		default: return "MUTABLE"; //3, 6, 9, 10, 13
		}
	}

	/**
	 * Определение зоны знака Зодиака
	 * @param index порядковый номер знака
	 * @return код зоны
	 */
	public static String signToZone(int index) {
		switch (index) {
		case 1 : case 2 : case 3 : case 4 : return "ACCUMULATE";
		case 5-8 : return "CREATIVE";
		default: return "TRANSFORM"; //9-13
		}
	}

	/**
	 * Поиск стихии
	 * @param code код стихии
	 * @return стихия
	 * @throws DataAccessException 
	 */
	public static Element getElement(String code) throws DataAccessException {
		for (Model model : new ElementService().getList())
			if (((Element)model).getCode().toUpperCase().equals(code))
				return (Element)model;
		return null;
	}

	/**
	 * Поиск зоны
	 * @param code код зоны
	 * @return зона
	 * @throws DataAccessException 
	 */
	public static Zone getZone(String code) throws DataAccessException {
		for (Model model : new ZoneService().getList())
			if (((Zone)model).getCode().toUpperCase().equals(code))
				return (Zone)model;
		return null;
	}

	/**
	 * Поиск креста
	 * @param code код креста
	 * @return крест
	 * @throws DataAccessException 
	 */
	public static Cross getCross(String code) throws DataAccessException {
		for (Model model : new CrossService().getList())
			if (((Cross)model).getCode().toUpperCase().equals(code))
				return (Cross)model;
		return null;
	}

	/**
	 * Определение стихии, к которой принадлежит дом
	 * @param index порядковый номер дома
	 * @return код стихии
	 */
	public static String houseToElement(int index) {
		//по индексу трети определяем дом, в котором она находится
		int i = CalcUtil.trunc((index + 2) / 3);
		switch (i) {
		case 1 : case 5 : case 9 : return "FIRE";
		case 2 : case 6 : case 10 : return "EARTH";
		case 3 : case 7 : case 11 : return "AIR";
		default: return "WATER"; //4, 8, 12
		}
	}

	/**
	 * Определение зоны Инь-Ян, к которой принадлежит дом
	 * @param index порядковый номер дома
	 * @return код зоны
	 */
	public static String houseToInYan(int index) {
		//по индексу трети определяем дом, в котором она находится
		int i = CalcUtil.trunc((index + 2) / 3);
		switch (i) {
		case 1 : case 3 : case 5 : case 7 : case 9 : case 11 : return "MALE";
		default: return "FEMALE";
		}
	}

	/**
	 * Определение вертикальной полусферы дома
	 * @param index порядковый номер знака
	 * @return код полусферы
	 */
	public static String houseToVerticalHalfSphere(int index) {
		//по индексу трети определяем дом, в котором она находится
		int i = CalcUtil.trunc((index + 2) / 3);
		return signToVerticalHalfSphere(i);
	}

	/**
	 * Определение горизонтальной полусферы дома
	 * @param index порядковый номер знака
	 * @return код полусферы
	 */
	public static String houseToHorizontalHalfSphere(int index) {
		//по индексу трети определяем дом, в котором она находится
		int i = CalcUtil.trunc((index + 2) / 3);
		return signToHorizontalHalfSphere(i);
	}

	/**
	 * Определение креста астрологического дома
	 * @param index порядковый номер дома
	 * @return код креста
	 */
	public static String houseToCross(int index) {
		//по индексу трети определяем дом, в котором она находится
		int i = CalcUtil.trunc((index + 2) / 3);
		switch (i) {
		case 1 : case 4 : case 7 : case 10 : return "CARDINAL";
		case 2 : case 5 : case 8 : case 11 : return "FIXED";
		default: return "MUTABLE"; //3, 6, 9, 12
		}
	}

	/**
	 * Определение квадрата астрологического дома
	 * @param index порядковый номер дома
	 * @return код квадрата
	 */
	public static String houseToSquare(int index) {
		//по индексу трети определяем дом, в котором она находится
		int i = CalcUtil.trunc((index + 2) / 3);
		switch (i) {
		case 1 : case 2 : case 3 : return "CHILDHOOD";
		case 4 : case 5 : case 6 : return "YOUTH";
		case 7 : case 8 : case 9 : return "MATURITY";
		default: return "OLDAGE"; //10-12
		}
	}

	/**
	 * Определение зоны астрологического дома
	 * @param index порядковый номер дома
	 * @return код зоны
	 */
	public static String houseToZone(int index) {
		//по индексу трети определяем дом, в котором она находится
		int i = CalcUtil.trunc((index + 2) / 3);
		return signToZone(i);
	}

	/**
	 * Поиск инь-ян
	 * @param code код инь-ян
	 * @return инь-ян
	 * @throws DataAccessException 
	 */
	public static InYan getInYan(String code) throws DataAccessException {
		for (Model model : new InYanService().getList())
			if (((InYan)model).getCode().toUpperCase().equals(code))
				return (InYan)model;
		return null;
	}

	/**
	 * Поиск полусферы
	 * @param code код полусферы
	 * @return полусфера
	 * @throws DataAccessException 
	 */
	public static Halfsphere getHalfsphere(String code) throws DataAccessException {
		for (Model model : new HalfsphereService().getList())
			if (((Halfsphere)model).getCode().toUpperCase().equals(code))
				return (Halfsphere)model;
		return null;
	}

	/**
	 * Поиск квадрата
	 * @param code код квадрата
	 * @return квадрат
	 * @throws DataAccessException 
	 */
	public static Square getSquare(String code) throws DataAccessException {
		for (Model model : new SquareService().getList())
			if (((Square)model).getCode().toUpperCase().equals(code))
				return (Square)model;
		return null;
	}
}
