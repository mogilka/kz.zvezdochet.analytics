package kz.zvezdochet.analytics.exporter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

import com.itextpdf.text.Anchor;
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
import com.itextpdf.text.Rectangle;
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
import kz.zvezdochet.analytics.bean.Category;
import kz.zvezdochet.analytics.bean.CrossSign;
import kz.zvezdochet.analytics.bean.Degree;
import kz.zvezdochet.analytics.bean.HouseSignText;
import kz.zvezdochet.analytics.bean.Moonday;
import kz.zvezdochet.analytics.bean.Numerology;
import kz.zvezdochet.analytics.bean.PlanetAspectText;
import kz.zvezdochet.analytics.bean.PlanetHouseText;
import kz.zvezdochet.analytics.bean.PlanetSignText;
import kz.zvezdochet.analytics.bean.PlanetText;
import kz.zvezdochet.analytics.bean.Rule;
import kz.zvezdochet.analytics.service.AspectConfigurationService;
import kz.zvezdochet.analytics.service.CardTypeService;
import kz.zvezdochet.analytics.service.CrossSignService;
import kz.zvezdochet.analytics.service.DegreeService;
import kz.zvezdochet.analytics.service.HouseSignService;
import kz.zvezdochet.analytics.service.MoondayService;
import kz.zvezdochet.analytics.service.NumerologyService;
import kz.zvezdochet.analytics.service.PlanetAspectService;
import kz.zvezdochet.analytics.service.PlanetHouseService;
import kz.zvezdochet.analytics.service.PlanetSignService;
import kz.zvezdochet.analytics.service.PlanetTextService;
import kz.zvezdochet.analytics.service.RuleService;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.CardKind;
import kz.zvezdochet.bean.Cross;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.Halfsphere;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Pheno;
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
import kz.zvezdochet.service.CardKindService;
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
	 * @param term признак использования астрологических терминов
	 */
	public void generate(Event event, boolean term) {
		child = event.isChild();
		female = event.isFemale();
		this.term = term;
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
			PDFUtil.printHeader(p, "Индивидуальный гороскоп", null);
			chapter.add(p);

			String text = event.getCallname() + " - ";
			text += DateUtil.fulldtf.format(event.getBirth());
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

			Font fontgray = PDFUtil.getAnnotationFont(false);
			text = "Дата составления: " + DateUtil.fulldtf.format(event.getDate());
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
				+ "Судьба и так сделает всё, чтобы помочь вам закалить ваш характер", font));

			//космограмма
			printCard(doc, chapter);
			chapter.add(Chunk.NEXTPAGE);

			//лунный день
			printLunar(chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			EventStatistics statistics = new EventStatistics(event.getConfiguration());
			Map<String, Double> signMap = statistics.getPlanetSigns(true);

			//градус рождения
			boolean undefined = (3 == event.getRectification());
			if (!child && !undefined) {
				printDegree(chapter, event);
				chapter.add(Chunk.NEXTPAGE);
			}

			//знаки
			printSigns(writer, chapter, signMap);
			chapter.add(Chunk.NEXTPAGE);

			//дома
			statistics.initPlanetHouses();
			printHouses(writer, chapter, statistics);
			chapter.add(Chunk.NEXTPAGE);

			//счастливые символы
			printSymbols(chapter, event);
			doc.add(chapter);


			chapter = new ChapterAutoNumber(PDFUtil.printHeader(new Paragraph(), "Общий типаж", null));
			chapter.setNumberDepth(0);
			chapter.add(new Paragraph("Общий типаж – это общая характеристика поколения людей, рождённых вблизи " + DateUtil.sdf.format(event.getBirth()), PDFUtil.getWarningFont()));

			//планеты в знаках
			printPlanetSign(chapter, event);
			doc.add(chapter);


			chapter = new ChapterAutoNumber(PDFUtil.printHeader(new Paragraph(), "Анализ карты рождения", null));
			chapter.setNumberDepth(0);

			chapter.add(new Paragraph("В разделе «Общий типаж» дана обобщённая характеристика вашей личности. "
				+ "Теперь речь пойдёт о ваших собственных наработках и качествах: как вы в реальности ведёте себя в жизненных ситуациях, "
				+ "и чем отличаетесь от себе подобных", PDFUtil.getSuccessFont()));

			//вид космограммы
			if (!undefined) {
				printCardKind(chapter, event);
				chapter.add(Chunk.NEXTPAGE);
			}

			//тип космограммы
			printCardType(chapter, event);

			//планеты
			printPlanetStrong(chapter, event);
			printPlanetWeak(chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//аспекты
			p = new Paragraph();
			PDFUtil.printHeader(p, "Аспекты планет", null);
			chapter.add(p);

			p = new Paragraph("Если в дальнейшем толковании упомянуты люди, которых нет в живых, "
				+ "или вы никогда их не видели (родители, родственники, партнёры), "
				+ "значит речь идёт о людях, их заменяющих или похожих на них по характеру", font);
			p.setSpacingAfter(10);
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
			if (!undefined)
				printConfigurations(chapter, event);

			doc.add(chapter);

			chapter = new ChapterAutoNumber(PDFUtil.printHeader(new Paragraph(), "Реализация личности", "planethouses"));
			chapter.setNumberDepth(0);

			//планеты в домах
			printPlanetHouses(chapter, event);
			doc.add(chapter);

			
			chapter = new ChapterAutoNumber(PDFUtil.printHeader(new Paragraph(), "Диаграммы", null));
			chapter.setNumberDepth(0);

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

			//лояльность
			printLoyalty(writer, chapter, event);
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
			chapter.add(Chunk.NEXTPAGE);

			//знаменитости
			printSimilar(chapter, event);
			doc.add(chapter);

			chapter = new ChapterAutoNumber(PDFUtil.printHeader(new Paragraph(), "Сокращения", null));
			chapter.setNumberDepth(0);

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
	@SuppressWarnings("unused")
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
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printDegree(Chapter chapter, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, term ? "Градус рождения" : "Символ рождения");
			if (event.getConfiguration().getHouses() != null &&
					event.getConfiguration().getHouses().size() > 0) {
				House house = (House)event.getConfiguration().getHouses().get(0);
				if (null == house) return;
				int value = (int)house.getCoord();
				Model model = new DegreeService().find(new Long(String.valueOf(value)));
			    if (model != null) {
			    	Degree degree = (Degree)model;
			    	if (term)
			    		section.add(new Paragraph("Градус ASC: " + degree.getId() + "° управитель " + degree.getName() + ", " + degree.getCode(), fonth5));
			    	String star = degree.isRoyal() || degree.isOccult() ? "*" : "";
					section.add(new Paragraph(degree.getDescription() + star, PDFUtil.getAnnotationFont(true)));
					section.add(new Paragraph(PDFUtil.removeTags(degree.getText(), font)));
					PDFUtil.printGender(section, degree, female, child, true);

					if (degree.isRoyal()) {
						Paragraph p = new Paragraph("*Королевский градус означает испытание через получение позитивных возможностей (обретение жизненного опыта через успех). "
							+ "Девиз такого градуса: «Надежда умирает последней», а значит вы получите помощь в сложный период жизни, благодаря чему вновь обретёте удачу", font);
						p.setSpacingAfter(10);
						section.add(p);
					}
					if (degree.isOccult()) {
						Paragraph p = new Paragraph("*Оккультный градус указывает на посвящённого человека, которому доступен опыт взаимодействия со скрытыми и неизвестными науке энергиями и явлениями", font);
						p.setSpacingAfter(10);
						section.add(p);
					}

					URL url = PlatformUtil.getPath(Activator.PLUGIN_ID, "/icons/degree/" + model.getId() + ".jpg");
					if (url != null) {
						String filename = url.getPath();
						com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(filename);
						section.add(image);
					}
					if (degree.isPositive()) {
						Paragraph p = new Paragraph("Подобную иллюстрацию можно нарисовать и повесить в месте вашего вдохновения", font);
						p.setSpacingBefore(10);
						section.add(p);
					}
			    }
			}
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
			section.add(new Paragraph("Сокращения и символы, использованные в тексте, описаны в конце документа", PDFUtil.getAnnotationFont(false)));
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
//				String[] deals = {"personality", "emotions", "thinking", "contact", "work", "activity"};

				for (Model model : event.getConfiguration().getPlanets()) {
					Planet planet = (Planet)model;
				    if (planet.isMain()) {
				    	List<PlanetSignText> list = service.find(planet, planet.getSign());
				    	if (list != null && list.size() > 0)
				    		for (PlanetSignText object : list) {
				    			Category category = object.getCategory();
				    			if (category.getCode().equals("childhood") && !child)
				    				continue;
//				    			if (doctype.equals("deal") && !Arrays.asList(deals).contains(category.getCode()))
//				    				continue;

				    			Section section = PDFUtil.printSection(chapter, category.getName());
				    			if (term) {
				    				section.add(new Chunk(planet.getMark("sign"), fonth5));
				    				section.add(new Chunk(planet.getSymbol(), PDFUtil.getHeaderAstroFont()));
				    				section.add(new Chunk(" " + planet.getName() + " в созвездии " + planet.getSign().getName() + " ", fonth5));
				    				section.add(new Chunk(planet.getSign().getSymbol(), PDFUtil.getHeaderAstroFont()));
				    				section.add(Chunk.NEWLINE);
				    			}
				    			Paragraph p = category.getCode().equals("profession")
				    				? new Paragraph(PDFUtil.html2pdf(object.getText(), font))
				    				: new Paragraph(PDFUtil.removeTags(object.getText(), font));
				    			section.add(p);
				    			PDFUtil.printGender(section, object, female, child, true);

								Rule rule = EventRules.rulePlanetSign(planet, planet.getSign(), event);
								if (rule != null) {
									section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
									section.add(Chunk.NEWLINE);
								}
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
			float fontsize = 9;
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
	 * TODO фиктивные планеты при определении вида космограмм не считаются?
	 */
	private void printCardKind(Chapter chapter, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Кармический потенциал");
			section.add(new Paragraph("Рисунок космограммы показывает общую картину, "
				+ "которая не в деталях, а глобально описывает ваше предназначение и кармический опыт прошлого. "
				+ "Определите, на каком уровне вы находитесь. Отследите по трём уровням своё развитие (см. ниже)", font));

			long id = event.getCardkindid();
			if (0 == id) return;
			CardKind kind = (CardKind)new CardKindService().find(id);
			Paragraph p = new Paragraph(kind.getName(), fonth5);
			section.add(p);

			com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(PlatformUtil.getPath(
					Activator.PLUGIN_ID, "/icons/kind/" + kind.getCode() + ".png").getPath());
			section.add(image);

			if (term) {
				p = new Paragraph(kind.getDescription(), PDFUtil.getAnnotationFont(true));
				p.setSpacingAfter(10);
				section.add(p);
			}
			section.add(new Paragraph(PDFUtil.html2pdf(kind.getText(), font)));
			Font bold = new Font(baseFont, 12, Font.BOLD);

//----------тигр

			if (1 == id) {
				PlanetTextService service = new PlanetTextService();
				PlanetText planetText = (PlanetText)service.findByPlanet(23L, "positive");
				if (planetText != null) {
					section.add(new Paragraph("1) " + planetText.getPlanet().getPositive() + ":", bold));
					section.add(new Paragraph(PDFUtil.html2pdf(planetText.getText(), font)));
				}
				planetText = (PlanetText)service.findByPlanet(22L, "positive");
				if (planetText != null) {
					section.add(new Paragraph("2) " + planetText.getPlanet().getPositive() + ":", bold));
					section.add(new Paragraph(PDFUtil.html2pdf(planetText.getText(), font)));
				}

//----------праща

			} else if (8 == id) {
				Long pids[] = {20L, 29L, 28L, 30L, 22L};
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
						section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
				}
				//Если планета-снаряд смещена вправо
				boolean right = true;
				if (right) {
					section.add(Chunk.NEWLINE);
					RuleService rservice = new RuleService();
					Rule rule = (Rule)rservice.find(86L);
					if (rule != null)
						section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
				}

//----------Колыбель Ньютона

			} else if (13 == id) {
				PlanetTextService service = new PlanetTextService();
				PlanetText planetText = (PlanetText)service.findByPlanet(26L, "positive");
				if (planetText != null) {
					section.add(new Paragraph(planetText.getPlanet().getShortName(), bold));
					section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));
				}
				planetText = (PlanetText)service.findByPlanet(21L, "positive");
				if (planetText != null) {
					section.add(new Paragraph(planetText.getPlanet().getShortName(), bold));
					section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));
				}

//----------лук

			} else if (6 == id) { //лук
				section.add(Chunk.NEWLINE);
				Long pids[] = {20L, 21L};
				PlanetService service = new PlanetService();
				com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
				list.setNumbered(true);
				for (Long pid : pids) {
					Planet planet = (Planet)service.find(pid);
					if (planet != null) {
						ListItem li = new ListItem();
						String s = planet.getDescription();
						if (term)
							s += " (" + planet.getName() + ")";
				        li.add(new Chunk(s, font));
				        list.add(li);
					}
				}
				section.add(list);

				kind.setDirection("down");
				Rule rule = EventRules.ruleCardKind(kind);
				if (rule != null) {
					p = new Paragraph(PDFUtil.removeTags(rule.getText(), font));
					p.setSpacingBefore(10);
					section.add(p);
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
	 */
	private void printCardType(Chapter chapter, Event event) {
		try {
			if (event.getConfiguration().getPlanets() != null) {
				EventStatistics stat = new EventStatistics(event.getConfiguration());
				stat.getPlanetSigns(false);
				Map<String, Integer> signMap = stat.getSignPlanets();

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
							section.add(new Paragraph(cardType.getDescription(), PDFUtil.getAnnotationFont(true)));
						}
						section.add(new Paragraph(PDFUtil.removeTags(cardType.getText(), font)));
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
						section.addSection(new Paragraph((term ? planet.getName() : planet.getShortName()) + "-меч", fonth5));
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));

						Rule rule = EventRules.rulePlanetSword(planet, female);
						if (rule != null)
							section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));

						PDFUtil.printGender(section, planetText, female, child, true);
					}
				} else if (planet.isShield()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "shield");
					if (planetText != null) {
						section.addSection(new Paragraph((term ? planet.getName() : planet.getShortName()) + "-щит", fonth5));
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));
						PDFUtil.printGender(section, planetText, female, child, true);
					}
				}
				if (planet.isPerfect() && !planet.isBroken()) {
					if (planet.inMine())
						section.add(new Paragraph("Планета " + planet.getName() + " не вызывает напряжения, так что вы сумеете проработать недостатки, описанные в разделе «" + planet.getShortName() + " в шахте»", fonth5));
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "perfect");
					if (planetText != null) {
						section.addSection(new Paragraph((term ? planet.getName() : planet.getShortName()) + "-гармония", fonth5));
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));
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
			List<Planet> weaks = new ArrayList<>();
			List<Model> planets = event.getConfiguration().getPlanets();
			for (Model model : planets) {
				Planet planet = (Planet)model;
				if (planet.inMine()
						|| (planet.isDamaged() && !planet.isBroken()))
					weaks.add(planet);
			}
			if (weaks.isEmpty())
				return;

			Section section = PDFUtil.printSection(chapter, "Слабые стороны");
			PlanetTextService service = new PlanetTextService();
			for (Planet planet : weaks) {
				PlanetText planetText = null;

				if (planet.inMine()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "mine");
					if (planetText != null) {
						section.addSection(new Paragraph((term ? planet.getName() : planet.getShortName()) + " в шахте", fonth5));
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));

						PlanetService planetService = new PlanetService();
						Planet ruler = planetService.getRuler(planet.getSign(), true);
						if (ruler != null) {
							PlanetText text = (PlanetText)service.findByPlanet(ruler.getId(), "positive");
							if (text != null) {
								Paragraph p = new Paragraph("В этом вам помогут следующие качества и сферы жизни:", new Font(baseFont, 12, Font.BOLD));
								p.setSpacingBefore(10);
								section.add(p);
								section.add(new Paragraph(PDFUtil.html2pdf(text.getText(), font)));
							}
						}
						PDFUtil.printGender(section, planetText, female, child, true);
					}
				} else if (planet.isDamaged() && !planet.isBroken()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "damaged");
					if (planetText != null) {
						section.addSection(new Paragraph((term ? planet.getName() + " без позитивных аспектов" : planet.getShortName()) + "-дисгармония", fonth5));
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));
						PDFUtil.printGender(section, planetText, female, child, true);
					}
				}
				if (planet.isRetrograde()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "retro");
					if (planetText != null) {
						section.addSection(new Paragraph((term ? planet.getName() : planet.getShortName()) + "-ретроград", fonth5));
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));
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
				"NEGATIVE", "NEGATIVE_HIDDEN", "POSITIVE", "POSITIVE_HIDDEN", "CREATIVE", "KARMIC", "SPIRITUAL", "PROGRESSIVE"
			};

			List<Bar> items = new ArrayList<Bar>();
		    for (Model tmodel : types) {
		    	AspectType mtype = null; 
		    	AspectType type = (AspectType)tmodel;
		    	if (Arrays.asList(codes).contains(type.getCode())) {
		    		mtype = type;
		    	} else {
		    		AspectType ptype = type.getParentType();
		    		if (ptype != null && Arrays.asList(codes).contains(ptype.getCode()))
		    			mtype = type.getParentType();
		    	}
		    	if (null == mtype)
		    		continue;

		    	int value = 0;
		    	for (Model model : planets) {
		    		Planet planet = (Planet)model;
					value += planet.getAspectCountMap().get(type.getCode());
		    	}
		    	if (0 == value)
		    		continue;

		    	boolean exists = false;
		    	String name = term ? mtype.getName() : mtype.getKeyword();
		    	for (Bar b : items) {
		    		if (b.getName().equals(name)) {
		    			exists = true;
				    	b.setValue(b.getValue() + value);
		    			break;
		    		}
		    	}
		    	if (!exists) {
			    	Bar bar = new Bar();
			    	bar.setName(name);
			    	bar.setValue(value);
					bar.setColor(mtype.getColor());
					bar.setCategory("Аспекты");
					items.add(bar);
		    	}
		    }
		    int size = items.size();
		    Bar[] bars = new Bar[size];
		    for (int i = 0; i < size; i++)
		    	bars[i] = items.get(i);

		    com.itextpdf.text.Image image = PDFUtil.printBars(writer, "Соотношение аспектов планет", "Аспекты", "Баллы", bars, 500, 300, false, false, true);
			Section section = PDFUtil.printSection(chapter, "Соотношение аспектов планет");

			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("Если переживаний больше, чем стресса, значит вам нужно искать разрядку своим негативным эмоциям, рассказывать о своих проблемах людям, которым вы доверяете. Не держите обиды и напряжение в себе", font));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Если скрытого позитива больше, чем лёгкости, значит вам нужно выражать больше эмоций, не сдерживать радость, делиться своими успехами с любимыми и интересными вам людьми", new Font(baseFont, 12, Font.NORMAL, BaseColor.RED)));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Если воздаяния за ошибки больше, чем стресса, значит нужно обратить взгляд в прошлое и найти причины, которые привели к текущим последствиям. Скорей всего они кроются в вашем прошлом поведении и мышлении", new Font(baseFont, 12, Font.NORMAL, BaseColor.BLUE)));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Чем больше духовности, тем более высокого уровня развития вы достигли в метафизическом плане (больше одного – уже хорошо)", new Font(baseFont, 12, Font.NORMAL, BaseColor.MAGENTA)));
	        list.add(li);

			section.add(list);
			section.add(image);
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
				Paragraph p = new Paragraph("В данном разделе описаны качества вашей личности, которые проявятся в критические моменты жизни:", PDFUtil.getDangerFont());
				p.setSpacingAfter(10);
				section.add(p);
			}
			PlanetAspectService service = new PlanetAspectService();
			Configuration conf = event.getConfiguration();
			List<SkyPointAspect> aspects = conf.getAspects();
			boolean exists = false;

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
				if (aspect.getAspect().getCode().equals("OPPOSITION")
						&& (planet2.getCode().equals("Kethu")
							|| planet2.getCode().equals("Rakhu")))
					continue;

				AspectType type = aspect.checkType(true);
				boolean match = false;
				String tcode = type.getCode();
				//аспект соответствует заявленному (негативному или позитивному)
				if (tcode.equals(aspectType))
					match = true;
				//в позитивные добавляем соединения и ядро Солнца
				else if	(aspectType.equals("POSITIVE") &&
						(tcode.equals("NEUTRAL_KERNEL") || tcode.equals("NEUTRAL")))
					match = true;
				//в негативные добавляем пояс Солнца
				else if (aspectType.equals("NEGATIVE") &&
						type.getCode().equals("NEGATIVE_BELT"))
					match = true;

				if (match) {
					List<Model> dicts = service.finds(aspect);
					for (Model model : dicts) {
						PlanetAspectText dict = (PlanetAspectText)model;
						if (dict != null) {
							exists = true;
		    				List<Model> planets = conf.getPlanets();
		    				int pindex = planets.indexOf(planet1);
		    				Planet aspl1 = (Planet)planets.get(pindex);
		    				pindex = planets.indexOf(planet2);
		    				Planet aspl2 = (Planet)planets.get(pindex);

		    				Paragraph p = new Paragraph("", fonth5);
		    				p.add(new Chunk(dict.getMark(aspl1, aspl2), fonth5));
		    				if (term)
								p.add(new Chunk(dict.getPlanet1().getName() + " " + 
									type.getSymbol() + " " + 
									dict.getPlanet2().getName(), fonth5));
		    				else
								p.add(new Chunk(dict.getPlanet1().getShortName() + " " + 
									type.getSymbol() + " " + 
									dict.getPlanet2().getShortName(), fonth5));

							if (term) {
								p.add(new Chunk(" " + planet1.getSymbol(), PDFUtil.getHeaderAstroFont()));
	
			    				if (aspect.getAspect().getCode().equals("CONJUNCTION") || aspect.getAspect().getCode().equals("OPPOSITION"))
			    					p.add(new Chunk(aspect.getAspect().getSymbol(), PDFUtil.getHeaderAstroFont()));
			    				else
			    					p.add(new Chunk(type.getSymbol(), fonth5));
	
			    				p.add(new Chunk(planet2.getSymbol(), PDFUtil.getHeaderAstroFont()));

								String pretext = aspect.getAspect().getCode().equals("CONJUNCTION")
										? "с планетой"
										: "к планете";
				    				p.add(new Paragraph(aspect.getAspect().getName() + " планеты " + dict.getPlanet1().getName() + " " + pretext + " " + dict.getPlanet2().getName(), PDFUtil.getAnnotationFont(true)));
							}
		    				section.addSection(p);
							section.add(new Paragraph(PDFUtil.removeTags(dict.getText(), font)));

							Rule rule = EventRules.rulePlanetAspect(aspect, female);
							if (rule != null) {
			    				section.add(Chunk.NEWLINE);
								section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
							}
							PDFUtil.printGender(section, dict, female, child, true);
						}
					}
				}
			}
			if (!exists && aspectType.equals("NEGATIVE")) {
				Paragraph p = new Paragraph("В вашем гороскопе нет резко негативных аспектов, влияющих на вашу личность. "
					+ "Это означает отсутствие внутренних конфликтов и наличие хорошего окружения (как у Христа за пазухой). "
					+ "Точки роста и напряжения будут формироваться не внутри вас, а посредством внешних обстоятельств, "
						+ "в процессе активных действий и развития всего вашего поколения. "
						+ "Они описаны в разделе «Фигуры гороскопа»", PDFUtil.getSuccessFont());
				p.setSpacingAfter(10);
				section.add(p);
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
		    List<AspectConfiguration> confs = new ArrayList<>();
		    AspectConfigurationService service = new AspectConfigurationService();
			AspectConfiguration conf = null, conf2 = null;
			PlanetService planetService = new PlanetService();
			//для однотипных конфигураций headable = false

//----------стеллиум 0° 0° 0° 0°

//		    conf = (AspectConfiguration)service.find("stellium");
//			conf.setHeadable(true);
//		    Map<String, Integer> signMap = new HashMap<String, Integer>();
//		    for (Model model2 : event.getConfiguration().getPlanets()) {
//		    	Planet planet = (Planet)model2;
//		    	if (!planet.isMain())
//		    		continue;
//		    	Object object = signMap.get(planet.getSign().getCode());
//		    	int	value = object != null ? (Integer)object : 0;
//		    	signMap.put(planet.getSign().getCode(), ++value);
//		    }
//		    Iterator<Map.Entry<String, Integer>> iterator = signMap.entrySet().iterator();
//		    SignService signService = new SignService();
//		    while (iterator.hasNext()) {
//		    	Entry<String, Integer> entry = iterator.next();
//		    	double val = entry.getValue();
//		    	if (val > 3) {
//		    		Sign sign = (Sign)signService.find(entry.getKey());
//		    		printConf(event, section, conf, sign);
//		    		break;
//		    	}
//		    }

//----------ожерелье 1° 1° 1°

//----------бисикстиль 60° 60° 120°

//		    conf = (AspectConfiguration)service.find("bisextile");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(24L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(20L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(25L) });
//			confs.add(conf);

//		    conf2 = (AspectConfiguration)service.find("bisextile");
//			conf2.setHeadable(false);
//			conf2.setVertex(new Planet[] { (Planet)planetService.find(25L) });
//			conf2.setLeftFoot(new Planet[] { (Planet)planetService.find(23L) });
//			conf2.setRightFoot(new Planet[] { (Planet)planetService.find(31L) });
//			confs.add(conf2);

//----------вилы 150° 150° 60°

//		    conf = (AspectConfiguration)service.find("pitchfork");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(25L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(20L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(26L) });
//			confs.add(conf);
//
//		    conf2 = (AspectConfiguration)service.find("pitchfork");
//			conf2.setHeadable(false);
//			conf2.setVertex(new Planet[] { (Planet)planetService.find(20L) });
//			conf2.setLeftFoot(new Planet[] { (Planet)planetService.find(32L), (Planet)planetService.find(31L) });
//			conf2.setRightFoot(new Planet[] { (Planet)planetService.find(25L) });
//			confs.add(conf2);

//----------перспектива 60° 30° 60° 150°

//		    conf = (AspectConfiguration)service.find("perspective");
//			conf.setHeadable(true);
//			conf.setLeftHand(new Planet[] { (Planet)planetService.find(32L) });
//			conf.setRightHand(new Planet[] { (Planet)planetService.find(24L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(33L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(20L) });
//			confs.add(conf);			

//----------трапеция 60° 60° 60° 180°
			
		    conf = (AspectConfiguration)service.find("trapezoid");
			conf.setHeadable(true);
			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(30L) });
			conf.setLeftHand(new Planet[] { (Planet)planetService.find(28L) });
			conf.setRightHand(new Planet[] { (Planet)planetService.find(29L) });
			conf.setRightFoot(new Planet[] { (Planet)planetService.find(34L) });
			confs.add(conf);

