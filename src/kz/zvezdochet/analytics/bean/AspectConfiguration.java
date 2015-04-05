package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.AspectConfigurationService;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.service.ModelService;


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
}