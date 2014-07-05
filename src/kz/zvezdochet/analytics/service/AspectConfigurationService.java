package kz.zvezdochet.analytics.service;

import kz.zvezdochet.analytics.bean.AspectConfiguration;
import kz.zvezdochet.core.bean.BaseEntity;
import kz.zvezdochet.core.service.ReferenceService;

/**
 * Реализация сервиса конфигураций аспектов
 * @author nataly
 *
 * @see ReferenceService Реализация сервиса справочника  
 */
public class AspectConfigurationService extends TextReferenceService {

	public AspectConfigurationService() {
		tableName = "aspectconfigurations";
	}

	@Override
	public BaseEntity createEntity() {
		return new AspectConfiguration();
	}
}
