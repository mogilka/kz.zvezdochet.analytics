package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.SphereService;
import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.service.ModelService;

/**
 * Сфера жизни
 * @author Nataly Didenko
 */
public class Sphere extends Dictionary {
	private static final long serialVersionUID = 672735353317629369L;

	@Override
	public ModelService getService() {
		return new SphereService();
	}
}