//----------парус 120° 120° 60° 60°
			
		    conf = (AspectConfiguration)service.find("sail");
			conf.setHeadable(true);
			conf.setVertex(new Planet[] { (Planet)planetService.find(22L) });
			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(20L) });
			conf.setRightFoot(new Planet[] { (Planet)planetService.find(25L) });
			conf.setBase(new Planet[] { (Planet)planetService.find(21L) });
			confs.add(conf);

//----------тригон 120° 120° 120°

		    conf = (AspectConfiguration)service.find("triangle");
			conf.setHeadable(true);
			conf.setVertex(new Planet[] { (Planet)planetService.find(19L), (Planet)planetService.find(20L), (Planet)planetService.find(32L), (Planet)planetService.find(24L) });
			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(25L) });
			conf.setRightFoot(new Planet[] { (Planet)planetService.find(22L) });
			confs.add(conf);

//----------бумеранг 150° 150° 30° 30°

//		    conf = (AspectConfiguration)service.find("boomerang");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			conf.setBase(new Planet[] { (Planet)planetService.find(34L) });
//			confs.add(conf);

//----------повозка 60° 120° 60° 120°

//		    conf = (AspectConfiguration)service.find("vehicle");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			conf.setBase(new Planet[] { (Planet)planetService.find(34L) });
//			confs.add(conf);

