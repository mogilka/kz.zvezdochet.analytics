package kz.zvezdochet.analytics.provider;

import org.eclipse.swt.graphics.Color;

import kz.zvezdochet.analytics.bean.PlanetHouseText;
import kz.zvezdochet.core.ui.ArrayLabelProvider;

/**
 * Формат таблицы планет в домах
 * @author Nataly Didenko
 */
public class PlanetHouseLabelProvider extends ArrayLabelProvider {
	@Override
	public String getColumnText(Object element, int columnIndex) {
		PlanetHouseText model = (PlanetHouseText)element;
		switch (columnIndex) {
			case 0: return model.getPlanet().getName();
			case 1: return model.getHouse().getName();
		}
		return null;
	}
	@Override
	public Color getBackground(Object element, int columnIndex) {
		PlanetHouseText model = (PlanetHouseText)element;
		Color color = model.getHouse().getColor();
		if (null == color)
			return super.getForeground(element, columnIndex);
		else
			return color;
	}
}
