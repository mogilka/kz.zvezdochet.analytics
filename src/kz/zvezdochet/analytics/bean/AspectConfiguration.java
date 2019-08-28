package kz.zvezdochet.analytics.bean;

import java.io.IOException;

import org.eclipse.swt.graphics.Color;

import kz.zvezdochet.analytics.Activator;
import kz.zvezdochet.analytics.service.AspectConfigurationService;
import kz.zvezdochet.bean.Element;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.util.PlatformUtil;


/**
 * Конфигурация аспектов
 * @author Natalie Didenko
 *
 */
public class AspectConfiguration extends TextGenderDictionary {
	private static final long serialVersionUID = 3014044501287835392L;

	@Override
	public ModelService getService() {
		return new AspectConfigurationService();
	}

	/**
	 * Возвращает путь к картинке
	 * @return URL изображения
	 */
	public String getImageUrl() {
		try {
			return PlatformUtil.getPath(Activator.PLUGIN_ID, "/icons/conf/" + code + ".gif").getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Цвет
	 */
	private Color color;
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Признак позитивной конфигурации
	 */
	private boolean positive;

	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	/**
	 * Стихия
	 */
	private Element element;

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	/**
	 * Признак позитивной планеты на вершине
	 */
	private boolean vertexPositive;
	/**
	 * Признак позитивной планеты слева внизу
	 */
	private boolean leftFootPositive;
	/**
	 * Признак позитивной планеты справа внизу
	 */
	private boolean rightFootPositive;
	/**
	 * Признак позитивной планеты в основании
	 */
	private boolean basePositive;

	public boolean isVertexPositive() {
		return vertexPositive;
	}

	public void setVertexPositive(boolean vertex) {
		this.vertexPositive = vertex;
	}

	public boolean isLeftFootPositive() {
		return leftFootPositive;
	}

	public void setLeftFootPositive(boolean left) {
		this.leftFootPositive = left;
	}

	public boolean isRightFootPositive() {
		return rightFootPositive;
	}

	public void setRightFootPositive(boolean right) {
		this.rightFootPositive = right;
	}

	public boolean isBasePositive() {
		return basePositive;
	}

	public void setBasePositive(boolean base) {
		this.basePositive = base;
	}

	/**
	 * Массив планет на вершине
	 */
	private Planet[] vertex;
	/**
	 * Массив планет слева внизу
	 */
	private Planet[] leftFoot;
	/**
	 * Массив планет справа внизу
	 */
	private Planet[] rightFoot;
	/**
	 * Массив планет в основании
	 */
	private Planet[] base;
	/**
	 * Массив планет слева посередине
	 */
	private Planet[] leftHand;

	public Planet[] getLeftHand() {
		return leftHand;
	}

	public void setLeftHand(Planet[] leftHand) {
		this.leftHand = leftHand;
	}

	public Planet[] getRightHand() {
		return rightHand;
	}

	public void setRightHand(Planet[] rightHand) {
		this.rightHand = rightHand;
	}

	/**
	 * Массив планет справа посередине
	 */
	private Planet[] rightHand;

	public Planet[] getVertex() {
		return vertex;
	}

	public void setVertex(Planet[] vertex) {
		this.vertex = vertex;
	}

	public Planet[] getLeftFoot() {
		return leftFoot;
	}

	public void setLeftFoot(Planet[] left) {
		this.leftFoot = left;
	}

	public Planet[] getRightFoot() {
		return rightFoot;
	}

	public void setRightFoot(Planet[] right) {
		this.rightFoot = right;
	}

	public Planet[] getBase() {
		return base;
	}

	public void setBase(Planet[] base) {
		this.base = base;
	}

	/**
	 * Признак позитивной планеты слева посередине
	 */
	private boolean leftHandPositive;
	/**
	 * Признак позитивной планеты справа посередине
	 */
	private boolean rightHandPositive;

	public boolean isLeftHandPositive() {
		return leftHandPositive;
	}

	public void setLeftHandPositive(boolean leftHandPositive) {
		this.leftHandPositive = leftHandPositive;
	}

	public boolean isRightHandPositive() {
		return rightHandPositive;
	}

	public void setRightHandPositive(boolean rightHandPositive) {
		this.rightHandPositive = rightHandPositive;
	}

	/**
	 * Массив планет слева вверху
	 */
	private Planet[] leftHorn;
	/**
	 * Массив планет справа вверху
	 */
	private Planet[] rightHorn;

	public Planet[] getLeftHorn() {
		return leftHorn;
	}

	public void setLeftHorn(Planet[] leftHorn) {
		this.leftHorn = leftHorn;
	}

	public Planet[] getRightHorn() {
		return rightHorn;
	}

	public void setRightHorn(Planet[] rightHorn) {
		this.rightHorn = rightHorn;
	}

	/**
	 * Признак позитивной планеты слева вверху
	 */
	private boolean leftHornPositive;
	/**
	 * Признак позитивной планеты справа вверху
	 */
	private boolean rightHornPositive;

	public boolean isLeftHornPositive() {
		return leftHornPositive;
	}

	public void setLeftHornPositive(boolean leftHornPositive) {
		this.leftHornPositive = leftHornPositive;
	}

	public boolean isRightHornPositive() {
		return rightHornPositive;
	}

	public void setRightHornPositive(boolean rightHornPositive) {
		this.rightHornPositive = rightHornPositive;
	}

	/**
	 * Градусы
	 */
	private String degree;

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	/**
	 * Тип фигуры
	 */
	private String shape;

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

//	/**
//	 * Конвертация параметров JSON в объект
//	 * @param json объект JSON
//	 */
//	public AspectConfiguration(JSONObject json) {
//		super();
//		setId(json.getLong("ID"));
//		setName(json.getString("Name"));
//		Object value = json.get("Code");
//		if (value != JSONObject.NULL)
//			setCode(value.toString());
//		value = json.get("Description");
//		if (value != JSONObject.NULL)
//			setDescription(value.toString());
//		value = json.get("Text");
//		if (value != JSONObject.NULL)
//			setText(value.toString());
//		setLatitude(json.getDouble("Latitude"));
//		setLongitude(json.getDouble("Longitude"));
//		setGreenwich(json.getDouble("Greenwich"));
//		setType(json.getString("type"));
//		setDate(DateUtil.getDatabaseDateTime(json.getString("date")));
//	}
}