//----------крыша 30° 60° 30°

		    conf = (AspectConfiguration)service.find("roof");
			conf.setHeadable(true);
			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(21L) });
			conf.setVertex(new Planet[] { (Planet)planetService.find(31L) });
			conf.setRightFoot(new Planet[] { (Planet)planetService.find(20L) });
			confs.add(conf);

//----------ограда 30° 150° 30° 150°

//		    conf = (AspectConfiguration)service.find("railing");
//			conf.setHeadable(true);
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(21L) });
//			conf.setLeftHand(new Planet[] { (Planet)planetService.find(34L), (Planet)planetService.find(31L) });
//			conf.setRightHand(new Planet[] { (Planet)planetService.find(28L), (Planet)planetService.find(30L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(22L), (Planet)planetService.find(25L) });
//			confs.add(conf);

//----------громоотвод 90° 60° 60° 90° 60°

//		    conf = (AspectConfiguration)service.find("arrester");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(33L) });
//			conf.setLeftHand(new Planet[] { (Planet)planetService.find(30L) });
//			conf.setRightHand(new Planet[] { (Planet)planetService.find(20L), (Planet)planetService.find(31L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(27L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(22L) });
//			confs.add(conf);			

//----------звезда Давида 60° 60° 60° 60° 60° 60°

