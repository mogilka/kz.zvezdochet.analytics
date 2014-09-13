package kz.zvezdochet.analytics.service;

import kz.zvezdochet.core.service.GenderTextDictionaryService;

/**
 * Сервис конфигурации аспектов
 * @author Nataly Didenko
 */
public class AspectConfigurationService extends GenderTextDictionaryService {

	public AspectConfigurationService() {
		tableName = "aspectconfigurations";
	}
}
