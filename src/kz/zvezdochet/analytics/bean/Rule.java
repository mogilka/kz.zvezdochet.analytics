/**
 * 
 */
package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.RuleService;
import kz.zvezdochet.core.bean.TextGenderModel;
import kz.zvezdochet.core.service.ModelService;

/**
 * Правило дополнительного толкования
 * @author Natalie Didenko
 */
public class Rule extends TextGenderModel {
	private static final long serialVersionUID = -7317270890720890440L;

	/**
	 * Описание
	 */
	private String description;
	/**
	 * Описание
	 */
	private String text;

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public ModelService getService() {
		return new RuleService();
	}
	@Override
	public void init(boolean mode) {}
}