//		    conf = (AspectConfiguration)service.find("davidstar");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			conf.setBase(new Planet[] { (Planet)planetService.find(34L) });
//			conf.setLeftHand(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightHand(new Planet[] { (Planet)planetService.find(28L) });
//			confs.add(conf);

//----------птица 60° 120° 60° 120°

//		    conf = (AspectConfiguration)service.find("bird");
//			conf.setHeadable(true);
//			conf.setLeftHand(new Planet[] { (Planet)planetService.find(32L) });
//			conf.setRightHand(new Planet[] { (Planet)planetService.find(23L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(22L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(24L), (Planet)planetService.find(30L), (Planet)planetService.find(21L) });
//			confs.add(conf);			

//----------корабль 72° 72° 144°

//		    conf = (AspectConfiguration)service.find("ship");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			confs.add(conf);

//----------пальма 144° 144° 72°

//		    conf = (AspectConfiguration)service.find("palm");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(32L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(21L) });
//			confs.add(conf);

//----------конверт 108° 72° 108° 72°

//		    conf = (AspectConfiguration)service.find("envelope");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			conf.setBase(new Planet[] { (Planet)planetService.find(34L) });
//			confs.add(conf);

//----------лодка 36° 72° 36°

//		    conf = (AspectConfiguration)service.find("boat");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			confs.add(conf);

//----------ракета 144° 36° 36° 144°

//		    conf = (AspectConfiguration)service.find("rocket");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			conf.setBase(new Planet[] { (Planet)planetService.find(34L) });
//			confs.add(conf);

//----------венок 72° 72° 72° 72° 72°

//		    conf = (AspectConfiguration)service.find("wreath");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			conf.setLeftHand(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightHand(new Planet[] { (Planet)planetService.find(28L) });
//			confs.add(conf);

//----------пирамида 150° 72° 135°

//		    conf = (AspectConfiguration)service.find("pyramid");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			confs.add(conf);

//----------компас 108° 180° 72°

//		    conf = (AspectConfiguration)service.find("compass");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			confs.add(conf);

//----------крест 90° 90° 90° 90°

//		    conf = (AspectConfiguration)service.find("cross");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			conf.setBase(new Planet[] { (Planet)planetService.find(34L) });
//			confs.add(conf);

//----------тау-квадрат 90° 90° 180°

//		    conf = (AspectConfiguration)service.find("taucross");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(28L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(22L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(21L) });
//			confs.add(conf);

//		    conf2 = (AspectConfiguration)service.find("taucross");
//			conf2.setHeadable(false);
//			conf2.setVertex(new Planet[] { (Planet)planetService.find(30L) });
//			conf2.setLeftFoot(new Planet[] { (Planet)planetService.find(22L) });
//			conf2.setRightFoot(new Planet[] { (Planet)planetService.find(21L) });
//			confs.add(conf2);

//----------кинжал 135° 135° 45° 45°

//		    conf = (AspectConfiguration)service.find("dagger");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(21L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(20L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(30L) });
//			conf.setBase(new Planet[] { (Planet)planetService.find(22L) });
//			confs.add(conf);

//----------секира 135° 135° 90°

//		    conf = (AspectConfiguration)service.find("poleaxe");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(31L), (Planet)planetService.find(32L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(30L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(22L) });
//			confs.add(conf);

//		    conf2 = (AspectConfiguration)service.find("poleaxe");
//			conf2.setHeadable(false);
//			conf2.setVertex(new Planet[] { (Planet)planetService.find(25L) });
//			conf2.setLeftFoot(new Planet[] { (Planet)planetService.find(26L) });
//			conf2.setRightFoot(new Planet[] { (Planet)planetService.find(24L) });
//			confs.add(conf2);
			
//----------дротик 45° 45° 90°

//		    conf = (AspectConfiguration)service.find("javelin");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(27L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(31L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(25L) });
//			confs.add(conf);

//----------барьер 45° 90° 45° 180°

//		    conf = (AspectConfiguration)service.find("barrier");
//			conf.setHeadable(true);
//			conf.setLeftHand(new Planet[] { (Planet)planetService.find(28L) });
//			conf.setRightHand(new Planet[] { (Planet)planetService.find(32L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(20L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(26L) });
//			confs.add(conf);			

//----------изолятор 135° 45° 135° 45°

//		    conf = (AspectConfiguration)service.find("isolator");
//			conf.setHeadable(true);
//			conf.setLeftHand(new Planet[] { (Planet)planetService.find(21L) });
//			conf.setRightHand(new Planet[] { (Planet)planetService.find(30L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(32L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(22L) });
//			confs.add(conf);

//----------дымоход 90° 45° 90° 135°

//		    conf = (AspectConfiguration)service.find("chimney");
//			conf.setHeadable(true);
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(28L) });
//			conf.setLeftHand(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightHand(new Planet[] { (Planet)planetService.find(24L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(32L) });
//			confs.add(conf);

//----------призрак 45° 135° 45° 135°

//		    conf = (AspectConfiguration)service.find("ghost");
//			conf.setHeadable(true);
//			conf.setLeftHand(new Planet[] { (Planet)planetService.find(25L) });
//			conf.setRightHand(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(31L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(24L) });
//			confs.add(conf);			

//----------заноза 45° 90° 135°

//		    conf = (AspectConfiguration)service.find("splinter");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(32L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(20L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(19L) });
//			confs.add(conf);			

//----------клетка 40° 40° 40° 40° 40° 40° 40° 40° 40°

//----------ящик 20° 40° 100° 40°

//		    conf = (AspectConfiguration)service.find("box");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			conf.setBase(new Planet[] { (Planet)planetService.find(34L) });
//			confs.add(conf);

//----------замок 20° 20° 40°

//		    conf = (AspectConfiguration)service.find("lock");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(21L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(24L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			confs.add(conf);

//----------аркан 40° 80° 40°

//		    conf = (AspectConfiguration)service.find("lasso");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			confs.add(conf);

//----------носилки 100° 80° 100° 80°

//		    conf = (AspectConfiguration)service.find("stretcher");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			conf.setBase(new Planet[] { (Planet)planetService.find(34L) });
//			confs.add(conf);

//----------двойной аркан 100° 100° 160°

//		    conf = (AspectConfiguration)service.find("bilasso");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(26L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(21L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(24L) });
//			confs.add(conf);

//			conf2 = (AspectConfiguration)service.find("bilasso");
//			conf2.setHeadable(false);
//			conf2.setVertex(new Planet[] { (Planet)planetService.find(24L) });
//			conf2.setLeftFoot(new Planet[] { (Planet)planetService.find(27L) });
//			conf2.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			confs.add(conf2);

