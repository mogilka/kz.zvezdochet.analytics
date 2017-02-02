package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.analytics.service.HouseSignService;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.TextGenderModel;
import kz.zvezdochet.core.service.ModelService;

/**
 * Толкование дома в знаке Зодиака
 * @author Nataly Didenko
 *
 */
public class HouseSignText extends TextGenderModel {
	private static final long serialVersionUID = -6135921834868635512L;

	/**
	 * Планета
	 */
    private House house;
        
	/**
	 * Знак Зодиака
	 */
    private Sign sign;

	public Sign getSign() {
		return sign;
	}

	public void setSign(Sign sign) {
		this.sign = sign;
	}

	@Override
	public ModelService getService() {
		return new HouseSignService();
	}

	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
	}
}
