package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.MoondayService;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.ModelService;

/**
 * Лунный день
 * @author Nataly Didenko
 */
public class Moonday extends Model {
	private static final long serialVersionUID = -3285572897119086698L;

	@Override
	public ModelService getService() {
		return new MoondayService();
	}

	/**
	 * Символ
	 */
	private String symbol;
	/**
	 * Толкование для родившихся в этот день
	 */
	private String birth;
	/**
	 * Минералы
	 */
	private String mineral;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
	public String getMineral() {
		return mineral;
	}
	public void setMineral(String mineral) {
		this.mineral = mineral;
	}
	@Override
	public void init(boolean mode) {}
}
