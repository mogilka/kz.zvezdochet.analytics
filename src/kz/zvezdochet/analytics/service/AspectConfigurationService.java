package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.AspectConfiguration;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.TextGenderDictionaryService;

/**
 * Сервис конфигурации аспектов
 * @author Nataly Didenko
 */
public class AspectConfigurationService extends TextGenderDictionaryService {

	public AspectConfigurationService() {
		tableName = "aspectconfigurations";
	}

	@Override
	public Model create() {
		return new AspectConfiguration();
	}
}
