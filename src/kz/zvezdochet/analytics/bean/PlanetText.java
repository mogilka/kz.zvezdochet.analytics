package kz.zvezdochet.analytics.bean;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.PlanetService;

/**
 * Толкование планеты
 * @author Nataly Didenko
 * TODO здесь будет всё по-другому - не будет столько полей
 */
public class PlanetText extends Model {
	private static final long serialVersionUID = -8328248201235163517L;

	/**
	 * Идентификатор планеты
	 */
    private long planetid;

	/**
     * Описание планеты-меч
     */
    private String swordText;
    
    /**
     * Описание планеты-щит
     */
    private String shieldText;
    
    /**
     * Описание планеты-пояс
     */
    private String beltText;
    
    /**
     * Описание планеты-ядро
     */
    private String kernelText;
    
    /**
     * Описание планеты в шахте
     */
    private String mineText;
    
    /**
     * Описание ретроградной планеты
     */
    private String retroText;
    
    /**
     * Описание силы планеты
     */
    private String strongText;
    
    /**
     * Описание слабости планеты
     */
    private String weakText;
    
    /**
     * Описание пораженности планеты
     */
    private String damagedText;
    
    /**
     * Описание благоприятной планеты
     */
    private String perfectText;
    
    public String getSwordText() {
		return swordText;
	}

	public void setSwordText(String sword) {
		this.swordText = sword;
	}

	public String getShieldText() {
		return shieldText;
	}

	public void setShieldText(String shield) {
		this.shieldText = shield;
	}

	public String getBeltText() {
		return beltText;
	}

	public void setBeltText(String belt) {
		this.beltText = belt;
	}

	public String getKernelText() {
		return kernelText;
	}

	public void setKernelText(String kernel) {
		this.kernelText = kernel;
	}

	public String getMineText() {
		return mineText;
	}

	public void setMineText(String mine) {
		this.mineText = mine;
	}

	public String getStrongText() {
		return strongText;
	}

	public void setStrongText(String strong) {
		this.strongText = strong;
	}

	public String getWeakText() {
		return weakText;
	}

	public void setWeakText(String weak) {
		this.weakText = weak;
	}

	public String getDamagedText() {
		return damagedText;
	}

	public void setDamagedText(String damage) {
		this.damagedText = damage;
	}

	public String getPerfectText() {
		return perfectText;
	}

	public void setPerfectText(String unharmed) {
		this.perfectText = unharmed;
	}

	public String getRetroText() {
		return retroText;
	}

	public void setRetroText(String retroText) {
		this.retroText = retroText;
	}

	public long getPlanetId() {
		return planetid;
	}

	public void setPlanetId(long planetid) {
		this.planetid = planetid;
	}

	public DictionaryService getService() {
		return new PlanetService();
	}
}
