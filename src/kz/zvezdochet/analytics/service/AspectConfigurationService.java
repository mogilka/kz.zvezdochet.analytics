package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.AspectConfiguration;
import kz.zvezdochet.core.bean.Base;
import kz.zvezdochet.core.service.ReferenceService;

/**
 * Реализация сервиса конфигураций аспектов
 * @author Nataly Didenko
 *
 * @see ReferenceService Реализация сервиса справочника  
 */
public class AspectConfigurationService extends TextReferenceService {

	public AspectConfigurationService() {
		tableName = "aspectconfigurations";
	}

	@Override
	public Base create() {
		return new AspectConfiguration();
	}
}
