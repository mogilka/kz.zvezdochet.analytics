package kz.zvezdochet.analytics.exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import kz.zvezdochet.analytics.Activator;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.PlatformUtil;
import kz.zvezdochet.service.SignService;
import kz.zvezdochet.util.Configuration;

/**
 * Генератор XML-файла для flash-изображения
 * @author Nataly Didenko
 *
 */
public class XMLExporter {
	
	public XMLExporter(Configuration conf) throws DataAccessException {
		String xmlfile = null;
		try {
			String path = PlatformUtil.getPath(Activator.PLUGIN_ID, "/out/horoscope_files/data.xml").getPath(); //$NON-NLS-1$
			File file = new File(path);
			final String FILE_SEPARATOR = System.getProperties().getProperty("file.separator"); //$NON-NLS-1$
			xmlfile = file.isDirectory() ? file.getPath() + FILE_SEPARATOR : file.getPath(); //$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
		}

		Document document = null;
		Element rootElement = null;
		try {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        document = builder.newDocument();
	        rootElement = document.createElement("data");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		exportHouses(document, rootElement, conf);
		exportPlanets(document, rootElement, conf);
		exportSigns(document, rootElement, conf);
		saveDocument(rootElement, xmlfile);
	}

	private void exportHouses(Document document, Element root, Configuration conf) {
		Element houses = document.createElement("sets");
		int count = 0;
		for (Model house : conf.getHouses()) {
			Element element = document.createElement("set");
			element.appendChild(document.createTextNode(((House)house).getCode()));
			element.setAttribute("id", String.valueOf(count++));
			element.setAttribute("name", ((House)house).getCode());
			double coord = CalcUtil.roundTo(CalcUtil.decToDeg(((House)house).getCoord()), 2);
			element.setAttribute("coord", String.valueOf(coord));
			houses.appendChild(element);
		}
		root.appendChild(houses);
	}

	private void exportPlanets(Document document, Element root, Configuration conf) {
		Element planets = document.createElement("sets");
		int count = 0;
		for (Model planet : conf.getPlanets()) {
			Element element = document.createElement("set");
			element.appendChild(document.createTextNode(((Planet)planet).getCode()));
			element.setAttribute("id", String.valueOf(count++));
			element.setAttribute("name", ((Planet)planet).getName());
			double coord = CalcUtil.roundTo(CalcUtil.decToDeg(((Planet)planet).getCoord()), 2);
			element.setAttribute("coord", String.valueOf(coord));
			planets.appendChild(element);
		}
		root.appendChild(planets);
	}
	
	private void exportSigns(Document document, Element root, Configuration conf) throws DataAccessException {
		List<Model> signlist = new SignService().getList();
		Element signs = document.createElement("sets");
		int count = 0;
		for (Model sign : signlist) {
			Element element = document.createElement("set");
			element.appendChild(document.createTextNode(((Sign)sign).getCode()));
			element.setAttribute("id", String.valueOf(count++));
			element.setAttribute("name", ((Sign)sign).getName());
			element.setAttribute("coord", String.valueOf(((Sign)sign).getI0()));
			signs.appendChild(element);
		}
		root.appendChild(signs);
	}

	/**
	 * Сохранение сгенерированного файла
	 * @param xmlfile путь к файлу
	 */
	private void saveDocument(Element root, String xmlfile) {
        try {
        	File file = new File(xmlfile);
        	OutputFormat format = new OutputFormat();
            format.setOmitXMLDeclaration(false);
            format.setEncoding("UTF-8");
            format.setIndenting(true);
            format.setLineSeparator(System.getProperty("line.separator"));

            XMLSerializer serializer = new org.apache.xml.serialize.XMLSerializer();
            serializer.setOutputByteStream(new FileOutputStream(file));
            serializer.setOutputFormat(format);            
            serializer.serialize(root);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
	}
}