//----------тоннель 160° 160° 40°

//		    conf = (AspectConfiguration)service.find("tunnel");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(27L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(32L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			confs.add(conf);

//----------плетёнка 80° 100° 160° 100° 80° 160°

		    conf = (AspectConfiguration)service.find("twist");
			conf.setHeadable(true);
			conf.setLeftHorn(new Planet[] { (Planet)planetService.find(28L) });
			conf.setRightHorn(new Planet[] { (Planet)planetService.find(25L) });
			conf.setLeftHand(new Planet[] { (Planet)planetService.find(30L) });
			conf.setRightHand(new Planet[] { (Planet)planetService.find(21L) });
			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(22L) });
			conf.setRightFoot(new Planet[] { (Planet)planetService.find(34L) });
			confs.add(conf);

//----------таран 130.55° 65.27° 65.27°

//		    conf = (AspectConfiguration)service.find("ram");
//			conf.setHeadable(true);
//			conf.setVertex(new Planet[] { (Planet)planetService.find(19L) });
//			conf.setLeftFoot(new Planet[] { (Planet)planetService.find(29L) });
//			conf.setRightFoot(new Planet[] { (Planet)planetService.find(28L) });
//			confs.add(conf);

//----------полуповозка 60° 180° 120°

//		    conf = (AspectConfiguration)service.find("semivehicle");
//			conf.setHeadable(true);
//		    com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
//		    ListItem li = null;
//
//			//планеты
////			PlanetTextService service = new PlanetTextService();
////			PlanetText text = null;
////			section.add(new Paragraph("Личностные изменения:", bold));
////			text = (PlanetText)service.findByPlanet(21L, "stage");
////			if (text != null) {
////			    li = new ListItem();
////				li.add(new Chunk(printStage(24, text.getText()), font));
////				list.add(li);
////			}
////			section.add(list);
//
//		    //дома
//			HouseService houseService = new HouseService();
//			House housetext = null;
//		    section.add(new Paragraph("Важные вехи жизни:", bold));
//		    list = new com.itextpdf.text.List(false, false, 10);
//		    housetext = (House)houseService.find(154L);
//		    if (housetext != null && housetext.getStage() != null) {
//		    	li = new ListItem();
//		    	li.add(new Chunk(printStage(7, housetext.getStage()), font));
//		    	list.add(li);
//		    }
//		    section.add(list);
//		    confs.add(conf);
			
