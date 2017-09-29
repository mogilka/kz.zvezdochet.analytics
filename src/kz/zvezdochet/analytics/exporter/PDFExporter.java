package kz.zvezdochet.analytics.exporter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.ChapterAutoNumber;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import kz.zvezdochet.analytics.Activator;
import kz.zvezdochet.analytics.bean.AspectConfiguration;
import kz.zvezdochet.analytics.bean.CardKind;
import kz.zvezdochet.analytics.bean.Category;
import kz.zvezdochet.analytics.bean.CrossSign;
import kz.zvezdochet.analytics.bean.Degree;
import kz.zvezdochet.analytics.bean.HouseSignText;
import kz.zvezdochet.analytics.bean.PlanetAspectText;
import kz.zvezdochet.analytics.bean.PlanetHouseText;
import kz.zvezdochet.analytics.bean.PlanetSignText;
import kz.zvezdochet.analytics.bean.PlanetText;
import kz.zvezdochet.analytics.bean.Rule;
import kz.zvezdochet.analytics.service.AspectConfigurationService;
import kz.zvezdochet.analytics.service.CardKindService;
import kz.zvezdochet.analytics.service.CardTypeService;
import kz.zvezdochet.analytics.service.CrossSignService;
import kz.zvezdochet.analytics.service.DegreeService;
import kz.zvezdochet.analytics.service.HouseSignService;
import kz.zvezdochet.analytics.service.PlanetAspectService;
import kz.zvezdochet.analytics.service.PlanetHouseService;
import kz.zvezdochet.analytics.service.PlanetSignService;
import kz.zvezdochet.analytics.service.PlanetTextService;
import kz.zvezdochet.analytics.service.RuleService;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Cross;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.Halfsphere;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Place;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.bean.SkyPoint;
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.bean.Square;
import kz.zvezdochet.bean.YinYang;
import kz.zvezdochet.bean.Zone;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.CoreUtil;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.core.util.PlatformUtil;
import kz.zvezdochet.core.util.StringUtil;
import kz.zvezdochet.export.bean.Bar;
import kz.zvezdochet.export.handler.PageEventHandler;
import kz.zvezdochet.export.util.PDFUtil;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.CrossService;
import kz.zvezdochet.service.ElementService;
import kz.zvezdochet.service.EventService;
import kz.zvezdochet.service.HalfsphereService;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.service.SignService;
import kz.zvezdochet.service.SquareService;
import kz.zvezdochet.service.YinYangService;
import kz.zvezdochet.service.ZoneService;
import kz.zvezdochet.util.Configuration;
import kz.zvezdochet.util.Cosmogram;

/**
 * Генератор PDF-файла натальной карты
 * @author Nataly Didenko
 * http://stackoverflow.com/questions/12997739/jfreechart-itext-put-multiple-charts-in-one-pdf
 * http://viralpatel.net/blogs/generate-pie-chart-bar-graph-in-pdf-using-itext-jfreechart/
 * http://viralpatel.net/blogs/generate-pdf-file-in-java-using-itext-jar/
 * http://itextpdf.com/examples/iia.php?id=131
 * http://stackoverflow.com/questions/17825782/how-to-convert-html-to-pdf-using-itext
 * https://github.com/flyingsaucerproject/flyingsaucer/blob/master/flying-saucer-examples/src/main/java/PDFRenderToMultiplePages.java
 * http://itextsupport.com/apidocs/itext5/latest/com/itextpdf/text/package-summary.html
 * http://www.vogella.com/tutorials/JavaPDF/article.html
 * http://developers.itextpdf.com/examples/itext5-building-blocks/chunk-examples
 * http://developers.itextpdf.com/examples/graphics/pattern-colors#1575-gradienttoptobottom.java
 * http://developers.itextpdf.com/question/how-change-line-spacing-text
 * https://sourceforge.net/p/itext/sandbox/ci/c05c80778a0ea01b901b3027d433b77e68f595af/tree/src/sandbox/objects/FitTextInRectangle.java#l45
 * http://developers.itextpdf.com/frequently-asked-developer-questions?id=223
 * http://developers.itextpdf.com/question/how-make-cyrillic-characters-display-properly-when-converting-html-pdf
 * http://stackoverflow.com/questions/16669462/convert-html-to-pdf-and-add-it-to-a-paragraph
 * http://demo.itextsupport.com/xmlworker/itextdoc/flatsite.html#itextdoc-menu-7
 * http://developers.itextpdf.com/examples/xml-worker-itext5/xml-worker-examplesb
 */
public class PDFExporter {
	/**
	 * Ребёнок
	 */
	private boolean child = false;
	/**
	 * Женский пол
	 */
	private boolean female = false;
	/**
	 * Успешная ректификация
	 */
//	private boolean success = false;
	/**
	 * Признак использования астрологических терминов
	 */
	private boolean term = false;
	/**
	 * Компонент рисования
	 */
	private Display display;
	/**
	 * Базовый шрифт
	 */
	private BaseFont baseFont;
	/**
	 * Вариации шрифтов
	 */
	private Font font, fonta, fonth5;

