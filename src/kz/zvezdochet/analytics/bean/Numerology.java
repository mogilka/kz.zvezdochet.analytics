package kz.zvezdochet.analytics.bean;

import java.util.Date;

import kz.zvezdochet.analytics.service.DegreeService;
import kz.zvezdochet.core.bean.TextGenderModel;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.util.DateUtil;

/**
 * Нумерологическое толкование
 * @author Natalie Didenko
 */
public class Numerology extends TextGenderModel {
	private static final long serialVersionUID = -3797987279024365454L;

	private int number;
	private String yeartext;
	private String image;
	private String description;
	private String zoroastrsyn;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getYeartext() {
		return yeartext;
	}

	public void setYeartext(String yeartext) {
		this.yeartext = yeartext;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getZoroastrsyn() {
		return zoroastrsyn;
	}

	public void setZoroastrsyn(String zoroastrsyn) {
		this.zoroastrsyn = zoroastrsyn;
	}

	@Override
	public ModelService getService() {
		return new DegreeService();
	}

	/**
	 * Расчёт числа рождения
	 * @param date дата
	 * @return число рождения
	 */
	public static int getNumber(Date date) {
		String sdate = DateUtil.formatDate(date);
		sdate = sdate
			.replace(".", "")
			.replace("0", "");
		int number = Integer.valueOf(sdate);
		while (number > 9) {
			String s = String.valueOf(number);
			number = 0;
			String[] syms = s.split("");
			for (int i = 0; i < syms.length; i++) {
				int n = Integer.valueOf(syms[i]);
				number += n;
			}
		}
		return number;
	}

	@Override
	public void init(boolean mode) {}

	/**
	 * Толкование числа рождения
	 */
	public String birth;
	/**
	 * Толкование числа имени
	 */
	public String name;

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Вычисление разницы лет по Зороастрийскому календарю с учётом весеннего равноденствия
	 * @param date дата
	 * @return число рождения
	 */
	public static int getDifference(Date date, Date date2) {
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(date);
//		int y = cal.get(Calendar.YEAR);
//		int m = cal.get(Calendar.MONTH);
//		int d = cal.get(Calendar.DATE);
//
//		Calendar cal2 = Calendar.getInstance();
//		cal2.setTime(date2);
//		int y2 = cal2.get(Calendar.YEAR);
//		int m2 = cal2.get(Calendar.MONTH);
//		int d2 = cal2.get(Calendar.DATE);
//
//		if (y == y2) {
//			if (m)
//		}
		return 0;
	}
}
