package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.CategoryService;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.service.ModelService;

/**
 * Категория объектов
 * @author Natalie Didenko
 *
 */
public class Category extends Dictionary {
	private static final long serialVersionUID = -2878256195661519033L;

	/**
	 * Идентификатор планеты
	 */
    private Long objectId;
    
	/**
	 * Приоритет категории
	 */
    private int priority;
	
	/**
	 * Планета
	 */
    private Planet planet;
    
    public Planet getPlanet() {
		return planet;
	}
	public void setPlanet(Planet planet) {
		this.planet = planet;
	}
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	@Override
	public ModelService getService() {
		return new CategoryService();
	}
}