	public PDFExporter(Display display) {
		this.display = display;
		try {
			baseFont = PDFUtil.getBaseFont();
			font = PDFUtil.getRegularFont();
			fonta = PDFUtil.getLinkFont();
			fonth5 = PDFUtil.getHeaderFont();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация индивидуального гороскопа
	 * @param event событие
	 */
	public void generate(Event event) {
		child = event.isChild();
		female = event.isFemale();
//		success = 1 == event.getRectification();

		saveCard(event);
		Document doc = new Document();
		try {
			String filename = PlatformUtil.getPath(Activator.PLUGIN_ID, "/out/horoscope.pdf").getPath();
			PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(filename));
	        writer.setPageEvent(new PageEventHandler(doc));
	        doc.open();

	        //metadata
	        PDFUtil.getMetaData(doc, "Индивидуальный гороскоп");

	        //раздел
			Chapter chapter = new ChapterAutoNumber("Общая информация");
			chapter.setNumberDepth(0);

			//шапка
			Paragraph p = new Paragraph();
			PDFUtil.printHeader(p, "Индивидуальный гороскоп");
			chapter.add(p);

			String text = DateUtil.fulldtf.format(event.getBirth());
			p = new Paragraph(text, font);
	        p.setAlignment(Element.ALIGN_CENTER);
			chapter.add(p);

			Place place = event.getPlace();
			if (null == place)
				place = new Place().getDefault();
			text = (event.getZone() >= 0 ? "UTC+" : "") + event.getZone() +
				" " + (event.getDst() >= 0 ? "DST+" : "") + event.getDst() + 
				" " + place.getName() +
				" " + place.getLatitude() + "°" +
				", " + place.getLongitude() + "°";
			p = new Paragraph(text, font);
	        p.setAlignment(Element.ALIGN_CENTER);
			chapter.add(p);

			Font fontgray = new Font(baseFont, 10, Font.NORMAL, PDFUtil.FONTCOLORGRAY);
			text = "Дата составления: " + DateUtil.fulldtf.format(new Date());
			p = new Paragraph(text, fontgray);
	        p.setAlignment(Element.ALIGN_CENTER);
			chapter.add(p);

			p = new Paragraph();
	        p.setAlignment(Element.ALIGN_CENTER);
			p.setSpacingAfter(20);
	        p.add(new Chunk("Автор: ", fontgray));
	        Chunk chunk = new Chunk(PDFUtil.AUTHOR, new Font(baseFont, 10, Font.UNDERLINE, PDFUtil.FONTCOLOR));
	        chunk.setAnchor(PDFUtil.WEBSITE);
	        p.add(chunk);
	        chapter.add(p);

			chapter.add(new Paragraph("Гороскоп описывает вашу личность как с позиции силы, так и с позиции слабости. "
				+ "Психологи утверждают, что развивать слабые стороны бессмысленно, лучше акцентироваться на достоинствах, это более эффективно. "
				+ "Не зацикливайтесь на недостатках. Искренне занимайтесь тем, к чему лежит душа, используя благоприятные возможности. "
				+ "Судьба и так сделает всё, чтобы помочь вам закалить ваш характер.", font));

			//космограмма
			printCard(doc, chapter);
			chapter.add(Chunk.NEXTPAGE);

			//знаменитости
			printCelebrities(chapter, event.getBirth());
			printSimilar(chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//градус рождения
			if (!child) {
				printDegree(chapter, event);
				chapter.add(Chunk.NEXTPAGE);
			}

			//знаки
			EventStatistics statistics = new EventStatistics(event.getConfiguration());
			Map<String, Double> signMap = statistics.getPlanetSigns(true);
			printSigns(writer, chapter, signMap);
			chapter.add(Chunk.NEXTPAGE);

			//дома
			statistics.initPlanetHouses();
			printHouses(writer, chapter, statistics);
			chapter.add(Chunk.NEXTPAGE);

			//счастливые символы
			printSign(chapter, event);
			doc.add(chapter);


			chapter = new ChapterAutoNumber("Общий типаж");
			chapter.setNumberDepth(0);

			p = new Paragraph();
			PDFUtil.printHeader(p, "Общий типаж");
			chapter.add(p);
			chapter.add(new Paragraph("Общий типаж – это общая характеристика поколения людей, рождённых вблизи " + DateUtil.sdf.format(event.getBirth()), font));

			//планеты в знаках
			printPlanetSign(chapter, event);
			doc.add(chapter);


			chapter = new ChapterAutoNumber("Анализ карты рождения");
			chapter.setNumberDepth(0);

			p = new Paragraph();
			PDFUtil.printHeader(p, "Анализ карты рождения");
			chapter.add(p);

			//вид космограммы
			printCardKind(chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//тип космограммы
			Map<String, Integer> signPlanetsMap = statistics.getSignPlanets();
			printCardType(chapter, event, signPlanetsMap);

			//планеты
			printPlanetStrong(chapter, event);
			printPlanetWeak(chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//аспекты
			p = new Paragraph();
			PDFUtil.printHeader(p, "Аспекты планет");
			chapter.add(p);

			chapter.add(new Paragraph("Сокращения:", font));
			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("\u2191 — сильное сочетание (хорошо для позитивных аспектов, плохо для негативных)", font));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("\u2193 — слабое сочетание (плохо для позитивных аспектов, хорошо для негативных)", font));
	        list.add(li);
	        chapter.add(list);

			printAspects(chapter, event, "Позитивные сочетания", "POSITIVE");
			printAspects(chapter, event, "Негативные сочетания", "NEGATIVE");
			chapter.add(Chunk.NEXTPAGE);

			//конфигурации аспектов
			printConfigurations(chapter, event);
			doc.add(chapter);


			chapter = new ChapterAutoNumber("Реализация личности");
			chapter.setNumberDepth(0);

			p = new Paragraph();
			PDFUtil.printHeader(p, "Реализация личности");
			chapter.add(p);

			//планеты в домах
			printPlanetHouses(chapter, event);
			doc.add(chapter);

			
			chapter = new ChapterAutoNumber("Диаграммы");
			chapter.setNumberDepth(0);

			p = new Paragraph();
			PDFUtil.printHeader(p, "Диаграммы");
			chapter.add(p);

			//координаты планет
			printCoords(chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//сила планет
			printPlanetStrength(writer, chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//аспекты
			printAspectTypes(writer, chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//стихии
			statistics.initPlanetDivisions();
			statistics.initHouseDivisions();
			printElements(writer, chapter, statistics);
			chapter.add(Chunk.NEXTPAGE);
	
			//инь-ян
			printYinYang(writer, chapter, statistics);
			chapter.add(Chunk.NEXTPAGE);
			
			//полусферы
			printHalfSpheres(writer, chapter, statistics);
			chapter.add(Chunk.NEXTPAGE);

			//квадраты
			printSquares(writer, chapter, statistics, signMap);
			chapter.add(Chunk.NEXTPAGE);
			
			//кресты
			printCrosses(writer, chapter, statistics);
			chapter.add(Chunk.NEXTPAGE);
			
			//зоны
			printZones(writer, chapter, statistics);
			doc.add(chapter);


			chapter = new ChapterAutoNumber("Сокращения");
			chapter.setNumberDepth(0);

			p = new Paragraph();
			PDFUtil.printHeader(p, "Сокращения");
			chapter.add(p);

			printAbbreviation(chapter);
			doc.add(chapter);

			doc.add(Chunk.NEWLINE);
	        doc.add(PDFUtil.printCopyright());
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
	        doc.close();
		}
	}

	/**
	 * Сохранение космограммы в PNG-файл
	 * @param event событие
	 */
	private void saveCard(Event event) {
		try {
		    Image image = new Image(display, Cosmogram.HEIGHT, Cosmogram.HEIGHT);
		    GC gc = new GC(image);
		    gc.setBackground(new Color(display, 254, 250, 248));
		    gc.fillRectangle(image.getBounds());
			new Cosmogram(event.getConfiguration(), null, null, gc);
			ImageLoader loader = new ImageLoader();
		    loader.data = new ImageData[] {image.getImageData()};
		    try {
				String card = PlatformUtil.getPath(Activator.PLUGIN_ID, "/out/card.png").getPath();
			    loader.save(card, SWT.IMAGE_PNG);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    image.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public void watermarkText(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        PdfContentByte under = stamper.getUnderContent(1);
        Font f = new Font(FontFamily.HELVETICA, 15);
        Phrase p = new Phrase(
            "This watermark is added UNDER the existing content", f);
        ColumnText.showTextAligned(under, Element.ALIGN_CENTER, p, 297, 550, 0);
        PdfContentByte over = stamper.getOverContent(1);
        p = new Phrase("This watermark is added ON TOP OF the existing content", f);
        ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, 297, 500, 0);
        p = new Phrase(
            "This TRANSPARENT watermark is added ON TOP OF the existing content", f);
        over.saveState();
        PdfGState gs1 = new PdfGState();
        gs1.setFillOpacity(0.5f);
        over.setGState(gs1);
        ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, 297, 450, 0);
        over.restoreState();
        stamper.close();
        reader.close();
    }

    public void watermarkImg() {
//    	Document document = new Document();
//    	PdfReader pdfReader = new PdfReader(strFileLocation);
//    	PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileStream(strFileLocationOut, FileMode.Create, FileAccess.Write, FileShare.None));
//    	iTextSharp.text.Image img = iTextSharp.text.Image.GetInstance(WatermarkLocation);
//    	
//    	Rectangle pagesize = reader.getCropBox(pageIndex);
//    	if (pagesize == null)
//    	    pagesize = reader.getMediaBox(pageIndex);
//    	img.SetAbsolutePosition(
//    	    pagesize.GetLeft(),
//    	    pagesize.GetBottom());
//    	
//    	img.SetAbsolutePosition(100, 300);
//    	PdfContentByte waterMark;
//    	for (int pageIndex = 1; pageIndex <= pdfReader.NumberOfPages; pageIndex++) {
//    	    waterMark = pdfStamper.GetOverContent(pageIndex);
//    	    waterMark.AddImage(img);
//    	}
//    	pdfStamper.FormFlattening = true;
//    	pdfStamper.Close();
	}

	/**
	 * Генерация знаменитостей
	 * @param date дата события
	 * @param cell тег-контейнер для вложенных тегов
	 */
	private void printCelebrities(Chapter chapter, Date date) {
		try {
			List<Event> events = new EventService().findEphemeron(date);
			if (events != null && events.size() > 0) {
				Section section = PDFUtil.printSection(chapter, "Однодневки");
				section.add(new Paragraph("В один день с вами родились такие известные люди:", font));

				com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
				for (Model model : events) {
					Event event = (Event)model;
					ListItem li = new ListItem();
			        Chunk chunk = new Chunk(DateUtil.formatDate(event.getBirth()), font);
			        li.add(chunk);

			        chunk = new Chunk("  ");
			        li.add(chunk);
			        chunk = new Chunk(event.getName(), fonta);
			        chunk.setAnchor(event.getUrl());
			        li.add(chunk);

			        chunk = new Chunk("   " + event.getDescription(), font);
			        li.add(chunk);
			        list.add(li);
				}
				section.add(list);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация похожих по характеру знаменитостей
	 * @param date дата события
	 * @param cell тег-контейнер для вложенных тегов
	 */
	private void printSimilar(Chapter chapter, Event event) {
		try {
			List<Model> events = new EventService().findSimilar(event, 1);
			if (events != null && events.size() > 0) {
				Section section = PDFUtil.printSection(chapter, "Близкие по духу");
				section.add(new Paragraph("Известные люди, похожие на вас по характеру:", font));

				com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
				for (Model model : events) {
					Event man = (Event)model;
					ListItem li = new ListItem();
			        Chunk chunk = new Chunk(DateUtil.formatDate(man.getBirth()), font);
			        li.add(chunk);

			        chunk = new Chunk("  ");
			        li.add(chunk);
			        chunk = new Chunk(man.getName(), fonta);
			        chunk.setAnchor(man.getUrl());
			        li.add(chunk);

			        chunk = new Chunk("   " + man.getDescription(), font);
			        li.add(chunk);
			        list.add(li);
				}
				section.add(list);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация диаграмм знаков
	 * @param cell тег-контейнер для вложенных тегов
	 * @param signMap карта знаков
	 */
	private void printSigns(PdfWriter writer, Chapter chapter, Map<String, Double> signMap) {
		try {
			//выраженные знаки
			Section section = PDFUtil.printSection(chapter, "Знаки Зодиака");

			int size = signMap.size();
			Bar[] bars = new Bar[size];
			Bar[] bars2 = new Bar[size];
			Iterator<Map.Entry<String, Double>> iterator = signMap.entrySet().iterator();
			int i = -1;
			SignService service = new SignService();
			double maxval = 0;
		    while (iterator.hasNext()) {
		    	i++;
		    	Entry<String, Double> entry = iterator.next();
		    	double val = entry.getValue();
		    	if (val > maxval)
		    		maxval = val;

		    	Bar bar = new Bar();
		    	Sign sign = (Sign)service.find(entry.getKey());
		    	bar.setName(sign.getName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(sign.getColor());
		    	bars[i] = bar;
	
		    	bar = new Bar();
		    	bar.setName(sign.getDescription());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(sign.getColor());
		    	bar.setCategory("Кредо");
		    	bars2[i] = bar;		    	
		    }
		    com.itextpdf.text.Image image = PDFUtil.printPie(writer, "Выраженные знаки Зодиака", bars, 400, 0, false);
			section.add(image);
	
			//кредо
			section = PDFUtil.printSection(chapter, "Кредо вашей жизни");
		    PdfPTable table = PDFUtil.printTableChart(writer, maxval, bars2, "Кредо вашей жизни");
			section.add(table);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Генерация градуса рождения
	 * @param event событие
	 * @param cell тег-контейнер для вложенных тегов
	 */
	private void printDegree(Chapter chapter, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Символ рождения");
			if (event.getConfiguration().getHouses() != null &&
					event.getConfiguration().getHouses().size() > 0) {
				House house = (House)event.getConfiguration().getHouses().get(0);
				if (null == house) return;
				int value = (int)house.getCoord();
				Model model = new DegreeService().find(new Long(String.valueOf(value)));
			    if (model != null) {
			    	Degree degree = (Degree)model;
			    	if (term)
			    		section.add(new Paragraph(degree.getId() + "° " + degree.getName() + ", " + degree.getCode(), fonth5));
					section.add(new Paragraph(degree.getDescription(), new Font(baseFont, 12, Font.ITALIC, PDFUtil.FONTCOLORGRAY)));
					section.add(new Paragraph(StringUtil.removeTags(degree.getText()), font));
					String filename = PlatformUtil.getPath(Activator.PLUGIN_ID, "/icons/degree/" + model.getId() + ".jpg").getPath();
					com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(filename);
					if (image != null)
						section.add(image);

			    }
			}
			Paragraph p = new Paragraph("Такую иллюстрацию можно нарисовать и повесить в месте вашего вдохновения", font);
			p.setSpacingBefore(10);
			section.add(p);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация космограммы
	 * @param doc документ
	 * @param chapter глава
	 */
	private void printCard(Document doc, Chapter chapter) {
		try {
			Section section = PDFUtil.printSection(chapter, "Карта рождения");

			String filename = PlatformUtil.getPath(Activator.PLUGIN_ID, "/out/card.png").getPath();
			com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(filename);
			float side = 300f;
			image.scaleAbsolute(side, side);
			float x = (doc.right() - doc.left()) / 2 - (side / 2);
			image.setIndentationLeft(x);
			section.add(image);

			String text = "Карта рождения (натальная карта) - это уникальный отпечаток положения планет на небе в момент вашего рождения. Планеты расположены так, как если бы вы смотрели на них с Земли:";
			section.add(new Paragraph(text, font));

			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			String[] items = {
				"ближе к точке ASC расположены планеты, восходящие над горизонтом",
				"ближе к MC - планеты в зените",
				"ближе к DSC - планеты, заходящие за горизонт",
				"ближе к IC - планеты в надире"
			};
			for (String item : items) {
				ListItem li = new ListItem();
		        Chunk chunk = new Chunk(item, font);
		        li.add(chunk);
		        list.add(li);
			}
			section.add(list);

			section.add(new Paragraph("Подробности в разделе «Координаты планет»", font));

			Font fontgray = new Font(baseFont, 12, Font.NORMAL, PDFUtil.FONTCOLORGRAY);
			section.add(new Paragraph("Сокращения и символы, использованные в тексте, описаны в конце документа", fontgray));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация планет в знаках
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printPlanetSign(Chapter chapter, Event event) {
		try {
			if (event.getConfiguration().getPlanets() != null) {
				PlanetSignService service = new PlanetSignService();
				for (Model model : event.getConfiguration().getPlanets()) {
					Planet planet = (Planet)model;
				    if (planet.isMain()) {
				    	List<PlanetSignText> list = service.find(planet, planet.getSign());
				    	if (list != null && list.size() > 0)
				    		for (PlanetSignText object : list) {
				    			Category category = object.getCategory();
				    			if (category.getCode().equals("childhood") && !child)
				    				continue;
				    			Section section = PDFUtil.printSection(chapter, category.getName());
				    			if (term) {
				    				section.add(new Chunk(planet.getMark("sign"), fonth5));
				    				section.add(new Chunk(planet.getSymbol(), PDFUtil.getHeaderAstroFont()));
				    				section.add(new Chunk(" " + planet.getName() + " в созвездии " + planet.getSign().getName() + " ", fonth5));
				    				section.add(new Chunk(planet.getSign().getSymbol(), PDFUtil.getHeaderAstroFont()));
				    				section.add(Chunk.NEWLINE);
				    			}
				    			section.add(PDFUtil.html2pdf(object.getText()));
				    			PDFUtil.printGender(section, object, female, child, true);
				    		}
				    }
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация таблицы координат планет и домов
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printCoords(Chapter chapter, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Координаты планет");
			float fontsize = 10;
			Font font = new Font(baseFont, fontsize, Font.NORMAL, BaseColor.BLACK);
			section.add(new Paragraph("Планеты в знаках Зодиака и астрологических домах:", this.font));

	        PdfPTable table = new PdfPTable(5);
	        table.setSpacingBefore(10);

			PdfPCell cell = new PdfPCell(new Phrase("Градус планеты", font));
	        cell.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("Планета", font));
	        cell.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("Созвездие", font));
	        cell.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("Градус дома", font));
	        cell.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("Дом", font));
	        cell.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cell);

			int i = -1;
			for (Model model : event.getConfiguration().getPlanets()) {
				BaseColor color = (++i % 2 > 0) ? new BaseColor(255, 255, 255) : new BaseColor(230, 230, 250);
				Planet planet = (Planet)model;

				cell = new PdfPCell(new Phrase(CalcUtil.roundTo(planet.getCoord(), 2) + "°", font));
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
				table.addCell(cell);

				Color scolor = planet.getColor();
				cell = new PdfPCell();
		        String descr = "";
				if (planet.isLord())
					descr += "влд ";

				if (planet.isKing())
					descr += "крл ";

				if (planet.isBelt())
					descr += "пояс ";
				else if (planet.isKernel())
					descr += "ядро ";

				if (planet.isPerfect())
					descr += "грм ";
				else if (planet.isDamaged())
					descr += "прж ";

				if (planet.isLilithed())
					descr += "сбз ";

				if (planet.isBroken() || planet.inMine())
					descr += "слб ";

				if (planet.isRetrograde())
					descr += "R";

				cell.addElement(new Phrase(planet.getName() + (descr.length() > 0 ? " (" + descr + ")" : ""), new Font(baseFont, fontsize, Font.NORMAL, new BaseColor(scolor.getRed(), scolor.getGreen(), scolor.getBlue()))));
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
		        table.addCell(cell);

				Sign sign = planet.getSign();
				scolor = sign.getElement().getDimColor();
		        cell = new PdfPCell();
		        descr = "";
				if (planet.isSignHome())
					descr = "(обт)";
				else if (planet.isSignExaltated())
					descr = "(экз)";
				else if (planet.isSignDeclined())
					descr = "(пдн)";
				else if (planet.isSignExile())
					descr = "(изг)";

				cell.addElement(new Phrase(sign.getName() + " " + descr, new Font(baseFont, fontsize, Font.NORMAL, new BaseColor(scolor.getRed(), scolor.getGreen(), scolor.getBlue()))));
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
				table.addCell(cell);

				House house = planet.getHouse();
				cell = new PdfPCell(new Phrase(CalcUtil.roundTo(house.getCoord(), 2) + "°", font));
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
				table.addCell(cell);

				scolor = house.getElement().getDimColor();
				cell = new PdfPCell();
		        descr = "";
				if (planet.isHouseHome())
					descr = "(обт)";
				else if (planet.isHouseExaltated())
					descr = "(экз)";
				else if (planet.isHouseDeclined())
					descr = "(пдн)";
				else if (planet.isHouseExile())
					descr = "(изг)";

				cell.addElement(new Phrase(house.getName() + " " + descr, new Font(baseFont, fontsize, Font.NORMAL, new BaseColor(scolor.getRed(), scolor.getGreen(), scolor.getBlue()))));
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
				table.addCell(cell);
			}
			section.add(table);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация вида космограммы
	 * @param chapter раздел
	 * @param event событие
	 * TODO фиктивные планеты при определении вида космограмм не считаются!!!
	 */
	private void printCardKind(Chapter chapter, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Кармический потенциал");
			section.add(new Paragraph("Рисунок космограммы показывает общую картину, "
				+ "которая не в деталях, а глобально описывает ваше предназначение и кармический опыт прошлого. "
				+ "Определите, на каком уровне вы находитесь. Отследите по трём уровням своё развитие", font));

			long id = 8L;
			CardKind kind = (CardKind)new CardKindService().find(id);
			Paragraph p = new Paragraph(kind.getName(), fonth5);
			p.setSpacingAfter(10);
			section.add(p);
			if (term)
				section.add(new Paragraph(kind.getDescription(), new Font(baseFont, 12, Font.ITALIC, PDFUtil.FONTCOLORGRAY)));
			String html = kind.getText();
			Phrase phrase = PDFUtil.html2pdf(html);
			section.add(phrase);

			if (1 == id) {
				PlanetTextService service = new PlanetTextService();
				PlanetText planetText = (PlanetText)service.findByPlanet(26L, "positive");
				if (planetText != null)
					section.add(PDFUtil.html2pdf(planetText.getText()));

				planetText = (PlanetText)service.findByPlanet(21L, "positive");
				if (planetText != null)
					section.add(PDFUtil.html2pdf(planetText.getText()));
			} else if (8 == id) { //праща
				Long pids[] = {21L, 26L, 30L};
				PlanetService service = new PlanetService();
				com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
				for (Long pid : pids) {
					Planet planet = (Planet)service.find(pid);
					if (planet != null) {
						ListItem li = new ListItem();
				        li.add(new Chunk(planet.getDescription(), font));
				        list.add(li);
					}
				}
				section.add(list);

				//Если планета-снаряд смещена влево
				boolean left = false;
				if (left) {
					section.add(Chunk.NEWLINE);
					Planet planet = (Planet)service.find(0L);
					Rule rule = EventRules.ruleCardKind(planet);
					if (rule != null)
						section.add(PDFUtil.html2pdf(rule.getText()));
				}
				//Если планета-снаряд смещена вправо
				boolean right = true;
				if (right) {
					section.add(Chunk.NEWLINE);
					RuleService rservice = new RuleService();
					Rule rule = (Rule)rservice.find(86L);
					if (rule != null)
						section.add(PDFUtil.html2pdf(rule.getText()));
				}
			}
//			CardKind type = null;
//			//упорядочиваем массив планет по возрастанию
//			List<Planet> planets = new ArrayList<Planet>();
//			for (BaseEntity entity : event.getConfiguration().getPlanets())
//				planets.add((Planet)entity);
//			Collections.sort(planets, new SkyPointComparator());
//			
//			//расчет интервалов между планетами
//			double max = 0.0;
//			double[] cuts = new double[planets.size()]; 
//			for (int i = 0; i < planets.size(); i++) {
//				int n = (i == planets.size() - 1) ? 0 : i + 1;
//				double value = CalcUtil.getDifference(planets.get(i).getCoord(), planets.get(n).getCoord());
//				cuts[i] = value;
//				if (value > max) max = value;
//			}
//			
//			if (type != null) {
//				Tag p = new Tag("h5"); 
//				p.add(type.getName());
//				td.add(p);
//				p = new Tag("p", "class=desc"); 
//				p.add(type.getDescription());
//				td.add(p);
//				p = new Tag("p"); 
//				p.add(type.getText());
//				td.add(p);
//			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация типа космограммы на основе положения Солнца и Луны
	 * @param chapter раздел
	 * @param event событие
	 * @param signMap карта знаков
	 */
	private void printCardType(Chapter chapter, Event event, Map<String, Integer> signMap) {
		try {
			if (event.getConfiguration().getPlanets() != null) {
				String type = "";
				Planet sun = (Planet)event.getConfiguration().getPlanets().get(0);
				Planet moon = (Planet)event.getConfiguration().getPlanets().get(1);
				
				if (sun.getSign().getId().equals(moon.getSign().getId()))
					type = "centered";
				else {
					int sunSign = signMap.get(sun.getSign().getCode());
					int moonSign = signMap.get(moon.getSign().getCode());
					if (sunSign > 1 & moonSign == 1)
						type = "solar";
					else if (sunSign == 1 & moonSign > 1)
						type = "lunar";
					else if (sunSign > 1 & moonSign > 1) {
						if (sunSign == moonSign)
							type = "equivalent";
						else
							type = (sunSign > moonSign) ? "solar_lunar" : "lunar_solar";
					} else if (sunSign == 1 & moonSign == 1) {
						//определяем знак, в котором больше всего планет
						int max = 0;
						for (Iterator<Integer> iterator = signMap.values().iterator(); iterator.hasNext();) {
							int value = iterator.next();
							if (max < value)
								max = value;
						}
						type = (max > 2) ? "planetary" : "scattered";
					}
				}
			
				if (type.length() > 0) {
				    Model model = new CardTypeService().find(type);
				    if (model != null) {
				    	TextGenderDictionary cardType = (TextGenderDictionary)model;
						Section section = PDFUtil.printSection(chapter, "Самораскрытие");
						if (term) {
							section.add(new Paragraph(cardType.getName(), fonth5));
							section.add(new Paragraph(cardType.getDescription(), new Font(baseFont, 12, Font.ITALIC, PDFUtil.FONTCOLORGRAY)));
						}
						section.add(new Paragraph(StringUtil.removeTags(cardType.getText()), font));
				    }
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация сильных планет
	 * @param chapter раздел
	 * @param event событие
	 * TODO разделить сильные и слабые
	 */
	private void printPlanetStrong(Chapter chapter, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Сильные стороны");
			PlanetTextService service = new PlanetTextService();
			List<Model> planets = event.getConfiguration().getPlanets();
			for (Model model : planets) {
				Planet planet = (Planet)model;
				PlanetText planetText = null;

				if (planet.isSword()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "sword");
					if (planetText != null) {
						section.add(new Paragraph((term ? planet.getName() : planet.getShortName()) + "-меч", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(planetText.getText()), font));

						Rule rule = EventRules.rulePlanetSword(planet, female);
						if (rule != null)
							section.add(PDFUtil.html2pdf(rule.getText()));

						PDFUtil.printGender(section, planetText, female, child, true);
					}
				} else if (planet.isShield()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "shield");
					if (planetText != null) {
						section.add(new Paragraph((term ? planet.getName() : planet.getShortName()) + "-щит", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(planetText.getText()), font));
						PDFUtil.printGender(section, planetText, female, child, true);
					}
				}
				if (planet.isPerfect() && !planet.isBroken()) {
					if (planet.inMine())
						section.add(new Paragraph("Планета " + planet.getName() + " не вызывает напряжения, так что вы сумеете проработать недостатки, описанные в разделе «" + planet.getShortName() + " в шахте»", fonth5));
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "perfect");
					if (planetText != null) {
						section.add(new Paragraph((term ? planet.getName() : planet.getShortName()) + "-гармония", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(planetText.getText()), font));
						PDFUtil.printGender(section, planetText, female, child, true);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация слабых планет
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printPlanetWeak(Chapter chapter, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Слабые стороны");
			PlanetTextService service = new PlanetTextService();
			List<Model> planets = event.getConfiguration().getPlanets();
			for (Model model : planets) {
				Planet planet = (Planet)model;
				PlanetText planetText = null;

				if (planet.inMine()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "mine");
					if (planetText != null) {
						section.add(new Paragraph((term ? planet.getName() : planet.getShortName()) + " в шахте", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(planetText.getText()), font));

						PlanetService planetService = new PlanetService();
						Planet ruler = planetService.getRuler(planet.getSign(), true);
						if (ruler != null) {
							planetText = (PlanetText)service.findByPlanet(ruler.getId(), "positive");
							if (planetText != null) {
								Paragraph p = new Paragraph("В этой ситуации вам помогут следующие сферы жизни:", font);
								p.setSpacingBefore(10);
								section.add(p);
								section.add(PDFUtil.html2pdf(planetText.getText()));
							}
						}
						PDFUtil.printGender(section, planetText, female, child, true);
					}
				} else if (planet.isDamaged() && !planet.isBroken()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "damaged");
					if (planetText != null) {
						section.add(new Paragraph((term ? planet.getName() : planet.getShortName()) + "-дисгармония", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(planetText.getText()), font));
						PDFUtil.printGender(section, planetText, female, child, true);
					}
				}
				if (planet.isRetrograde()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "retro");
					if (planetText != null) {
						section.add(new Paragraph((term ? planet.getName() : planet.getShortName()) + "-ретроград", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(planetText.getText()), font));
						PDFUtil.printGender(section, planetText, female, child, true);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация типов аспектов
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printAspectTypes(PdfWriter writer, Chapter chapter, Event event) {
		try {
			event.getConfiguration().initPlanetAspects();
			List<Model> planets = event.getConfiguration().getPlanets();
			//фильтрация списка типов аспектов
			List<Model> types = new AspectTypeService().getList();
			String[] codes = {
				"NEUTRAL", "NEGATIVE", "POSITIVE", "CREATIVE", "KARMIC", "SPIRITUAL", "PROGRESSIVE"
			};

			List<Bar> items = new ArrayList<Bar>();
		    for (Model tmodel : types) {
		    	AspectType mtype = null; 
		    	AspectType type = (AspectType)tmodel;
		    	if (Arrays.asList(codes).contains(type.getCode())) {
		    		mtype = type;
		    	} else if (type.getParentType() != null
		    			&& Arrays.asList(codes).contains(type.getParentType().getCode()))
		    		mtype = type.getParentType();
		    	if (null == mtype)
		    		continue;

		    	int value = 0;
		    	for (Model model : planets) {
		    		Planet planet = (Planet)model;
					value += planet.getAspectCountMap().get(type.getCode());
		    	}
		    	if (0 == value)
		    		continue;
		    	Bar bar = new Bar();
		    	bar.setName(mtype.getName()/*.substring(0, 4)*/);
		    	bar.setValue(value);
				bar.setColor(mtype.getColor());
				bar.setCategory("Аспекты");
				items.add(bar);
		    }
		    int size = items.size();
		    Bar[] bars = new Bar[size];
		    for (int i = 0; i < size; i++)
		    	bars[i] = items.get(i);

		    com.itextpdf.text.Image image = PDFUtil.printBars(writer, "Соотношение аспектов планет", "Аспекты", "Баллы", bars, 500, 300, false, false);
			Section section = PDFUtil.printSection(chapter, "Соотношение аспектов планет");
			section.add(image);

			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("Больше гармоничных аспектов — меньше препятствий", new Font(baseFont, 12, Font.NORMAL, BaseColor.RED)));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Больше творческих — меньше ограничений", new Font(baseFont, 12, Font.NORMAL, new BaseColor(0, 102, 51))));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Больше нейтральных — больше сфер жизни затрагивают изменения", new Font(baseFont, 12, Font.NORMAL, new BaseColor(255, 153, 51))));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Больше негармоничных — больше стрессовых ситуаций", font));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Больше скрытых — больше событий происходит за кулисами жизни", new Font(baseFont, 12, Font.NORMAL, BaseColor.GRAY)));
	        list.add(li);
			
			li = new ListItem();
	        li.add(new Chunk("Больше кармических — больше тупиковых ситуаций, которые нужно преодолеть", new Font(baseFont, 12, Font.NORMAL, BaseColor.BLUE)));
	        list.add(li);
			
			li = new ListItem();
	        li.add(new Chunk("Больше прогрессивных — бо́льшим испытаниям вы подвергнуты", new Font(baseFont, 12, Font.NORMAL, new BaseColor(51, 153, 153))));
	        list.add(li);
			
			li = new ListItem();
	        li.add(new Chunk("Чем больше духовных, тем более высокого уровня развития вы достигли", new Font(baseFont, 12, Font.NORMAL, BaseColor.MAGENTA)));
	        list.add(li);
			section.add(list);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация аспектов планет
	 * @param chapter раздел
	 * @param event событие
	 * @param title заголовок секции
	 * @param aspectType код типа аспектов
	 */
	private void printAspects(Chapter chapter, Event event, String title, String aspectType) {
		try {
			Section section = PDFUtil.printSection(chapter, title);
			if (aspectType.equals("NEGATIVE")) {
				Paragraph p = new Paragraph("В данном разделе описаны качества вашей личности, которые проявляются в критические моменты жизни:", font);
				p.setSpacingAfter(10);
				section.add(p);
			}
			PlanetAspectService service = new PlanetAspectService();
			Configuration conf = event.getConfiguration();
			List<SkyPointAspect> aspects = conf.getAspects();
			AspectType damaged = (AspectType)new AspectTypeService().find("NEGATIVE");

			for (SkyPointAspect aspect : aspects) {
				Planet planet1 = (Planet)aspect.getSkyPoint1();
				if (!planet1.isMain())
					continue;
				long asplanetid = aspect.getAspect().getPlanetid();
				if (asplanetid > 0 && asplanetid != planet1.getId())
					continue;
				Planet planet2 = (Planet)aspect.getSkyPoint2();
				if (planet1.getNumber() > planet2.getNumber())
					continue;

				AspectType type = aspect.getAspect().getType();
				if (type.getCode().equals("NEUTRAL")
						&& (planet1.isLilithed() || planet2.isLilithed()
							|| planet1.isKethued() || planet2.isKethued()))
					type = damaged;

				boolean match = false;
				//аспект соответствует заявленному (негативному или позитивному)
				if (type.getCode().equals(aspectType))
					match = true;
				//в позитивные добавляем ядро Солнца
				else if	(aspectType.equals("POSITIVE") &&
						type.getCode().equals("NEUTRAL_KERNEL"))
					match = true;
				//в негативные добавляем пояс Солнца
				else if (aspectType.equals("NEGATIVE") &&
						type.getCode().equals("NEGATIVE_BELT"))
					match = true;
				//в позитивные добавляем соединения без Лилит и Кету
				else if	(aspectType.equals("POSITIVE") &&
						!planet2.getCode().equals("Lilith") &&
						!planet2.getCode().equals("Kethu") &&
						type.getCode().equals("NEUTRAL"))
					match = true;
				//в негативные добавляем соединения с Лилит и Кету
				else if (aspectType.equals("NEGATIVE") &&
						((planet2.getCode().equals("Lilith") ||
						planet2.getCode().equals("Kethu")) &&
						type.getCode().equals("NEUTRAL")))
					match = true;

				if (match) {
					List<Model> dicts = service.finds(planet1, planet2, aspect.getAspect(), type);
					for (Model model : dicts) {
						PlanetAspectText dict = (PlanetAspectText)model;
						if (dict != null) {
		    				List<Model> planets = conf.getPlanets();
		    				int pindex = planets.indexOf(planet1);
		    				Planet aspl1 = (Planet)planets.get(pindex);
		    				pindex = planets.indexOf(planet2);
		    				Planet aspl2 = (Planet)planets.get(pindex);

		    				section.add(new Chunk(dict.getMark(aspl1, aspl2), fonth5));
							section.add(new Chunk(dict.getPlanet1().getShortName() + " " + 
								type.getSymbol() + " " + 
								dict.getPlanet2().getShortName(), fonth5));

							if (term) {
								section.add(new Chunk(" " + planet1.getSymbol(), PDFUtil.getHeaderAstroFont()));
	
			    				if (aspect.getAspect().getCode().equals("CONJUNCTION") || aspect.getAspect().getCode().equals("OPPOSITION"))
			    					section.add(new Chunk(aspect.getAspect().getSymbol(), PDFUtil.getHeaderAstroFont()));
			    				else
			    					section.add(new Chunk(type.getSymbol(), fonth5));
	
			    				section.add(new Chunk(planet2.getSymbol(), PDFUtil.getHeaderAstroFont()));
							}
							section.add(new Paragraph(StringUtil.removeTags(dict.getText()), font));

							Rule rule = EventRules.rulePlanetAspect(aspect);
							if (rule != null) {
			    				section.add(Chunk.NEWLINE);
								section.add(PDFUtil.html2pdf(rule.getText()));
							}
							PDFUtil.printGender(section, dict, female, child, true);
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация конфигурации аспектов
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printConfigurations(Chapter chapter, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Фигуры гороскопа");
		    Paragraph p = new Paragraph("Рисунок вашего гороскопа состоит из геометрических фигур, "
		    	+ "которые отражают взаимосвязи планет между собой. У всех людей они разные. "
		    	+ "Каждая фигура обобщает ваши сильные и слабые стороны, показывает главные источники роста и напряжения.", font);
		    p.setSpacingAfter(10);
		    section.add(p);

			List<Model> confs = new AspectConfigurationService().getList();
			String[] codes = {
				"stellium",		//0° 0° 0° 0°
//				"semivehicle",	//60° 180° 120°
				"cross",		//90° 90° 90° 90°
				"taucross",		//90° 180° 90°
				"dagger",		//135° 45° 45° 135°
				"poleaxe",		//135° 90° 135°
				"javelin",		//45° 90° 45°
				"davidstar",	//60° 60° 60° 60° 60° 60°
				"trapezoid",	//60° 60° 60° 180°
				"sail",			//120° 60° 60° 120°
				"triangle",		//120° 120° 120°
				"bisextile",	//60° 120° 60°
				"boomerang",	//150° 30° 30° 150°
//				"pitchfork",	//150° 60° 150°
				"vehicle",		//60° 120° 60° 120°
				"roof",			//30° 60° 30°
				"railing",		//150° 30° 150° 30°
				"cage",			//40° 40° 40° 40° 40° 40° 40° 40° 40°
				"box",			//20° 40° 100° 40°
				"lock",			//20° 40° 20°
				"lasso",		//40° 80° 40°
				"stretcher",	//100° 80° 100° 80°
				"wreath",		//72° 72° 72° 72° 72°
				"ship",			//72° 144° 72°
				"palm",			//144° 72° 144°
				"pyramid",		//150° 72° 135°
				"envelope",		//108° 72° 108° 72°
				"compass",		//108° 180° 72°
				"boat",			//36° 72° 36°
				"bilasso"		//80° 80° 160°
			};
			PlanetTextService service = new PlanetTextService();
			PlanetText text = null;
			HouseService houseService = new HouseService();
			House housetext = null;

			for (Model model : confs) {
				AspectConfiguration conf = (AspectConfiguration)model;
				String code = conf.getCode();

				if (code.equals("stellium")) {
					Map<String, Integer> signMap = new HashMap<String, Integer>();
					for (Model model2 : event.getConfiguration().getPlanets()) {
						Planet planet = (Planet)model2;
						Object object = signMap.get(planet.getSign().getCode());
						int	value = object != null ? (Integer)object : 0;
						signMap.put(planet.getSign().getCode(), ++value);
					}
					Iterator<Map.Entry<String, Integer>> iterator = signMap.entrySet().iterator();
				    while (iterator.hasNext()) {
				    	Entry<String, Integer> entry = iterator.next();
				    	double val = entry.getValue();
				    	if (val > 3) {
				    		Sign sign = (Sign)new SignService().find(entry.getKey());
				    		printConf(section, conf, sign);
				    		break;
				    	}
				    }
				} else {
					if (Arrays.asList(codes).contains(code))
			    		continue;
			    	printConf(section, conf, null);
				}

				if (code.equals("semivehicle")) {
					com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
					ListItem li = new ListItem();

					//планеты
//				    text = (PlanetText)service.findByPlanet(25L, "stage");
//					if (text != null) {
//					    li = new ListItem();
//						li.add(new Chunk(printStage(36, text.getStage(), font));
//						list.add(li);
//					}
					//дома
					housetext = (House)houseService.find(149L);
					if (housetext != null && housetext.getStage() != null) {
					    li = new ListItem();
						li.add(new Chunk(printStage(10, housetext.getStage()), font));
						list.add(li);
					}
					housetext = (House)houseService.find(149L);
					if (housetext != null && housetext.getStage() != null) {
					    li = new ListItem();
						li.add(new Chunk(printStage(13, housetext.getStage()), font));
						list.add(li);
					}
					housetext = (House)houseService.find(149L);
					if (housetext != null && housetext.getStage() != null) {
					    li = new ListItem();
						li.add(new Chunk(printStage(15, housetext.getStage()), font));
						list.add(li);
					}
					housetext = (House)houseService.find(148L);
					if (housetext != null && housetext.getStage() != null) {
					    li = new ListItem();
						li.add(new Chunk(printStage(20, housetext.getStage()), font));
						list.add(li);
					}
					housetext = (House)houseService.find(148L);
					if (housetext != null && housetext.getStage() != null) {
					    li = new ListItem();
						li.add(new Chunk(printStage(23, housetext.getStage()), font));
						list.add(li);
					}
					housetext = (House)houseService.find(148L);
					if (housetext != null && housetext.getStage() != null) {
					    li = new ListItem();
						li.add(new Chunk(printStage(25, housetext.getStage()), font));
						list.add(li);
					}
					housetext = (House)houseService.find(147L);
					if (housetext != null && housetext.getStage() != null) {
					    li = new ListItem();
						li.add(new Chunk(printStage(37, housetext.getStage()), font));
						list.add(li);
					}
					housetext = (House)houseService.find(147L);
					if (housetext != null && housetext.getStage() != null) {
					    li = new ListItem();
						li.add(new Chunk(printStage(40, housetext.getStage()), font));
						list.add(li);
					}
					housetext = (House)houseService.find(147L);
					if (housetext != null && housetext.getStage() != null) {
					    li = new ListItem();
						li.add(new Chunk(printStage(42, housetext.getStage()), font));
						list.add(li);
					}
				    section.add(list);

				} else if (code.equals("cross")) {
					text = (PlanetText)service.findByPlanet(19L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(31L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(28L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(30L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					Cross cross = (Cross)new CrossService().find(1L);
					if (cross != null) {
						section.add(new Paragraph(cross.getName(), fonth5));
						section.add(new Paragraph(StringUtil.removeTags(cross.getConfiguration()), font));
					}

				} else if (code.equals("taucross")) {
					text = (PlanetText)service.findByPlanet(24L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					Cross cross = (Cross)new CrossService().find(1L);
					if (cross != null) {
						String str = "Ваша реакция на вышеперечисленные ситуации";
						if (term)
							str += " (" + cross.getName() + ")";
						section.add(new Paragraph(str + ":", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(cross.getTau()), font));
					}
					section.add(Chunk.NEWLINE);

					text = (PlanetText)service.findByPlanet(19L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(25L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					cross = (Cross)new CrossService().find(3L);
					if (cross != null) {
						String str = "Ваша реакция на вышеперечисленные ситуации";
						if (term)
							str += " (" + cross.getName() + ")";
						section.add(new Paragraph(str + ":", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(cross.getTau()), font));
					}

				} else if (code.equals("javelin")) {
					text = (PlanetText)service.findByPlanet(24L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(25L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(19L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					boolean many = true;
					if (many) {
						Rule rule = (Rule)new RuleService().find(11L);
						section.add(PDFUtil.html2pdf(rule.getText()));
					}

				} else if (code.equals("sail")) {
					text = (PlanetText)service.findByPlanet(22L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

				} else if (code.equals("bisextile")) {
					text = (PlanetText)service.findByPlanet(29L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					//если несколько треугольников
					RuleService ruleService = new RuleService();
					Rule rule =	(Rule)ruleService.find(38L);
					if (rule != null)
						section.add(PDFUtil.html2pdf(rule.getText()));

				} else if (code.equals("pitchfork")) {
					text = (PlanetText)service.findByPlanet(21L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

				} else if (code.equals("vehicle")) {
					text = (PlanetText)service.findByPlanet(19L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(21L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(30L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

				} else if (code.equals("poleaxe")) {
					text = (PlanetText)service.findByPlanet(25L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(29L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

				} else if (code.equals("roof")) {
					text = (PlanetText)service.findByPlanet(23L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

				} else if (code.equals("palm")) {
					text = (PlanetText)service.findByPlanet(22L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

				} else if (code.equals("boomerang")) {
					//цель
					text = (PlanetText)service.findByPlanet(21L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					//источник
					text = (PlanetText)service.findByPlanet(22L, "negative");
					if (text != null) {
						section.add(new Paragraph("Бумеранг прилетит отсюда:", fonth5));
						section.add(PDFUtil.html2pdf(text.getText()));
					}

				} else if (code.equals("trapezoid")) {
					text = (PlanetText)service.findByPlanet(20L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(26L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

				} else if (code.equals("dagger")) {
					text = (PlanetText)service.findByPlanet(22L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

				} else if (code.equals("lock")) {
					Long pids[] = {24L};
					PlanetService pservice = new PlanetService();
					com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
					for (Long pid : pids) {
						Planet planet = (Planet)pservice.find(pid);
						if (planet != null) {
							ListItem li = new ListItem();
					        li.add(new Chunk(planet.getDescription(), font));
					        list.add(li);
						}
					}
					section.add(list);
				}
				section.add(Chunk.NEWLINE);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация диаграмм домов
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param statistics объект статистики события
	 */
	private void printHouses(PdfWriter writer, Chapter chapter, EventStatistics statistics) {
		try {
			Section section = PDFUtil.printSection(chapter, "Сферы жизни");
			section.add(new Paragraph("Сферы жизни отражают ваши врождённые возможности, багаж, с которым вы пришли в этот мир. "
				+ "Пригодится он вам или нет – покажет время. "
				+ "В любом случае, это отправная точка корабля событий, на котором вы поплывёте по морю жизни и реализуете свою миссию", font));

			
			Map<String, Double> houses = statistics.getPlanetHouses();

			Bar[] bars = new Bar[houses.size()];
			Iterator<Map.Entry<String, Double>> iterator = houses.entrySet().iterator();
		    int i = -1;
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	House house = statistics.getHouse(entry.getKey());
		    	Bar bar = new Bar();
		    	bar.setName(term ? house.getName() : house.getName());
		    	bar.setValue(entry.getValue());
				bar.setColor(house.getColor());
				bar.setCategory("Сферы жизни");
				bars[++i] = (bar);
		    }
		    com.itextpdf.text.Image image = PDFUtil.printBars(writer, "", "Сферы жизни", "Баллы", bars, 500, 500, false, false);
			section.add(image);
			section.add(new Paragraph("Более подробно сферы жизни описаны в разделе «Реализация личности»", font));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация планет в домах и домов в знаках
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printPlanetHouses(Chapter chapter, Event event) {
	    Paragraph p = new Paragraph("Этот раздел в меньшей степени рассказывает о вашем характере и в большей степени говорит о том, "
	    	+ "что произойдёт в реальности, и как вы к этому отнесётесь. Здесь описаны важные для вас сферы жизни", font);
	    p.setSpacingAfter(10);
    	chapter.add(p);

		List<Model> houses = event.getConfiguration().getHouses();
		if (null == houses) return;
		List<Model> cplanets = event.getConfiguration().getPlanets();
		try {
			PlanetHouseService service = new PlanetHouseService();
			HouseSignService hservice = new HouseSignService();
			for (Model hmodel : houses) {
				House house = (House)hmodel;
				/*
				 * Игнорируем 3 треть 1 дома, т.к. она неверно толкует внешность.
				 * TODO в таблицу домов в знаках прописать туда внешности. А в планетах в домах описывать физ. данные
				 * TODO описание внешности должно содержать всё до мелочей от цвета глаз до роста!
				 * отдельная тема для исследования
				 */
//				if (house.getCode().equals("I_3")) continue;

				//Определяем количество планет в доме
				List<Planet> planets = new ArrayList<Planet>();
				for (Model pmodel : cplanets) {
					Planet planet = (Planet)pmodel;
					if (planet.getCode().equals("Kethu"))
						continue;
					if (planet.getHouse().getId().equals(house.getId()))
						planets.add(planet);
				}
				//Создаем информационный блок, только если дом не пуст
				Section section = null;
				if (planets.size() > 0) {
					section = PDFUtil.printSection(chapter, house.getName());
			
					for (Planet planet : planets) {
						String sign = planet.isDamaged() || planet.isLilithed() ? "-" : "+";

						String mark = planet.getMark("house");
						if (mark.length() > 0) {
		    				section.add(new Chunk(mark, fonth5));
		    				section.add(new Chunk(planet.getSymbol() + " ", PDFUtil.getHeaderAstroFont()));
						}
		    			if (term) {
		    				section.add(new Chunk(" " + planet.getName() + " в " + house.getDesignation() + " доме", fonth5));
		    				section.add(Chunk.NEWLINE);
		    			} else
		    				section.add(new Chunk(planet.getShortName() + " " + sign + " " + house.getName(), fonth5));

						PlanetHouseText dict = (PlanetHouseText)service.find(planet, house, null);
						if (dict != null) {
							section.add(new Paragraph(StringUtil.removeTags(dict.getText()), font));
							PDFUtil.printGender(section, dict, female, child, true);

							Rule rule = EventRules.rulePlanetHouse(planet, house, female);
							if (rule != null) {
								section.add(PDFUtil.html2pdf(rule.getText()));
								section.add(Chunk.NEWLINE);
							}
						}
					}
				}

				//добавляем информацию о доме в знаке
				if (!house.isExportOnSign())
					continue;

				Sign sign = SkyPoint.getSign(house.getCoord(), event.getBirthYear());
				HouseSignText dict = (HouseSignText)hservice.find(house, sign);
				if (dict != null) {
					if (null == section)
						section = PDFUtil.printSection(chapter, house.getName());
					if (term)
						section.add(new Paragraph(house.getDesignation() + " в созвездии " + sign.getName(), fonth5));
					else
						section.add(new Paragraph(house.getName() + " + " + sign.getShortname(), fonth5));
					section.add(new Paragraph(StringUtil.removeTags(dict.getText()), font));
					PDFUtil.printGender(section, dict, female, child, true);

					Rule rule = EventRules.ruleHouseSign(house, sign, event);
					if (rule != null) {
						section.add(PDFUtil.html2pdf(rule.getText()));
						section.add(Chunk.NEWLINE);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация стихий
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param statistics объект статистики
	 */
	private void printElements(PdfWriter writer, Chapter chapter, EventStatistics statistics) {
		try {
			Section section = PDFUtil.printSection(chapter, "Темперамент");
			
			Map<String, Double> planetMap = statistics.getPlanetElements();
			Map<String, Double> houseMap = statistics.getHouseElements();

			String[] elements = new String[planetMap.size()];
			Bar[] bars = new Bar[planetMap.size() + houseMap.size()];
			Iterator<Map.Entry<String, Double>> iterator = planetMap.entrySet().iterator();
			int i = -1;
			ElementService service = new ElementService();
		    while (iterator.hasNext()) {
		    	i++;
		    	Entry<String, Double> entry = iterator.next();
		    	elements[i] = entry.getKey();
		    	Bar bar = new Bar();
		    	kz.zvezdochet.bean.Element element = (kz.zvezdochet.bean.Element)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue() * (-1));
		    	bar.setColor(element.getColor());
		    	bar.setCategory("Темперамент в сознании");
		    	bars[i] = bar;
		    }
		    
			//определение выраженной стихии
		    Arrays.sort(elements);
		    kz.zvezdochet.bean.Element element = null;
		    List<Model> elist = service.getList(false);
		    for (Model model : elist) {
		    	kz.zvezdochet.bean.Element e = (kz.zvezdochet.bean.Element)model;
		    	String[] codes = e.getCode().split("_");
		    	if (codes.length == elements.length) {
			    	Arrays.sort(codes);
		    		boolean match = Arrays.equals(codes, elements);
		    		if (match) {
		    			element = e;
		    			break;
		    		} else
		    			continue;
		    	}
		    }
		    if (element != null) {
		    	String text = element.getTemperament();
		    	if (term)
		    		text += " (" + element.getName() + ")";
		    	section.add(new Paragraph(text, fonth5));
		    	section.add(new Paragraph(StringUtil.removeTags(element.getText()), font));
		    	PDFUtil.printGender(section, element, female, child, true);
		    }

			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("Категория \"Темперамент в сознании\" показывает вашу идеальную модель: "
					+ "на чём мысленно вы сконцентрированы, какие проявления для вас важны, необходимы и естественны.", font));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Категория \"Темперамент в поступках\" показывает, "
					+ "как меняются ваши приоритеты на событийном уровне, в социуме по сравнению с предыдущей моделью.", font));
	        list.add(li);
	        section.add(list);

			iterator = houseMap.entrySet().iterator();
			i = planetMap.size() - 1;
		    while (iterator.hasNext()) {
		    	i++;
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	element = (kz.zvezdochet.bean.Element)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bar.setCategory("Темперамент в поступках");
		    	bars[i] = bar;
		    }
		    com.itextpdf.text.Image image = PDFUtil.printStackChart(writer, "Сравнение темпераментов", "Аспекты", "Баллы", bars, 500, 0, true);
			section.add(image);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация инь-ян
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param statistics объект статистики
	 */
	private void printYinYang(PdfWriter writer, Chapter chapter, EventStatistics statistics) {
		try {
			Section section = PDFUtil.printSection(chapter, "Мужское и женское начало");
			
			Map<String, Double> planetMap = statistics.getPlanetYinYangs();
			Map<String, Double> houseMap = statistics.getHouseYinYangs();

			Bar[] bars = new Bar[planetMap.size() + houseMap.size()];
			Iterator<Map.Entry<String, Double>> iterator = planetMap.entrySet().iterator();
			int i = -1;
			YinYang yinyang = null;
			double score = 0.0;
			YinYangService service = new YinYangService();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	YinYang element = (YinYang)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue() * (-1));
		    	bar.setColor(element.getColor());
		    	bar.setCategory("Мужское и женское в сознании");
		    	bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		yinyang = element;
		    	}
		    }
		    if (yinyang != null) {
		    	if (term)
		    		section.add(new Paragraph(yinyang.getDescription(), fonth5));
		    	section.add(new Paragraph(StringUtil.removeTags(yinyang.getText()), font));
		    	PDFUtil.printGender(section, yinyang, female, child, true);
		    }

			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("Категория \"Мужское и женское в сознании\" показывает, "
			    	+ "насколько вы активны в мыслях и принятии решений наедине с самим собой.", font));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Категория \"Мужское и женское в поступках\" показывает, "
					+ "как меняется ваша активность на событийном уровне, в социуме по сравнению с предыдущей моделью.", font));
	        list.add(li);
			section.add(list);

			iterator = houseMap.entrySet().iterator();
			i = planetMap.size() - 1;
			yinyang = null;
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	YinYang element = (YinYang)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bar.setCategory("Мужское и женское в поступках");
		    	bars[++i] = bar;
		    }
		    com.itextpdf.text.Image image = PDFUtil.printStackChart(writer, "Мужское и женское начало", "Аспекты", "Баллы", bars, 500, 150, true);
			section.add(image);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация полусфер
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param statistics объект статистики
	 */
	private void printHalfSpheres(PdfWriter writer, Chapter chapter, EventStatistics statistics) {
		try {
			Section section = PDFUtil.printSection(chapter, "Тип личности");
			
			Map<String, Double> planetMap = statistics.getPlanetHalfspheres();
			Map<String, Double> houseMap = statistics.getHouseHalfspheres();

			Bar[] bars = new Bar[8];
			Iterator<Map.Entry<String, Double>> iterator = planetMap.entrySet().iterator();
			Halfsphere sphere = null;
			double score = 0.0;
			HalfsphereService service = new HalfsphereService();
			int i = -1;
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Halfsphere element = (Halfsphere)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue() * (-1));
		    	bar.setColor(element.getColor());
		    	bar.setCategory("В мыслях");
	    		bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		sphere = element;
		    	}
		    }
		    if (sphere != null) {
		    	section.add(new Paragraph(sphere.getName(), fonth5));
		    	if (term)
		    		section.add(new Paragraph(sphere.getDescription(), new Font(baseFont, 12, Font.ITALIC, PDFUtil.FONTCOLORGRAY)));
		    	section.add(new Paragraph(StringUtil.removeTags(sphere.getText()), font));
		    	PDFUtil.printGender(section, sphere, female, child, true);
		    }

			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("Открытость выражается в самоутверждении и сотрудничестве", font));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Закрытость – в духовности и материализме", font));
	        list.add(li);
			section.add(list);

			iterator = houseMap.entrySet().iterator();
			sphere = null;
			i = 3;
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Halfsphere element = (Halfsphere)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bar.setCategory("В поступках");
	    		bars[++i] = bar;
		    }
		    com.itextpdf.text.Image image = PDFUtil.printStackChart(writer, "Открытость и закрытость", "Аспекты", "Баллы", bars, 500, 0, true);
			section.add(image);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация квадратов
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param statistics объект статистики
	 * @param signMap карта знаков Зодиака
	 */
	private void printSquares(PdfWriter writer, Chapter chapter, EventStatistics statistics, Map<String, Double> signMap) {
		try {
			Section section = PDFUtil.printSection(chapter, "Зрелость");
			Map<String, Double> planetMap = statistics.getPlanetSquares();
			Map<String, Double> houseMap = statistics.getHouseSquares();

			Bar[] bars = new Bar[8];
			Iterator<Map.Entry<String, Double>> iterator = planetMap.entrySet().iterator();
			int i = -1;
			Square square = null;
			double score = 0.0;
			SquareService service = new SquareService();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Square element = (Square)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue() * (-1));
		    	bar.setColor(element.getColor());
		    	bar.setCategory("Зрелость в сознании");
		    	bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		square = element;
		    	}
		    }
			iterator = houseMap.entrySet().iterator();
			i = 3;
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Square element = (Square)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bar.setCategory("Зрелость в поступках");
		    	bars[++i] = bar;
		    }
		    if (square != null) {
		    	if (term)
		    		section.add(new Paragraph(square.getDescription(), fonth5));
		    	section.add(new Paragraph(StringUtil.removeTags(square.getText()), font));
		    	PDFUtil.printGender(section, square, female, child, true);
		    }
		    Paragraph p = new Paragraph("Диаграмма показывает, как в ваших мыслях и поступках выражены качества разных возрастных групп:", font);
		    p.setSpacingBefore(10);
	    	section.add(p);
		    com.itextpdf.text.Image image = PDFUtil.printStackChart(writer, "Зрелость", "Аспекты", "Баллы", bars, 500, 0, true);
			section.add(image);

			//знаки
			bars = new Bar[signMap.size()];
			iterator = signMap.entrySet().iterator();
			i = -1;
			SignService service2 = new SignService();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	double val = entry.getValue();
		    	if (0 == val)
		    		continue;
		    	Bar bar = new Bar();
		    	Sign element = (Sign)service2.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(val);
		    	bar.setColor(element.getColor());
		    	bars[++i] = bar;
		    }
		    image = PDFUtil.printPie(writer, "В каких качествах выражена зрелость мыслей", bars, 500, 0, false);
			section.add(image);

			//дома
			houseMap = statistics.getMainPlanetHouses(); //TODO найти более оптимальный вариант, мат.формулу
			bars = new Bar[houseMap.size()];
			iterator = houseMap.entrySet().iterator();
			i = -1;
			HouseService hservice = new HouseService();
			   while (iterator.hasNext()) {
			   	Entry<String, Double> entry = iterator.next();
		    	double val = entry.getValue();
		    	if (0 == val)
		    		continue;
			   	Bar bar = new Bar();
				//по индексу трети определяем дом, в котором она находится
			   	House element = (House)hservice.find(entry.getKey());
			   	bar.setName(element.getDiaName());
			   	bar.setValue(val);
			   	bar.setColor(element.getColor());
			   	bars[++i] = bar;
			}
		    image = PDFUtil.printPie(writer, "В каких качествах выражена зрелость поступков", bars, 500, 0, false);
			section.add(image);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация крестов
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param statistics объект статистики
	 */
	private void printCrosses(PdfWriter writer, Chapter chapter, EventStatistics statistics) {
		try {
			Section section = PDFUtil.printSection(chapter, "Стратегия");
			
			Map<String, Double> crossMap = statistics.getPlanetCrosses();
			Bar[] bars = new Bar[6];
			Iterator<Map.Entry<String, Double>> iterator = crossMap.entrySet().iterator();
			int i = -1;
			Cross cross = null;
			double score = 0.0;
			CrossService service = new CrossService();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Cross element = (Cross)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue() * (-1));
		    	bar.setColor(element.getColor());
		    	bar.setCategory("Стратегия в сознании");
		    	bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		cross = element;
		    	}
		    }
			crossMap = statistics.getHouseCrosses();
			iterator = crossMap.entrySet().iterator();
			i = 2;
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Cross element = (Cross)service.find(entry.getKey());
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bar.setCategory("Стратегия в поступках");
		    	bars[++i] = bar;
		    }
		    if (cross != null) {
		    	if (term)
		    		section.add(new Paragraph(cross.getName() + ": " + cross.getDescription(), fonth5));
		    	section.add(new Paragraph(StringUtil.removeTags(cross.getText()), font));
		    	PDFUtil.printGender(section, cross, female, child, true);
		    }
		    Paragraph p = new Paragraph("Диаграмма показывает, какой тип стратегии присущ вашим мыслям. " +
				"И как эта стратегия меняется при принятии решений в действии (на событийном уровне, в социуме):", font);
		    p.setSpacingBefore(10);
	    	section.add(p);
		    com.itextpdf.text.Image image = PDFUtil.printStackChart(writer, "Стратегия", "Аспекты", "Баллы", bars, 500, 0, true);
			section.add(image);

			//знаки
			crossMap = statistics.getCrossSigns();
			bars = new Bar[crossMap.size()];
			iterator = crossMap.entrySet().iterator();
			i = -1;
			CrossSignService service2 = new CrossSignService();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	double val = entry.getValue();
		    	if (0 == val)
		    		continue;
		    	Bar bar = new Bar();
		    	CrossSign element = (CrossSign)service2.find(entry.getKey());
		    	bar.setName(element.getName());
		    	bar.setValue(val);
		    	bar.setColor(element.getColor());
		    	bars[++i] = bar;
		    }
		    image = PDFUtil.printPie(writer, "В каких качествах выражена стратегия намерений", bars, 500, 0, false);
			section.add(image);

			//дома
			crossMap = statistics.getCrossHouses();
			bars = new Bar[crossMap.size()];
			iterator = crossMap.entrySet().iterator();
			i = -1;
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	double val = entry.getValue();
		    	if (0 == val)
		    		continue;
		    	Bar bar = new Bar();
		    	CrossSign element = (CrossSign)service2.find(entry.getKey());
		    	bar.setName(element.getName());
		    	bar.setValue(val);
		    	bar.setColor(element.getColor());
		    	bars[++i] = bar;
		    }
		    image = PDFUtil.printPie(writer, "В каких качествах выражена стратегия действий", bars, 500, 0, false);
			section.add(image);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация зон
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param statistics объект статистики
	 */
	private void printZones(PdfWriter writer, Chapter chapter, EventStatistics statistics) {
		try {
			Section section = PDFUtil.printSection(chapter, "Развитие духа");
			
			Map<String, Double> zoneMap = statistics.getPlanetZones();
			Bar[] bars = new Bar[6];
			Iterator<Map.Entry<String, Double>> iterator = zoneMap.entrySet().iterator();
			int i = -1;
			Zone zone = null;
			double score = 0.0;
			ZoneService service = new ZoneService();
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Zone element = (Zone)service.find(entry.getKey());
		    	bar.setName(element.getName());
		    	bar.setValue(entry.getValue() * (-1));
		    	bar.setColor(element.getColor());
		    	bar.setCategory("Развитие духа в сознании");
		    	bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		zone = element;
		    	}
		    }
			zoneMap = statistics.getHouseZones();
			iterator = zoneMap.entrySet().iterator();
			i = 2;
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	Bar bar = new Bar();
		    	Zone element = (Zone)service.find(entry.getKey());
		    	bar.setName(element.getName());
		    	bar.setValue(entry.getValue());
		    	bar.setColor(element.getColor());
		    	bar.setCategory("Развитие духа в поступках");
		    	bars[++i] = bar;
		    }
		    if (zone != null) {
		    	if (term)
		    		section.add(new Paragraph(zone.getDescription(), fonth5));
		    	section.add(new Paragraph(StringUtil.removeTags(zone.getText()), font));
		    	PDFUtil.printGender(section, zone, female, child, true);
		    }
		    Paragraph p = new Paragraph("Диаграмма показывает, какие приоритеты вы ставите для своего развития. " +
				"И как на событийном уровне (в действии) они меняются:", font);
		    p.setSpacingBefore(10);
	    	section.add(p);
		    com.itextpdf.text.Image image = PDFUtil.printStackChart(writer, "Развитие духа", "Аспекты", "Баллы", bars, 500, 0, true);
			section.add(image);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация силы планет
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printPlanetStrength(PdfWriter writer, Chapter chapter, Event event) {
		try {
			List<Model> planets = event.getConfiguration().getPlanets();

		    Bar[] bars = new Bar[planets.size()];
		    int i = -1;
		    for (Model model : planets) {
	    		Planet planet = (Planet)model;
		    	Bar bar = new Bar();
		    	bar.setName(term ? planet.getName() : planet.getShortName());
		    	bar.setValue(planet.getPoints());
				bar.setColor(planet.getColor());
				bar.setCategory("Планеты");
				bars[++i] = (bar);
		    }
		    com.itextpdf.text.Image image = PDFUtil.printBars(writer, "", "Планеты", "Баллы", bars, 500, 500, false, false);
		    String text = term ? "Соотношение силы планет" : "Соотношение силы качеств";
			Section section = PDFUtil.printSection(chapter, text);
		    text = term ? "Чем выше значение, тем легче и активнее планета выражает свои качества" : "Чем выше значение, тем легче и активнее проявляются качества";
	    	section.add(new Paragraph(text, font));
			section.add(image);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Сокращения, использованные в документе
	 * @param chapter раздел
	 */
	private void printAbbreviation(Chapter chapter) {
		try {
			chapter.add(new Paragraph("Сокращения:", font));
			com.itextpdf.text.List ilist = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("\u2191 — сильная планета, адекватно проявляющая себя в гороскопе", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("\u2193 — ослабленная планета, чьё проявление связано с неуверенностью, стрессом и препятствиями", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("R — ретроградная планета, проявление качеств которой неочевидно и неуверенно", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("влд — владыка гороскопа, самая сильная планета", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("грм — гармоничная планета, способная преодолеть негатив", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("изг — планета в изгнании, что-то мешает проявлению её качеств", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("крл — король аспектов, самая позитивная планета", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("обт — планета в обители, проявляющая себя естественно и свободно", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("пдн — планета в падении (чувствует себя «не в своей тарелке»)", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("пояс — ущербная планета, чьи качества подавлены", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("прж — поражённая планета, несущая стресс и препятствия", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("сбз — планета-источник порока и соблазна", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("слб — слабо развитая планета", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("экз — планета в экзальтации, способная максимально проявить себя", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("ядро — планета-источник потенциала", font));
	        ilist.add(li);
	        chapter.add(ilist);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация символы знака Солнца
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printSign(Chapter chapter, Event event) {
		try {
			if (event.getConfiguration().getPlanets() != null) {
		    	Sign sign = null;
				Font bold = PDFUtil.getSubheaderFont();

				Section section = PDFUtil.printSection(chapter, "Предназначение");
				com.itextpdf.text.List ilist = new com.itextpdf.text.List(false, false, 10);
				for (Model model : event.getConfiguration().getPlanets()) {
					Planet planet = (Planet)model;
				    if (planet.getCode().equals("Sun")) {
				    	sign = planet.getSign();

						ListItem li = new ListItem();
				        li.add(new Chunk("Предназначение Духа: ", bold));
				        li.add(new Chunk(sign.getSlogan(), font));
				        ilist.add(li);
				    } else if (planet.getCode().equals("Moon")) {
						ListItem li = new ListItem();
				        li.add(new Chunk("Предназначение Души: ", bold));
				        li.add(new Chunk(planet.getSign().getSlogan(), font));
				        ilist.add(li);
				    } else if (planet.getCode().equals("Mercury")) {
						ListItem li = new ListItem();
				        li.add(new Chunk("Предназначение Ума: ", bold));
				        li.add(new Chunk(planet.getSign().getSlogan(), font));
				        ilist.add(li);
				    } else if (planet.getCode().equals("Venus")) {
						ListItem li = new ListItem();
				        li.add(new Chunk("Предназначение Любви: ", bold));
				        li.add(new Chunk(planet.getSign().getSlogan(), font));
				        ilist.add(li);
				    } else if (planet.getCode().equals("Mars")) {
						ListItem li = new ListItem();
				        li.add(new Chunk("Предназначение Силы: ", bold));
				        li.add(new Chunk(planet.getSign().getSlogan(), font));
				        ilist.add(li);
				    }
				}
		        section.add(ilist);

		        if (sign != null) {
					section = PDFUtil.printSection(chapter, "Символы удачи");
					ilist = new com.itextpdf.text.List(false, false, 10);
					ListItem li = new ListItem();
					li.add(new Chunk("Счастливое число: ", bold));
			        li.add(new Chunk(sign.getNumbers(), font));
			        ilist.add(li);

			        if (!StringUtil.isEmpty(sign.getYears())) {
						li = new ListItem();
				        li.add(new Chunk("Критический возраст: ", bold));
				        li.add(new Chunk(sign.getYears(), font));
				        ilist.add(li);
			        }

			        if (!StringUtil.isEmpty(sign.getColors())) {
						li = new ListItem();
				        li.add(new Chunk("Благоприятные цвета: ", bold));
				        li.add(new Chunk(sign.getColors(), font));
				        ilist.add(li);
			        }

			        if (!StringUtil.isEmpty(sign.getAnticolors())) {
						li = new ListItem();
				        li.add(new Chunk("Неблагоприятные цвета: ", bold));
				        li.add(new Chunk(sign.getAnticolors(), font));
				        ilist.add(li);
			        }

			        if (!StringUtil.isEmpty(sign.getWeekdays())) {
						li = new ListItem();
				        li.add(new Chunk("Благоприятный день недели: ", bold));
				        li.add(new Chunk(sign.getWeekdays(), font));
				        ilist.add(li);
			        }

					li = new ListItem();
			        li.add(new Chunk("Талисман: ", bold));
			        li.add(new Chunk(sign.getTalisman(), font));
			        ilist.add(li);

			        if (!StringUtil.isEmpty(sign.getAmulet())) {
						li = new ListItem();
				        li.add(new Chunk("Амулет: ", bold));
				        li.add(new Chunk(sign.getAmulet(), font));
				        ilist.add(li);
			        }

					li = new ListItem();
			        li.add(new Chunk("Благоприятные камни: ", bold));
			        li.add(new Chunk(sign.getJewel(), font));
			        ilist.add(li);

			        if (!StringUtil.isEmpty(sign.getMineral())) {
						li = new ListItem();
				        li.add(new Chunk("Благоприятные минералы: ", bold));
				        li.add(new Chunk(sign.getMineral(), font));
				        ilist.add(li);
			        }

			        if (!StringUtil.isEmpty(sign.getMetal())) {
						li = new ListItem();
				        li.add(new Chunk("Благоприятные металлы: ", bold));
				        li.add(new Chunk(sign.getMetal(), font));
				        ilist.add(li);
			        }

			        if (!StringUtil.isEmpty(sign.getFlowers())) {
						li = new ListItem();
				        li.add(new Chunk("Благоприятные растения: ", bold));
				        li.add(new Chunk(sign.getFlowers(), font));
				        ilist.add(li);
			        }
			        section.add(ilist);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация конфигурации аспектов
	 * @param section раздел
	 * @param conf конфигурация
	 */
	private void printConf(Section section, AspectConfiguration conf, Sign sign) {
		try {
			String filename = PlatformUtil.getPath(Activator.PLUGIN_ID, "/icons/conf/" + conf.getCode() + ".gif").getPath();
			com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(filename);
			section.add(image);

			section.add(new Paragraph(conf.getName(), fonth5));
			String text = StringUtil.removeTags(conf.getText());
			if (sign != null) {
				text = text.replace("{sign}", sign.getName());
				text = text.replace("{merit}", sign.getKeyword());
			}
			section.add(new Paragraph(text, font));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BadElementException e) {
			e.printStackTrace();
		}
	}

	private String printStage(int age, String text) {
		return CoreUtil.getAgeString(age) + " - " + text;
	}
}
