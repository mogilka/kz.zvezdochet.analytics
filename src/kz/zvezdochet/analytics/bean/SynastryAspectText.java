package kz.zvezdochet.analytics.bean;

/**
 * Синастрический аспект
 * @author Nataly Didenko
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
}
