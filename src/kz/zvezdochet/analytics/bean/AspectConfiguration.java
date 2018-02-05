package kz.zvezdochet.analytics.bean;

import java.io.IOException;

import org.eclipse.swt.graphics.Color;

import kz.zvezdochet.analytics.Activator;
import kz.zvezdochet.analytics.service.AspectConfigurationService;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.util.PlatformUtil;


/**
 * Конфигурация аспектов
 * @author Nataly Didenko
 *
 */
public class AspectConfiguration extends TextGenderDictionary {
	private static final long serialVersionUID = 3014044501287835392L;

	@Override
	public ModelService getService() {
		return new AspectConfigurationService();
	}

	/**
	 * Возвращает путь к картинке
	 * @return URL изображения
	 */
	public String getImageUrl() {
		try {
			return PlatformUtil.getPath(Activator.PLUGIN_ID, "/icons/conf/" + code + ".gif").getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

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
	 * Признак позитивной конфигурации
	 */
	private boolean positive;

	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}
}