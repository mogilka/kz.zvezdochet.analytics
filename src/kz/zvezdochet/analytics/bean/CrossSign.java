package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.CrossSignService;
import kz.zvezdochet.core.bean.DiagramObject;
import kz.zvezdochet.core.service.ModelService;

/**
 * Подкатегория крестов
 * @author Nataly Didenko
 *
 */
public class CrossSign extends DiagramObject {
	private static final long serialVersionUID = -3660987638020451477L;

	@Override
	public ModelService getService() {
		return new CrossSignService();
	}
}
