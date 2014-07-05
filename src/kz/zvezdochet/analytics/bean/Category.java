package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Reference;

/**
 * Класс, представляющий Категорию объектов
 * @author Nataly
 *
 */
public class Category extends Reference {
	private static final long serialVersionUID = -2878256195661519033L;

	/**
	 * Ссылка на идентификатор связанного объекта
	 */
    private Long objectId;
    
	/**
	 * Приоритет категории
	 */
    private int priority;
	
	/**
	 * Ссылка на объект
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
}
