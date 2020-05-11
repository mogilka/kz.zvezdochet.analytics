package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.SynastryAspectService;
import kz.zvezdochet.core.service.ModelService;

/**
 * Синастрический аспект
 * @author Natalie Didenko
 *
 */
public class SynastryAspectText extends PlanetAspectText {
	private static final long serialVersionUID = -7872375252777326071L;

	public SynastryAspectText() {}

	/**
	 * Роли
	 */
	private String roles;

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	@Override
	public ModelService getService() {
		return new SynastryAspectService();
	}

	/**
	 * Уровень критичности аспекта
	 */
	private int level;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
