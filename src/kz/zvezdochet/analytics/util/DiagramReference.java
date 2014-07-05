package kz.zvezdochet.analytics.util;

import kz.zvezdochet.analytics.bean.TextGenderReference;
import kz.zvezdochet.util.IColorizedObject;
import kz.zvezdochet.util.IDiagramObject;

import org.eclipse.swt.graphics.Color;

/**
 * Класс, представляющий абстрактный диаграммный справочник
 * @author nataly
 */
public abstract class DiagramReference extends TextGenderReference implements IColorizedObject, IDiagramObject {
	private static final long serialVersionUID = 3257825153209037032L;

	/**
	 * Цвет
	 */
	private Color color;
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Наименование для диаграммы
	 */
	private String diaName;
	
	public String getDiaName() {
		return diaName;
	}

	public void setDiaName(String diaName) {
		this.diaName = diaName;
	}
}
