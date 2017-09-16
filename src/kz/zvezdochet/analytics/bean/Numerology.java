package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.DegreeService;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.ModelService;

/**
 * Нумерологическое толкование
 * @author Nataly Didenko
 */
public class Numerology extends Model {
	private static final long serialVersionUID = -3797987279024365454L;

	private int number;
	private String yeartext;
	private String image;
	private String description;
	private String zoroastrsyn;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getYeartext() {
		return yeartext;
	}

	public void setYeartext(String yeartext) {
		this.yeartext = yeartext;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getZoroastrsyn() {
		return zoroastrsyn;
	}

	public void setZoroastrsyn(String zoroastrsyn) {
		this.zoroastrsyn = zoroastrsyn;
	}

	@Override
	public ModelService getService() {
		return new DegreeService();
	}
}