//----------|

			if (confs.size() > 0) {
				Section section = PDFUtil.printSection(chapter, "Фигуры гороскопа");
			    Paragraph p = new Paragraph("Рисунок вашего гороскопа состоит из геометрических фигур, "
			    	+ "которые отражают взаимосвязи планет между собой. У всех людей они разные. "
			    	+ "Каждая фигура обобщает ваши сильные и слабые стороны, показывает главные источники роста и напряжения.", font);
			    p.setSpacingAfter(10);
			    section.add(p);

				for (AspectConfiguration configuration : confs)
					printConf(event, section, configuration, null);
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
			section.add(new Paragraph("Сферы жизни отражают ваши врождённые возможности, багаж и опыт, с которым вы пришли в этот мир. "
				+ "Пригодится он вам или нет – покажет время. "
				+ "В любом случае, это отправная точка корабля событий, на котором вы поплывёте по морю жизни и реализуете свою миссию.", font));
			section.add(Chunk.NEWLINE);
			section.add(new Paragraph("Приведённые здесь сферы жизни важны для вас от рождения. "
				+ "Даже если в будущем приоритеты поменяются, указанные в диаграмме факторы сохранят свою силу. "
				+ "Чем длиннее столбик, тем больше мыслей и событий с ним будет связано:", font));

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
		    com.itextpdf.text.Image image = PDFUtil.printBars(writer, "", "Сферы жизни", "Баллы", bars, 500, 500, false, false, false);
			section.add(image);

			Anchor anchor = new Anchor("Реализация личности", fonta);
            anchor.setReference("#planethouses");
			Paragraph p = new Paragraph();
			p.add(new Chunk("Более подробно эти сферы описаны в разделе ", font));
	        p.add(anchor);
			section.add(p);
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

				//Определяем количество планет в доме
				List<Planet> planets = new ArrayList<Planet>();
				for (Model pmodel : cplanets) {
					Planet planet = (Planet)pmodel;
					if (planet.getHouse().getId().equals(house.getId()))
						planets.add(planet);
				}
				//Создаем информационный блок, только если дом не пуст
				Section section = null;
				if (planets.size() > 0) {
					section = PDFUtil.printSection(chapter, house.getName());
			
					for (Planet planet : planets) {
						if (planet.getCode().equals("Lilith") && house.isSelened())
							continue;

						boolean negative = planet.isDamaged()
								|| planet.isLilithed()
								|| (planet.getCode().equals("Lilith")
									&& !planet.isLord() && !planet.isPerfect());
						String sign = negative ? "-" : "+";

						p = new Paragraph("", fonth5);
						String mark = planet.getMark("house");
						if (mark.length() > 0) {
		    				p.add(new Chunk(mark, fonth5));
		    				p.add(new Chunk(planet.getSymbol() + " ", PDFUtil.getHeaderAstroFont()));
						}

		    			if (term) {
		    				p.add(new Chunk(" " + planet.getName() + " в " + house.getDesignation() + " доме", fonth5));
		    				p.add(Chunk.NEWLINE);
		    			} else {
		    				String pname = negative ? planet.getNegative() : planet.getPositive();
		    				p.add(new Chunk(house.getName() + " " + sign + " " + pname, fonth5));
		    			}
		    			section.addSection(p);

						if (planet.getCode().equals("Selena") && house.isLilithed()) {
							Rule rule = EventRules.ruleMoonsHouse(house);
							if (rule != null) {
								section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
								section.add(Chunk.NEWLINE);
							}
						} else {
							PlanetHouseText dict = (PlanetHouseText)service.find(planet, house, null);
							if (dict != null) {
								section.add(new Paragraph(PDFUtil.removeTags(dict.getText(), font)));
								PDFUtil.printGender(section, dict, female, child, true);
	
								Rule rule = EventRules.rulePlanetHouse(planet, house, female);
								if (rule != null) {
									section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
									section.add(Chunk.NEWLINE);
								}
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
						section.addSection(new Paragraph(house.getDesignation() + " дом в созвездии " + sign.getName(), fonth5));
					else
						section.addSection(new Paragraph(house.getName() + " + " + sign.getShortname(), fonth5));

					if (160 == house.getId()) {
						section.add(new Paragraph("Судьба всегда даёт нам в качестве партнёра человека, который является нашей противоположностью и способен нас дополнять, учить новому и помогать исполнять нашу жизненную миссию. Поэтому вас ждёт встреча с человеком, образ которого описан ниже:", font));
						section.add(Chunk.NEWLINE);
					}
					p = house.getCode().equals("II_2")
						? new Paragraph(PDFUtil.html2pdf(dict.getText(), font))
						: new Paragraph(PDFUtil.removeTags(dict.getText(), font));
					section.add(p);
					PDFUtil.printGender(section, dict, female, child, true);

					Rule rule = EventRules.ruleHouseSign(house, sign, event);
					if (rule != null) {
						section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
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
		    	bar.setCategory("в сознании");
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
		    	section.add(new Paragraph(element.getTemperament(), fonth5));
		    	if (term)
		    		section.add(new Paragraph("Ниболее выражены стихии: " + element.getName(), PDFUtil.getAnnotationFont(true)));
		    	section.add(new Paragraph(PDFUtil.removeTags(element.getText(), font)));
		    	PDFUtil.printGender(section, element, female, child, true);
		    }

			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("Категория \"в сознании\" показывает вашу идеальную модель: "
					+ "на чём мысленно вы сконцентрированы, какие проявления для вас важны, необходимы и естественны.", font));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Категория \"в поступках\" показывает, "
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
		    	bar.setCategory("в поступках");
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
		    	bar.setCategory("в сознании");
		    	bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		yinyang = element;
		    	}
		    }
		    if (yinyang != null) {
		    	if (term) {
		    		section.add(new Paragraph(yinyang.getName(), fonth5));
		    		String s = yinyang.getCode().equals("Male") ? "мужские" : "женские";
		    		section.add(new Paragraph("Наиболее выражены " + s + " знаки: " + yinyang.getDescription(), PDFUtil.getAnnotationFont(true)));
		    	}
		    	section.add(new Paragraph(PDFUtil.removeTags(yinyang.getText(), font)));
		    	PDFUtil.printGender(section, yinyang, female, child, true);
		    }

			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("Категория \"в сознании\" показывает, насколько вы активны в мыслях и принятии решений наедине с самим собой.", font));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Категория \"в поступках\" показывает, как меняется ваша активность на событийном уровне, в социуме по сравнению с предыдущей моделью.", font));
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
		    	bar.setCategory("в поступках");
		    	bars[++i] = bar;
		    }
		    com.itextpdf.text.Image image = PDFUtil.printStackChart(writer, "Мужское (активное) и женское (пассивное) начало", "Аспекты", "Баллы", bars, 500, 150, true);
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
		    	bar.setCategory("в мыслях");
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
		    		section.add(new Paragraph(sphere.getDescription(), PDFUtil.getAnnotationFont(true)));
		    	section.add(new Paragraph(PDFUtil.removeTags(sphere.getText(), font)));
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
		    	bar.setCategory("в поступках");
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
		    	bar.setCategory("в сознании");
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
		    	bar.setCategory("в поступках");
		    	bars[++i] = bar;
		    }
		    if (square != null) {
		    	if (term) {
		    		section.add(new Paragraph(square.getName(), fonth5));
		    		section.add(new Paragraph("Наиболее выражен " + square.getDescription(), PDFUtil.getAnnotationFont(true)));
		    	}
		    	section.add(new Paragraph(PDFUtil.removeTags(square.getText(), font)));
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
		    	bar.setCategory("в сознании");
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
		    	bar.setCategory("в поступках");
		    	bars[++i] = bar;
		    }
		    if (cross != null) {
		    	if (term) {
		    		section.add(new Paragraph(cross.getName(), fonth5));
		    		section.add(new Paragraph("Ниболее выражены знаки: " + cross.getDescription(), PDFUtil.getAnnotationFont(true)));
		    	}
		    	section.add(new Paragraph(PDFUtil.removeTags(cross.getText(), font)));
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
		    	bar.setCategory("в сознании");
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
		    	bar.setCategory("в поступках");
		    	bars[++i] = bar;
		    }
		    if (zone != null) {
		    	if (term) {
		    		section.add(new Paragraph(zone.getName(), fonth5));
		    		section.add(new Paragraph("Наиболее выражены знаки " + zone.getDescription(), PDFUtil.getAnnotationFont(true)));
		    	}
		    	section.add(new Paragraph(PDFUtil.removeTags(zone.getText(), font)));
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
		    	bar.setName(term ? planet.getName() : planet.getPositive());
		    	bar.setValue(planet.getPoints());
				bar.setColor(planet.getColor());
				bar.setCategory("Планеты");
				bars[++i] = (bar);
		    }
		    com.itextpdf.text.Image image = PDFUtil.printBars(writer, "", "Планеты", "Баллы", bars, 500, 500, false, false, false);
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
	 * Генерация символов знака Солнца и др.
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printSymbols(Chapter chapter, Event event) {
		try {
			if (event.getConfiguration().getPlanets() != null) {
		    	Sign sign = null;
				Font bold = PDFUtil.getSubheaderFont();

				Section section = PDFUtil.printSection(chapter, "Ваше предназначение");
				com.itextpdf.text.List ilist = new com.itextpdf.text.List(false, false, 10);

				ListItem li = new ListItem();
				for (Model model : event.getConfiguration().getPlanets()) {
					Planet planet = (Planet)model;
				    if (planet.getCode().equals("Sun")) {
				    	sign = planet.getSign();

						li = new ListItem();
				        li.add(new Chunk("Предназначение Духа: ", bold));
				        li.add(new Chunk(sign.getSlogan(), font));
				        ilist.add(li);
				    } else if (planet.getCode().equals("Moon")) {
						li = new ListItem();
				        li.add(new Chunk("Предназначение Души: ", bold));
				        li.add(new Chunk(planet.getSign().getSlogan(), font));
				        ilist.add(li);
				    } else if (planet.getCode().equals("Mercury")) {
						li = new ListItem();
				        li.add(new Chunk("Предназначение Ума: ", bold));
				        li.add(new Chunk(planet.getSign().getSlogan(), font));
				        ilist.add(li);
				    } else if (planet.getCode().equals("Venus")) {
						li = new ListItem();
				        li.add(new Chunk("Предназначение Любви: ", bold));
				        li.add(new Chunk(planet.getSign().getSlogan(), font));
				        ilist.add(li);
				    } else if (planet.getCode().equals("Mars")) {
						li = new ListItem();
				        li.add(new Chunk("Предназначение Силы: ", bold));
				        li.add(new Chunk(planet.getSign().getSlogan(), font));
				        ilist.add(li);
				    }
				}
		        section.add(ilist);

		        if (sign != null) {
					section = PDFUtil.printSection(chapter, "Символы удачи");
					ilist = new com.itextpdf.text.List(false, false, 10);

					li = new ListItem();
			        li.add(new Chunk("Число рождения: ", bold));
			        int number = Numerology.getNumber(event.getBirth());
			        String text = String.valueOf(number);
			        Numerology numerology = (Numerology)new NumerologyService().find(number);
			        if (numerology != null && numerology.getBirth() != null)
			        	text += ". " + numerology.getBirth();
			        li.add(new Chunk(text, font));
			        ilist.add(li);

					li = new ListItem();
					li.add(new Chunk("Счастливое число: ", bold));
					text = sign.getNumbers() + ". Согласно Каббале, счастливые числа обладают особенной вибрацией, поэтому на них имеет смысл опираться при выборе даты, длительности или любого предмета, имеющего номер";
			        li.add(new Chunk(text, font));
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
	 * @param event событие
	 * @param section раздел
	 * @param conf конфигурация
	 * @param sign знак Зодиака
	 */
	private void printConf(Event event, Section section, AspectConfiguration conf, Sign sign) {
		try {
			String code = conf.getCode();
			String text = "";
			if (conf.isHeadable()) {
				section.addSection(new Paragraph(conf.getName(), fonth5));
				if (term)
    				section.add(new Paragraph("Конфигурация аспектов: " + conf.getDescription(), PDFUtil.getAnnotationFont(true)));
				text = conf.getText();
			}
			Paragraph shape = null;
			Paragraph appendix = null;
			Font bold = new Font(baseFont, 12, Font.BOLD);

			String[] triangle = new String[] {"ram", "lasso", "bilasso", "poleaxe", "taucross", "triangle", "pitchfork",
					"bisextile", "tunnel", "javelin", "roof", "lock", "ship", "boat", "palm", "pyramid", "compass", "splinter"};
			String[] rhombus = new String[] {"rocket", "boomerang", "sail", "dagger"};
			String[] tetragon = new String[] {"vehicle", "isolator", "cross", "trapezoid", "railing", "box", "stretcher",
					"envelope", "chimney", "perspective", "barrier", "bird", "ghost"};
			String[] pentagon = new String[] {"arrester", "wreath"};
			String[] hexagon =  new String[] {"twist"};

			if (code.equals("stellium")) {
				if (sign != null) {
					text = text.replace("{sign}", sign.getName());
					text = text.replace("{merit}", sign.getKeyword());
				}

			} else if (code.equals("necklace")) {
				com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(conf.getImageUrl());
				shape = new Paragraph();
				shape.add(image);

//========= треугольники

			} else if (Arrays.asList(triangle).contains(code)) {
				if (code.equals("triangle")) {
					kz.zvezdochet.bean.Element element = (kz.zvezdochet.bean.Element)new ElementService().find(2L); //заполнить вручную
					if (element != null)
						conf.setElement(element);
				}

				shape = printTriangle(conf);

				if (!code.equals("triangle")
						&& !code.equals("bilasso")
						&& !code.equals("pitchfork")
						&& !code.equals("splinter")) {
					appendix = new Paragraph();
					PlanetTextService ptservice = new PlanetTextService();
					for (Planet planet : conf.getVertex()) {
						PlanetText ptext = (PlanetText)ptservice.findByPlanet(planet.getId(), conf.isVertexPositive() ? "positive" : "negative");
						if (ptext != null) {
							String s = conf.isVertexPositive() ? planet.getPositive() : planet.getNegative();
							if (term)
								s += " (" + ptext.getPlanet().getName() + ")";
							appendix.add(new Paragraph(s, bold));
							appendix.add(new Paragraph(PDFUtil.html2pdf(ptext.getText(), font)));
						}
					}
				}

//----------тау-квадрат

				if (code.equals("taucross")) {
					Cross cross = (Cross)new CrossService().find(3L);
					if (cross != null) {
						String str = "Ваша реакция на удары судьбы";
						if (term)
							str += " (" + cross.getName() + ")";
						appendix.add(Chunk.NEWLINE);
						appendix.add(new Paragraph(str + ":", fonth5));
						appendix.add(new Paragraph(PDFUtil.removeTags(cross.getTau(), font)));
						appendix.add(Chunk.NEWLINE);
					}

//----------большой треугольник

				} else if (code.equals("triangle")) {
					appendix = new Paragraph();
					appendix.add(Chunk.NEWLINE);
					
					com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
					Planet[] tplanets = {conf.getVertex()[0], conf.getLeftFoot()[0], conf.getRightFoot()[0]};
					House[] houses = new House[3];
					List<Model> planets = event.getConfiguration().getPlanets();
					for (int i = 0; i < 3; i++) {
						 Planet tp = tplanets[i];
						for (Model m : planets) {
							Planet p = (Planet)m;
							if (tp.getCode().equals(p.getCode())) {
								houses[i] = p.getHouse(); break;
							}
						}
					}
					for (House h : houses) {
						if (h != null) {
							ListItem li = new ListItem();
							li.add(new Chunk(h.getDescription(), font));
							list.add(li);
						}
					}
					appendix.add(list);
					appendix.add(Chunk.NEWLINE);
					
					if (conf.getElement() != null) {
						if (term)
							appendix.add(new Paragraph(conf.getElement().getDescription(), fonth5));
						appendix.add(new Paragraph("Качества, благодаря которым вам обеспечена лёгкость и успех: ", bold));
						appendix.add(new Paragraph(conf.getElement().getTriangle(), font));
					}

//----------бисикстиль

				} else if (code.equals("bisextile")) {
					//если несколько бисикстилей
					if (!conf.isHeadable()) {
						RuleService ruleService = new RuleService();
						Rule rule =	(Rule)ruleService.find(38L);
						if (rule != null)
							section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
					}

//----------дротик

				} else if (code.equals("javelin")) {
					//если несколько дротиков
					if (!conf.isHeadable()) {
						Rule rule = (Rule)new RuleService().find(11L);
						section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
					}
				}

//========= ромбы

			} else if (Arrays.asList(rhombus).contains(code)) {
				shape = printRhombus(conf);

//========= трапеции

			} else if (Arrays.asList(tetragon).contains(code)) {
				shape = printTetragon(conf);

//----------крест

				if (code.equals("cross")) {
					Cross cross = (Cross)new CrossService().find(1L);
					if (cross != null) {
						appendix = new Paragraph();
						String str = "Ваша реакция на проблемные сферы";
						if (term)
							str += " (" + cross.getName() + ")";
						appendix.add(Chunk.NEWLINE);
						appendix.add(new Paragraph(str + ":", fonth5));
						appendix.add(new Paragraph(PDFUtil.removeTags(cross.getTau(), font)));
					}

//----------трапеция

				} else if (code.equals("trapezoid")) {
					appendix = new Paragraph();
					PlanetTextService ptservice = new PlanetTextService();
					for (Planet planet : conf.getLeftHand()) {
						PlanetText ptext = (PlanetText)ptservice.findByPlanet(planet.getId(), "positive");
						if (ptext != null) {
							String s = ptext.getPlanet().getPositive();
							if (term)
								s += " (" + ptext.getPlanet().getName() + ")";
							appendix.add(new Paragraph(s, bold));
							appendix.add(new Paragraph(PDFUtil.html2pdf(ptext.getText(), font)));
						}
					}
					for (Planet planet : conf.getRightHand()) {
						PlanetText ptext = (PlanetText)ptservice.findByPlanet(planet.getId(), "positive");
						if (ptext != null) {
							String s = ptext.getPlanet().getPositive();
							if (term)
								s += " (" + ptext.getPlanet().getName() + ")";
							appendix.add(new Paragraph(s, bold));
							appendix.add(new Paragraph(PDFUtil.html2pdf(ptext.getText(), font)));
						}
					}

//----------носилки

				} else if (code.equals("stretcher")) {
					if (!female) {
						RuleService rservice = new RuleService();
						Rule rule = (Rule)rservice.find(101L);
						if (rule != null)
							section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
					}
				}

//========= пятиугольник

			} else if (Arrays.asList(pentagon).contains(code)) {
				shape = printPentagon(conf);

				if (code.equals("arrester")) {
					appendix = new Paragraph();
					PlanetTextService ptservice = new PlanetTextService();
					for (Planet planet : conf.getVertex()) {
						PlanetText ptext = (PlanetText)ptservice.findByPlanet(planet.getId(), conf.isVertexPositive() ? "positive" : "negative");
						if (ptext != null) {
							String s = conf.isVertexPositive() ? planet.getPositive() : planet.getNegative();
							if (term)
								s += " (" + ptext.getPlanet().getName() + ")";
							appendix.add(new Paragraph(s, bold));
							appendix.add(new Paragraph(PDFUtil.html2pdf(ptext.getText(), font)));
						}
					}
				}

//========= шестиугольник

			} else if (Arrays.asList(hexagon).contains(code)) {
				shape = printHexagon(conf);
			}

			if (shape != null) {
				section.add(shape);
				section.add(Chunk.NEWLINE);
			}
			section.add(new Paragraph(PDFUtil.removeTags(text, font)));
			if (appendix != null)
				section.add(appendix);
			section.add(Chunk.NEWLINE);
			section.add(Chunk.NEWLINE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация толкования для возраста Полуповозки
	 * @param age возраст
	 * @param text толкование
	 * @return полное толкование
	 */
	@SuppressWarnings("unused")
	private String printStage(int age, String text) {
		return CoreUtil.getAgeString(age) + " - " + text;
	}

	/**
	 * Генерация лояльности
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printLoyalty(PdfWriter writer, Chapter chapter, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Лояльность");

			int loyalty = 0, flatness = 0, max = 5, loyalty2 = 0, flatness2 = 0;
			for (Model model : event.getConfiguration().getPlanets()) {
				Planet planet = (Planet)model;
				boolean loyal2 = planet.getHouse().getElement().isLoyalty();
		    	if (loyal2)
		    		++loyalty2;
		    	else
		    		++flatness2;

		    	if (!planet.isMain())
					continue;
				boolean loyal = planet.getSign().getElement().isLoyalty();
		    	if (loyal)
		    		++loyalty;
		    	else
		    		++flatness;
			}

			//определение выраженной категории
		    int diff = loyalty - flatness;
		    String title = "";
		    String text = "";
		    if (diff > 0) {
			    text = "Лояльность – это умение адаптироваться, быть гибким, понимающим и снисходительным. ";
		    	if (diff > 2)
		    		title = "Лояльный тип";
		    	else
		    		title = "Склонность больше к лояльности, чем к категоричности";
	    		text += "У вас это качество преобладает в соотношении " + loyalty + " из " + max;
		    } else {
			    text = "Категоричность – это нежелание адаптироваться, занижать планку и быть снисходительным. ";
			    int d = Math.abs(diff);
		    	if (d > 2)
		    		title = "Категоричный тип";
		    	else
		    		title = "Склонность больше к категоричности, чем к лояльности";
	    		text += "У вас это качество преобладает в соотношении " + flatness + " из " + max;
		    }
	    	section.add(new Paragraph(title, fonth5));
	    	if (term) {
	    		String s = (diff > 0)
	    			? "Наиболее выражены стихии Воздуха и Воды"
   	    			: "Наиболее выражены стихии Огня и Земли";
		    	section.add(new Paragraph(s, PDFUtil.getAnnotationFont(true)));
	    	}
	    	section.add(new Paragraph(text, font));

			Bar[] bars = new Bar[4];
	    	Bar bar = new Bar();
	    	bar.setName("Лояльность");
		    bar.setValue(loyalty * (-1));
	    	bar.setCategory("в сознании");
	    	bars[0] = bar;

	    	bar = new Bar();
	    	bar.setName("Категоричность");
		    bar.setValue(flatness * (-1));
	    	bar.setCategory("в сознании");
	    	bars[1] = bar;

	    	bar = new Bar();
	    	bar.setName("Лояльность");
		    bar.setValue(loyalty2);
	    	bar.setCategory("в поступках");
	    	bars[2] = bar;

	    	bar = new Bar();
	    	bar.setName("Категоричность");
		    bar.setValue(flatness2);
	    	bar.setCategory("в поступках");
	    	bars[3] = bar;

	    	section.add(Chunk.NEWLINE);
			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("Категория \"в сознании\" показывает вашу привычную модель: насколько лояльно вы относитесь к миру.", font));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Категория \"в поступках\" показывает, как уровень лояльности меняется на событийном уровне, в социуме по сравнению с предыдущей моделью.", font));
	        list.add(li);
	        section.add(list);

		    com.itextpdf.text.Image image = PDFUtil.printStackChart(writer, "Сравнение лояльности", "Аспекты", "Баллы", bars, 500, 0, true);
			section.add(image);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Отрисовка треугольной конфигурации
	 * @param conf конфигурация аспектов
	 * @return параграф с инфографикой
	 */
	private Paragraph printTriangle(AspectConfiguration conf) {
		try {
	        PdfPTable table = new PdfPTable(3);
	        table.setWidthPercentage(100);
	        Font font = PDFUtil.getRegularFont();
	        Color color = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
			if (conf.getCode().equals("triangle")) {
				if (conf.getElement() != null)
					color = conf.getElement().getDimColor();
			} else
				color = conf.getColor();
	        font.setColor(color.getRed(), color.getGreen(), color.getBlue());

	        kz.zvezdochet.bean.Element element = conf.getElement();
	        String[] passive = {"earth", "water"};
	        boolean headOverHeels = element != null && Arrays.asList(passive).contains(element.getCode());

	        //вершина
			String text = "";
			PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			if (headOverHeels) {
				for (Planet planet : conf.getLeftFoot()) {
					String s = conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative();
					if (term)
						s += " (" + planet.getName() + ")";
					text += s + "\n";
				}
				Paragraph p = new Paragraph(text, font);
				p.setAlignment(Element.ALIGN_RIGHT);
				cell.addElement(p);
			}
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			if (!headOverHeels) {
				text = "";
				for (Planet planet : conf.getVertex()) {
					String s = conf.isVertexPositive() ? planet.getPositive() : planet.getNegative();
					if (term)
						s += " (" + planet.getName() + ")";
					text += s + "\n";
				}
				Paragraph p = new Paragraph(text, font);
				p.setAlignment(Element.ALIGN_CENTER);
				cell.addElement(p);
			}
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			if (headOverHeels) {
				text = "";
				for (Planet planet : conf.getRightFoot()) {
					String s = conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative();
					if (term)
						s += " (" + planet.getName() + ")";
					text += s + "\n";
				}
				Paragraph p = new Paragraph(text, font);
				cell.addElement(p);
			}
			table.addCell(cell);

			//изображение
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			com.itextpdf.text.Image image = null;
			if (conf.getCode().equals("triangle")) {
				if (conf.getElement() != null) {
					String filename = PlatformUtil.getPath(Activator.PLUGIN_ID, "/icons/conf/" + conf.getElement().getCode() + ".gif").getPath();
					image = com.itextpdf.text.Image.getInstance(filename);
				}
			} else
				image = com.itextpdf.text.Image.getInstance(conf.getImageUrl());
			cell = new PdfPCell(image);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			//основание
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			if (!headOverHeels) {
				text = "";
				for (Planet planet : conf.getLeftFoot()) {
					String s = conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative();
					if (term)
						s += " (" + planet.getName() + ")";
					text += s + "\n";
				}
				Paragraph p = new Paragraph(text, font);
				p.setAlignment(Element.ALIGN_RIGHT);
				cell = new PdfPCell();
				cell.setBorder(Rectangle.NO_BORDER);
				cell.addElement(p);
			}
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			if (headOverHeels) {
				text = "";
				for (Planet planet : conf.getVertex()) {
					String s = conf.isVertexPositive() ? planet.getPositive() : planet.getNegative();
					if (term)
						s += " (" + planet.getName() + ")";
					text += s + "\n";
				}
				Paragraph p = new Paragraph(text, font);
				p.setAlignment(Element.ALIGN_CENTER);
				cell.addElement(p);
			}
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			text = "";
			if (!headOverHeels) {
				for (Planet planet : conf.getRightFoot()) {
					String s = conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative();
					if (term)
						s += " (" + planet.getName() + ")";
					text += s + "\n";
				}
				Paragraph p = new Paragraph(text, font);
				cell.addElement(p);
			}
			table.addCell(cell);

			Paragraph paragraph = new Paragraph();
			paragraph.add(table);
			return paragraph;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Отрисовка ромбовидной конфигурации
	 * @param conf конфигурация аспектов
	 * @return параграф с инфографикой
	 */
	private Paragraph printRhombus(AspectConfiguration conf) {
		try {
	        PdfPTable table = new PdfPTable(3);
	        table.setWidthPercentage(100);
	        Font font = PDFUtil.getRegularFont();
	        Color color = conf.getColor();
	        font.setColor(color.getRed(), color.getGreen(), color.getBlue());

	        //вершина
			PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			String text = "";
			for (Planet planet : conf.getVertex()) {
				String s = conf.isVertexPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			Paragraph p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_CENTER);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			//основание
			text = "";
			for (Planet planet : conf.getLeftFoot()) {
				String s = conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			cell = new PdfPCell();
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(conf.getImageUrl());
			cell = new PdfPCell(image);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			text = "";
			for (Planet planet : conf.getRightFoot()) {
				String s = conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			//низ
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			for (Planet planet : conf.getBase()) {
				String s = conf.isBasePositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_CENTER);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			Paragraph paragraph = new Paragraph();
			paragraph.add(table);
			return paragraph;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Генерация Лунного дня
	 * @param chapter раздел
	 * @param event событие
	 * @link http://mirkosmosa.ru/lunar-calendar/phase-moon/1977/december/10
	 * @link http://goroskop.org/luna/form.shtml
	 */
	private void printLunar(Chapter chapter, Event event) {
		try {
			long moondayid = event.getMoondayid();
			if (0 == moondayid)
				return;
			Pheno pheno = new Pheno((int)moondayid);
			if (pheno != null) {
				Moonday moonday = (Moonday)new MoondayService().find((long)pheno.getAge());
				if (moonday != null) {
					Section section = PDFUtil.printSection(chapter, "Лунный день");

					int number = pheno.getImageNumber();
					if (number > 0 && number < 31) {
						String filename = PlatformUtil.getPath(Activator.PLUGIN_ID, "/icons/moon/" + number + ".png").getPath();
						com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(filename);
						float side = 72f;
						image.scaleAbsolute(side, side);
						section.add(image);
					}
					section.add(new Paragraph(pheno.getAge() + "-й лунный день (" + pheno.getPhase() + ")", fonth5));
					String descr = "";
					Date start = pheno.getStart();
					if (start != null)
						descr += "Начало лунного дня с учётом времени и места: " + DateUtil.fulldtf.format(start);
					section.add(new Paragraph(descr, PDFUtil.getAnnotationFont(true)));

					Paragraph p = new Paragraph();
					p.add(new Chunk("Символ: ", new Font(baseFont, 12, Font.BOLD)));
			        p.add(new Chunk(moonday.getSymbol(), font));
			        section.add(p);

					section.add(new Paragraph(PDFUtil.removeTags(moonday.getBirth(), font)));
					URL url = PlatformUtil.getPath(Activator.PLUGIN_ID, "/icons/moon/" + moonday.getId() + ".jpg");
					if (url != null) {
						String filename = url.getPath();
						com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(filename);
						section.add(image);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Отрисовка четырёхугольной конфигурации
	 * @param conf конфигурация аспектов
	 * @return параграф с инфографикой
	 */
	private Paragraph printTetragon(AspectConfiguration conf) {
		try {
	        PdfPTable table = new PdfPTable(3);
	        table.setWidthPercentage(100);
	        Font font = PDFUtil.getRegularFont();
	        Color color = conf.getColor();
	        font.setColor(color.getRed(), color.getGreen(), color.getBlue());

	        //верх
			String text = "";
			for (Planet planet : conf.getLeftHand()) {
				String s = conf.isLeftHandPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			Paragraph p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			for (Planet planet : conf.getRightHand()) {
				String s = conf.isRightHandPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			//изображение
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(conf.getImageUrl());
			cell = new PdfPCell(image);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			//низ
			text = "";
			for (Planet planet : conf.getLeftFoot()) {
				String s = conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			for (Planet planet : conf.getRightFoot()) {
				String s = conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			Paragraph paragraph = new Paragraph();
			paragraph.add(table);
			return paragraph;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Отрисовка пятиугольной конфигурации
	 * @param conf конфигурация аспектов
	 * @return параграф с инфографикой
	 */
	private Paragraph printPentagon(AspectConfiguration conf) {
		try {
	        PdfPTable table = new PdfPTable(3);
	        table.setWidthPercentage(100);
	        Font font = PDFUtil.getRegularFont();
	        Color color = conf.getColor();
	        font.setColor(color.getRed(), color.getGreen(), color.getBlue());

	        //верх
	        PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			String text = "";
			for (Planet planet : conf.getVertex()) {
				String s = conf.isVertexPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			Paragraph p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_CENTER);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

	        //середина
			text = "";
			for (Planet planet : conf.getLeftHand()) {
				String s = conf.isLeftHandPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(conf.getImageUrl());
			cell = new PdfPCell(image);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			text = "";
			for (Planet planet : conf.getRightHand()) {
				String s = conf.isRightHandPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			//низ
			text = "";
			for (Planet planet : conf.getLeftFoot()) {
				String s = conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			for (Planet planet : conf.getRightFoot()) {
				String s = conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			Paragraph paragraph = new Paragraph();
			paragraph.add(table);
			return paragraph;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Отрисовка шестиугольной конфигурации
	 * @param conf конфигурация аспектов
	 * @return параграф с инфографикой
	 */
	private Paragraph printHexagon(AspectConfiguration conf) {
		try {
	        PdfPTable table = new PdfPTable(3);
	        table.setWidthPercentage(100);
	        Font font = PDFUtil.getRegularFont();
	        Color color = conf.getColor();
	        font.setColor(color.getRed(), color.getGreen(), color.getBlue());

	        //верх
	        String text = "";
			for (Planet planet : conf.getLeftHorn()) {
				String s = conf.isLeftHornPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			Paragraph p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

	        cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			for (Planet planet : conf.getRightHorn()) {
				String s = conf.isRightHornPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

	        //середина
			text = "";
			for (Planet planet : conf.getLeftHand()) {
				String s = conf.isLeftHandPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(conf.getImageUrl());
			cell = new PdfPCell(image);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			text = "";
			for (Planet planet : conf.getRightHand()) {
				String s = conf.isRightHandPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			//низ
			text = "";
			for (Planet planet : conf.getLeftFoot()) {
				String s = conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			for (Planet planet : conf.getRightFoot()) {
				String s = conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative();
				if (term)
					s += " (" + planet.getName() + ")";
				text += s + "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			Paragraph paragraph = new Paragraph();
			paragraph.add(table);
			return paragraph;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
