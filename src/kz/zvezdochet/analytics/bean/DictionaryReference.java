package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.core.bean.Reference;


/**
 * Класс, представляющий описание справочника
 * @author Nataly
 * 
 * @see Reference Справочник
 */
public class DictionaryReference extends Reference {
	private static final long serialVersionUID = -1540041715209920674L;

	public DictionaryReference(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}
}
