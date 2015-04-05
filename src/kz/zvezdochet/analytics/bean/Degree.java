package kz.zvezdochet.analytics.bean;

import java.util.Arrays;

import kz.zvezdochet.analytics.service.DegreeService;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.util.CalcUtil;

/**
 * Зодиакальный градус
 * @author Nataly Didenko
 */
public class Degree extends TextGenderDictionary {
	private static final long serialVersionUID = -3521775467191351633L;

	/**
	 * Проверка, является ли градус королевским
	 * @param coord координата
	 * @return true|false королевский|иной
	 */
	public static boolean isRoyal(double coord) {
		int[] royals = {18, 69, 127, 175, 223, 281, 330};
		int d = CalcUtil.trunc(coord);
		return Arrays.asList(royals).contains(d); 
	}

	/**
	 * Проверка, является ли градус разрушительным
	 * @param coord координата
	 * @return true|false разрушительный|иной
	 */
	public static boolean isDestructive(double coord) {
		int[] royals = {23, 73, 130, 181, 229, 289, 334};
		int d = CalcUtil.trunc(coord);
		return Arrays.asList(royals).contains(d); 
	}

	@Override
	public ModelService getService() {
		return new DegreeService();
	}
}