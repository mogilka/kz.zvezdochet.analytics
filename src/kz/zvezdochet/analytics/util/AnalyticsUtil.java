package kz.zvezdochet.analytics.util;

import kz.zvezdochet.core.util.CalcUtil;

/**
 * Класс, предоставляющий методы для работы с объектами аналитики
 * @author Nataly Didenko
 * //TODO сделать связи в БД и брать оттуда
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
}
