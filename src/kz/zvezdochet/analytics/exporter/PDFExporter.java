package kz.zvezdochet.analytics.exporter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.json.JSONException;
import org.json.JSONObject;

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
import kz.zvezdochet.analytics.bean.Category;
import kz.zvezdochet.analytics.bean.CrossSign;
import kz.zvezdochet.analytics.bean.Degree;
import kz.zvezdochet.analytics.bean.HouseSignRule;
import kz.zvezdochet.analytics.bean.HouseSignText;
import kz.zvezdochet.analytics.bean.Moonday;
import kz.zvezdochet.analytics.bean.Numerology;
import kz.zvezdochet.analytics.bean.PlanetAspectText;
import kz.zvezdochet.analytics.bean.PlanetHouseRule;
import kz.zvezdochet.analytics.bean.PlanetHouseText;
import kz.zvezdochet.analytics.bean.PlanetSignText;
import kz.zvezdochet.analytics.bean.PlanetText;
import kz.zvezdochet.analytics.bean.Rule;
import kz.zvezdochet.analytics.service.CardTypeService;
import kz.zvezdochet.analytics.service.CrossSignService;
import kz.zvezdochet.analytics.service.DegreeService;
import kz.zvezdochet.analytics.service.HouseSignRuleService;
import kz.zvezdochet.analytics.service.HouseSignService;
import kz.zvezdochet.analytics.service.MoondayService;
import kz.zvezdochet.analytics.service.NumerologyService;
import kz.zvezdochet.analytics.service.PlanetAspectService;
import kz.zvezdochet.analytics.service.PlanetHouseRuleService;
import kz.zvezdochet.analytics.service.PlanetHouseService;
import kz.zvezdochet.analytics.service.PlanetSignService;
import kz.zvezdochet.analytics.service.PlanetTextService;
import kz.zvezdochet.analytics.service.RuleService;
import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectConfiguration;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.CardKind;
import kz.zvezdochet.bean.Cross;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.EventConfiguration;
import kz.zvezdochet.bean.Halfsphere;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Pheno;
import kz.zvezdochet.bean.Place;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.PlanetHousePosition;
import kz.zvezdochet.bean.PlanetSignPosition;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.bean.SkyPoint;
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.bean.Square;
import kz.zvezdochet.bean.YinYang;
import kz.zvezdochet.bean.Zone;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.CoreUtil;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.core.util.OsUtil;
import kz.zvezdochet.core.util.PlatformUtil;
import kz.zvezdochet.core.util.StringUtil;
import kz.zvezdochet.export.bean.Bar;
import kz.zvezdochet.export.handler.PageEventHandler;
import kz.zvezdochet.export.util.PDFUtil;
import kz.zvezdochet.service.AspectConfigurationService;
import kz.zvezdochet.service.AspectService;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.CardKindService;
import kz.zvezdochet.service.CrossService;
import kz.zvezdochet.service.ElementService;
import kz.zvezdochet.service.EventConfigurationService;
import kz.zvezdochet.service.EventService;
import kz.zvezdochet.service.HalfsphereService;
import kz.zvezdochet.service.PlanetHousePositionService;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.service.PlanetSignPositionService;
import kz.zvezdochet.service.SignService;
import kz.zvezdochet.service.SquareService;
import kz.zvezdochet.service.YinYangService;
import kz.zvezdochet.service.ZoneService;
import kz.zvezdochet.util.Cosmogram;

/**
 * Генератор PDF-файла натальной карты
 * @author Natalie Didenko
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
 * 
 * https://astrology.org.ua/rectification.htm
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
		event.initData(true);
		child = event.isChild();
		female = event.isFemale();
		this.term = term;

		saveCard(event);
		Document doc = new Document();
		try {
			String filename = PlatformUtil.getPath(Activator.PLUGIN_ID, "/out/natal.pdf").getPath();
			PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(filename));
			PageEventHandler handler = new PageEventHandler();
	        writer.setPageEvent(handler);
	        doc.open();

	        //metadata
	        PDFUtil.getMetaData(doc, "Натальная карта");

	        //раздел
			Chapter chapter = new ChapterAutoNumber("Общая информация");
			chapter.setNumberDepth(0);

			//шапка
			Paragraph p = new Paragraph();
			PDFUtil.printHeader(p, "Натальная карта", null);
			chapter.add(p);

			p = new Paragraph();
			String text = (event.isCelebrity() ? event.getName() : event.getCallname()) + " – ";
			text += DateUtil.fulldtf.format(event.getBirth());
			p.add(new Chunk(text, font));
			if (!event.isRectified())
				p.add(new Chunk(" (не ректифицировано)", PDFUtil.getDangerFont()));
	        p.setAlignment(Element.ALIGN_CENTER);
			chapter.add(p);

			Place place = event.getPlace();
			if (null == place)
				place = new Place().getDefault();
			text = (event.getZone() >= 0 ? "UTC+" : "") + event.getZone() +
				" " + (event.getDst() >= 0 ? "DST+" : "DST") + event.getDst() + 
				" " + place.getName() +
				" " + place.getLatitude() + "°" +
				", " + place.getLongitude() + "°";
			p = new Paragraph(text, font);
	        p.setAlignment(Element.ALIGN_CENTER);
			chapter.add(p);

			Font fontgray = PDFUtil.getAnnotationFont(false);
			Date date = (null == event.getDate()) ? new Date() : event.getDate(); 
			text = "Дата составления: " + DateUtil.fulldtf.format(date);
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

			p = new Paragraph("Файл содержит ваши врождённые предрасположенности. "
				+ "Ваша личность описана как с позиции ", font);
			Anchor anchor = new Anchor("силы", fonta);
            anchor.setReference("#planetstrong");
	        p.add(anchor);
			p.add(new Chunk(", так и с позиции ", font));
			anchor = new Anchor("слабости", fonta);
            anchor.setReference("#planetweak");
	        p.add(anchor);
			p.add(new Chunk(". Не зацикливайтесь на недостатках – развивайте свои сильные стороны, "
				+ "используя благоприятные факторы гороскопа, – это более эффективно. "
				+ "Судьба и так сделает всё, чтобы помочь вам закалить характер.", font));
	        p.setSpacingAfter(10);
			chapter.add(p);
			chapter.add(new Paragraph("Если вы росли без отца или матери, но в гороскопе они упомянуты, "
				+ "значит через указанные толкования проявится наследственность ваших родителей, "
				+ "а также влияние других опекунов (бабушек, дедушек, мачехи, отчима, крёстных)", font));

			//космограмма
			printCard(doc, chapter);
			chapter.add(Chunk.NEXTPAGE);

			//лунный день
			printLunar(chapter, event);

			EventStatistics statistics = new EventStatistics(event);
			Map<String, Double> signMap = statistics.getPlanetSigns(true);

			//градус рождения
			boolean exact = event.getRectification() < 2;
			if (exact) {
				printDegree(chapter, event);
				chapter.add(Chunk.NEXTPAGE);
			}

			//знаки
			printSigns(writer, chapter, event, signMap);
			chapter.add(Chunk.NEXTPAGE);

			//дома
			statistics.initPlanetHouses();
			if (event.isHousable()) {
				printHouses(writer, chapter, event, statistics);
				chapter.add(Chunk.NEXTPAGE);
			}

			//счастливые символы
			printSymbols(chapter, event);
			doc.add(chapter);

			chapter = new ChapterAutoNumber(PDFUtil.printHeader(new Paragraph(), "Ваш психотип", "commontype"));
			chapter.setNumberDepth(0);
			if (term)
				chapter.add(new Paragraph("Психотип строится на основе положения минорных планет в натальных знаках Зодиака. "
					+ "Это ваш обобщённый образ, потому что он характеризует вас как представителя своего поколения, а не как уникальную личность. "
					+ "Более точное и персонализированное описание вашей натуры строится на основе аспектов планет и их положения в астрологических домах (см. дальнейшие разделы)", font));
			else
				chapter.add(new Paragraph("Психотип – это обобщённый образ людей, рождённых вблизи " + DateUtil.sdf.format(event.getBirth())
					+ ". Психотип характеризует вас как представителя своего поколения, а не как уникальную личность. "
					+ "Более точное и персонализированное описание вашей натуры приведено в дальнейших разделах", PDFUtil.getWarningFont()));

			//планеты в знаках
			printPlanetSign(chapter, event);
			doc.add(chapter);

			chapter = new ChapterAutoNumber(PDFUtil.printHeader(new Paragraph(), "Анализ карты рождения", null));
			chapter.setNumberDepth(0);

			Font greenfont = PDFUtil.getSuccessFont();
			anchor = new Anchor("Ваш психотип", fonta);
            anchor.setReference("#commontype");
			p = new Paragraph();
			p.add(new Chunk("В разделе ", greenfont));
	        p.add(anchor);
			p.add(new Chunk(" дана обобщённая характеристика вашей личности. "
				+ "Теперь речь пойдёт о ваших собственных наработках и качествах: как вы в реальности ведёте себя в жизненных ситуациях, "
				+ "и чем отличаетесь от себе подобных", greenfont));
			chapter.add(p); 

			//вид космограммы
			printCardKind(doc, chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//тип космограммы
			printCardType(chapter, event);

			//планеты
			printPlanetStrong(chapter, event);
			printPlanetWeak(chapter, event);
			printPlanetRetro(chapter, event);
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

			printAspects(chapter, event, term ? "Позитивные аспекты" : "Позитивные сочетания", "POSITIVE");
			chapter.add(Chunk.NEXTPAGE);
			printAspects(chapter, event, term ? "Негативные аспекты" : "Негативные сочетания", "NEGATIVE");
			chapter.add(Chunk.NEXTPAGE);
			doc.add(chapter); 

			chapter = new ChapterAutoNumber(PDFUtil.printHeader(new Paragraph(), "Реализация личности", "planethouses"));
			chapter.setNumberDepth(0);

			//планеты в домах
			if (event.isHousable()) {
				printPlanetHouses(chapter, event);
				doc.add(chapter);
			}
			
			chapter = new ChapterAutoNumber(PDFUtil.printHeader(new Paragraph(), "Диаграммы", null));
			chapter.setNumberDepth(0);

			//сила планет
			printPlanetStrength(writer, chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//аспекты
			printAspectTypes(writer, chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//стихии
			statistics.initPlanetDivisions();
			statistics.initHouseDivisions();
			printElements(writer, chapter, statistics, event);
			chapter.add(Chunk.NEXTPAGE);

			//лояльность
			printLoyalty(writer, chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//инь-ян
			printYinYang(writer, chapter, statistics, event);
			chapter.add(Chunk.NEXTPAGE);
			
			//полусферы
			printHalfSpheres(writer, chapter, statistics, event);
			chapter.add(Chunk.NEXTPAGE);

			//квадраты
			printSquares(writer, chapter, statistics, signMap, event);
			chapter.add(Chunk.NEXTPAGE);
			
			//кресты
			printCrosses(writer, chapter, statistics, event);
			chapter.add(Chunk.NEXTPAGE);
			
			//зоны
			printZones(writer, chapter, statistics, event);
			chapter.add(Chunk.NEXTPAGE);

			//конфигурации аспектов
			printConfigurations(doc, chapter, event);

			//координаты планет
			printCoords(chapter, event);
			doc.add(chapter);

			chapter = new ChapterAutoNumber(PDFUtil.printHeader(new Paragraph(), "Сокращения", null));
			chapter.setNumberDepth(0);
			printAbbreviation(chapter);
			doc.add(chapter);

//			PDFUtil.printTOC(doc, handler);

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
			new Cosmogram(event, null, null, gc, false);
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
				Section section = PDFUtil.printSection(chapter, "Однодневки", null);
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

			        chunk = new Chunk("   " + event.getComment(), font);
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
	@SuppressWarnings("unused")
	private void printSimilar(Chapter chapter, Event event) {
		try {
			List<Model> events = new EventService().findSimilar(event, 1);
			if (events != null && events.size() > 0) {
				Section section = PDFUtil.printSection(chapter, "Близкие по духу", null);
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

			        chunk = new Chunk("   " + man.getComment(), font);
			        li.add(chunk);
			        list.add(li);
				}
				section.add(list);
				chapter.add(Chunk.NEXTPAGE);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация диаграмм знаков
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param event персона
	 * @param signMap карта знаков
	 */
	private void printSigns(PdfWriter writer, Chapter chapter, Event event, Map<String, Double> signMap) {
		try {
			//выраженные знаки
			Section section = PDFUtil.printSection(chapter, "Знаки Зодиака", null);
			Font asfont = new Font(PDFUtil.getAstroFont(), 14, Font.NORMAL);

			String link = "https://zvezdochet.guru/ru/post/63/sravnitelnaya-infografika-astrologicheskih-sistem";
			Paragraph p = new Paragraph();
			p.add(new Chunk("Диаграмма построена на основе ", font));
			Chunk chunk = new Chunk(term ? "непустых натальных созвездий" : "сидерического зодиака", new Font(baseFont, 12, Font.UNDERLINE, PDFUtil.FONTCOLOR));
			chunk.setAnchor(link);
			p.add(chunk);
			if (term) {
				p.add(new Chunk(", в которых находятся ваши минорные (личные) планеты:", font));
				section.add(p);
				section.add(Chunk.NEWLINE);

				com.itextpdf.text.List ilist = new com.itextpdf.text.List(false, false, 10);
				Map<Long, Planet> planets = event.getPlanets();
				for (Planet planet : planets.values()) {
					if (!planet.isMain())
						continue;
					ListItem li = new ListItem();
					Sign sign = planet.getSign();
					String symbol = sign.getCode().equals("Ophiuchus") ? "∞" : sign.getSymbol();
					li.add(new Chunk(planet.getSymbol(), asfont));
					li.add(new Chunk(" " + planet.getName() + " в созвездии ", font));
					li.add(new Chunk(symbol, asfont));
					li.add(new Chunk(" " + sign.getName(), font));
					ilist.add(li);
				}
		        section.add(ilist);
			} else {
				p.add(new Chunk(", в котором знаки идентичны созвездиям", font));
				section.add(p);
			}
			section.add(Chunk.NEWLINE);

			int size = signMap.size();
			Bar[] bars = new Bar[size];
			Bar[] bars2 = new Bar[size];
			Iterator<Map.Entry<String, Double>> iterator = signMap.entrySet().iterator();
			int i = -1;
			SignService service = new SignService();

		    while (iterator.hasNext()) {
		    	i++;
		    	Entry<String, Double> entry = iterator.next();
		    	double val = entry.getValue();

		    	Bar bar = new Bar();
		    	Sign sign = (Sign)service.find(entry.getKey());
		    	bar.setName(sign.getName());
		    	bar.setValue(val);
		    	bar.setColor(sign.getColor());
		    	bars[i] = bar;
	
		    	bar = new Bar();
		    	bar.setName(sign.getDescription());
		    	bar.setValue(val);
		    	bar.setColor(sign.getColor());
		    	bar.setCategory("Кредо");
		    	bars2[i] = bar;		    	
		    }
		    com.itextpdf.text.Image image = PDFUtil.printPie(writer, "Выраженные знаки Зодиака", bars, 400, 0, false);
			section.add(image);
	
			//кредо
			section = PDFUtil.printSection(chapter, "Кредо вашей жизни", null);
		    PdfPTable table = PDFUtil.printTableChart(writer, bars2, "Кредо вашей жизни", true);
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
			Section section = PDFUtil.printSection(chapter, "Градус рождения", null);
			section.add(new Paragraph("Градус рождения – это зодиакальный градус, который восходил над горизонтом в момент вашего рождения. "
				+ "Этот градус в общем виде определяет ваши способности и имеет визуальный образ:", font));
			section.add(Chunk.NEWLINE);

			if (event.getHouses() != null && event.getHouses().size() > 0) {
				House house = event.getHouses().get(142L);
				if (null == house) return;
				int value = (int)house.getLongitude();
				Model model = new DegreeService().find((long)value);
			    if (model != null) {
			    	Degree degree = (Degree)model;
			    	String text = degree.getText();
			    	if (0 == text.trim().length())
			    		section.add(new Paragraph("Градус ASC: " + degree.getId(), font));
			    	if (term)
			    		section.add(new Paragraph("Градус ASC: " + degree.getId() + "° управитель " + degree.getName() + ", " + degree.getCode(), fonth5));
			    	String star = degree.isRoyal() || degree.isOccult() ? "*" : "";
					section.add(new Paragraph(degree.getDescription() + star, PDFUtil.getAnnotationFont(true)));
					section.add(new Paragraph(PDFUtil.removeTags(text, font)));
					PDFUtil.printGender(section, degree, female, child, true);

					if (degree.isRoyal()) {
						Paragraph p = new Paragraph("*Вы родились под королевским градусом, и хотя он не гарантирует безоблачной жизни, но вам удастся пересилить трудности, которые кажутся непреодолимыми."
							+ " Королевский градус даёт испытание через получение позитивных возможностей (обретение жизненного опыта через успех)."
							+ " Девиз такого градуса: «Надежда умирает последней», а значит вы получите помощь в сложный период жизни, благодаря чему вновь обретёте удачу", font);
						p.setSpacingAfter(10);
						section.add(p);
					}
					if (degree.isOccult()) {
						Paragraph p = new Paragraph("*Вы родились под оккультным градусом, который определяет вас как посвящённого человека, "
							+ "которому доступен опыт взаимодействия со скрытыми и неизвестными науке энергиями и явлениями", font);
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
			Section section = PDFUtil.printSection(chapter, "Космограмма", "cosmogram");

			String filename = PlatformUtil.getPath(Activator.PLUGIN_ID, "/out/card.png").getPath();
			com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(filename);
			float side = 300f;
			image.scaleAbsolute(side, side);
			float x = (doc.right() - doc.left()) / 2 - (side / 2);
			image.setIndentationLeft(x);
			section.add(image);

			String text = "Космограмма – это уникальный отпечаток положения планет на небе в момент вашего рождения. Планеты расположены так, как если бы вы смотрели на них с Земли:";
			section.add(new Paragraph(text, font));

			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			String[] items = {
				"ближе к точке ASC расположены планеты, восходящие над горизонтом",
				"ближе к IC – планеты в надире",
				"ближе к DSC – планеты, заходящие за горизонт",
				"ближе к MC – планеты в зените"
			};
			for (String item : items) {
				ListItem li = new ListItem();
		        Chunk chunk = new Chunk(item, font);
		        li.add(chunk);
		        list.add(li);
			}
			section.add(list);

			Anchor anchor = new Anchor("Координаты планет", fonta);
            anchor.setReference("#planetcoords");
			Paragraph p = new Paragraph();
			p.add(new Chunk("Подробности в разделе ", font));
	        p.add(anchor);
			section.add(p);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация планет в знаках
	 * @param chapter раздел
	 * @param event событие
	 * @todo искать на базе категорий, потому что для категории может не быть ещё текста
	 */
	private void printPlanetSign(Chapter chapter, Event event) {
		try {
			if (event.getPlanets() != null) {
				PlanetSignService service = new PlanetSignService();
				PlanetSignPositionService positionService = new PlanetSignPositionService();

				Font afont = PDFUtil.getHeaderAstroFont();
				Font grayfont = PDFUtil.getAnnotationFont(true);

				Collection<Planet> planets = event.getPlanets().values();
				boolean baby = event.isBaby();
				for (Planet planet : planets) {
				    if (planet.isMain()) {
				    	Sign sign = planet.getSign();
				    	List<PlanetSignText> list = service.find(planet, sign);
				    	if (list != null && list.size() > 0)
				    		for (PlanetSignText object : list) {
				    			Category category = object.getCategory();
				    			String catcode = category.getCode();
				    			if (catcode.equals("childhood") && !baby)
				    				continue;
				    			if (child && Arrays.asList(new String[] {"sex", "male", "female"}).contains(catcode))
				    				continue;
				    			if (catcode.equals("male") && !female)
				    				continue;
				    			if (catcode.equals("female") && female)
				    				continue;

				    			Section section = PDFUtil.printSection(chapter, category.getName(), null);
				    			if (term) {
				    				section.add(new Chunk(planet.getMark("sign", term) + " ", fonth5));
				    				section.add(new Chunk(planet.getSymbol(), afont));
				    				section.add(new Chunk(" " + planet.getName() + " в созвездии " + sign.getName() + " ", fonth5));
				    				section.add(new Chunk(sign.getSymbol(), afont));

				    				if (Arrays.asList(new String[] {"personality", "emotions", "thinking", "feelings", "activity"}).contains(catcode)) {
					    				PlanetSignPosition position = positionService.find(planet);
					    				if (position != null) {
					    					String s = position.getDescription() + ". " + position.getType().getDescription();
					    					if (planet.isDamaged())
					    						s += ". Поскольку планета " + planet.getName() + " поражена, то это местами затруднит её проявление";
					    					else if (planet.inMine())
					    						s += ". Поскольку планета " + planet.getName() + " находится в шахте, то это ослабит её проявление";
					    					section.add(new Paragraph(s, grayfont));
					    					section.add(Chunk.NEWLINE);
					    				}
				    				}
				    			}
				    			String text = object.getText();
				    			if (text != null) {
					    			Paragraph p = catcode.equals("profession")
					    				? new Paragraph(PDFUtil.html2pdf(text, font))
					    				: new Paragraph(PDFUtil.removeTags(text, font));
					    			section.add(p);
					    			PDFUtil.printGender(section, object, female, child, true);
				    			}
								Rule rule = EventRules.rulePlanetSign(planet, sign, event, category);
								if (rule != null) {
									section.add(Chunk.NEWLINE);
									section.add(new Paragraph(PDFUtil.removeTags("Rule" + rule.getText(), font)));
									section.add(Chunk.NEWLINE);
								}
								section.add(Chunk.NEWLINE);
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
			Section section = PDFUtil.printSection(chapter, "Координаты планет", "planetcoords");
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
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				BaseColor color = (++i % 2 > 0) ? new BaseColor(255, 255, 255) : new BaseColor(230, 230, 250);

				cell = new PdfPCell(new Phrase(CalcUtil.roundTo(planet.getLongitude(), 2) + "°", font));
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

				if (planet.isBroken())
					descr += "слб ";

				if (planet.inMine())
					descr += "шхт ";

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
				if (event.isHousable())
					cell = new PdfPCell(new Phrase(CalcUtil.roundTo(house.getLongitude(), 2) + "°", font));
				else
					cell = new PdfPCell();
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
				table.addCell(cell);

		        descr = "";
				cell = new PdfPCell();
				if (event.isHousable()) {
					scolor = house.getElement().getDimColor();
					if (planet.isHouseHome())
						descr = "(обт)";
					else if (planet.isHouseExaltated())
						descr = "(экз)";
					else if (planet.isHouseDeclined())
						descr = "(пдн)";
					else if (planet.isHouseExile())
						descr = "(изг)";
					cell.addElement(new Phrase(house.getName() + " " + descr, new Font(baseFont, fontsize, Font.NORMAL, new BaseColor(scolor.getRed(), scolor.getGreen(), scolor.getBlue()))));
				}
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
				table.addCell(cell);
			}
			section.add(table);
			section.add(new Paragraph("Сокращения и символы, использованные в таблице, описаны в конце документа", PDFUtil.getAnnotationFont(false)));

			//дома
			if (!event.isHousable()) return;
			section.add(Chunk.NEXTPAGE);
			section = PDFUtil.printSection(chapter, "Координаты астрологических домов", "housecoords");

			table = new PdfPTable(4);
	        table.setSpacingBefore(10);

			cell = new PdfPCell(new Phrase("Градус дома", font));
	        cell.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("Обозначение", font));
	        cell.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("Созвездие", font));
	        cell.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("Описание", font));
	        cell.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cell);

			i = -1;
			Collection<House> houses = event.getHouses().values();
			for (House house : houses) {
				BaseColor color = (++i % 2 > 0) ? new BaseColor(255, 255, 255) : new BaseColor(230, 230, 250);

				cell = new PdfPCell(new Phrase(CalcUtil.roundTo(house.getLongitude(), 3) + "°", font));
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
				table.addCell(cell);

				Color scolor = house.getElement().getDimColor();
				cell = new PdfPCell();
				cell.addElement(new Phrase(house.getDesignation(), new Font(baseFont, fontsize, Font.NORMAL, new BaseColor(scolor.getRed(), scolor.getGreen(), scolor.getBlue()))));
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
		        table.addCell(cell);

				Sign sign = house.getSign();
				scolor = sign.getElement().getDimColor();
		        cell = new PdfPCell();
				cell.addElement(new Phrase(sign.getName(), new Font(baseFont, fontsize, Font.NORMAL, new BaseColor(scolor.getRed(), scolor.getGreen(), scolor.getBlue()))));
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
				table.addCell(cell);

				cell = new PdfPCell();
				cell.addElement(new Phrase(house.getName(), new Font(baseFont, fontsize, Font.NORMAL)));
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
	 * @param doc документ
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printCardKind(Document doc, Chapter chapter, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Кармический потенциал", null);
			Anchor anchor = new Anchor("Рисунок вашего гороскопа", fonta);
            anchor.setReference("#cosmogram");
			Paragraph p = new Paragraph();
	        p.add(anchor);
			p.add(new Chunk(" показывает общую картину, которая не в деталях, а глобально описывает ваше предназначение и опыт прошлого:", font));
			section.add(p);

			long id = event.getCardkindid();
			if (0 == id) return;
			CardKind kind = (CardKind)new CardKindService().find(id);
			p = new Paragraph(kind.getName(), fonth5);
			section.add(p);

			JSONObject jsonObject = new JSONObject(event.getOptions());
			URL filename = PlatformUtil.getPath(Activator.PLUGIN_ID, "/icons/kind/" + kind.getCode() + ".png");
			if (filename != null) {
				com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(filename.getPath());
	
				if (4 == id || 6 == id) { //поворачиваем лук и чашу
					float angle = 0;
					if (jsonObject != null) {
						JSONObject obj = jsonObject.getJSONObject("cardkind");
						if (obj != null) {
							String direction = obj.getString("direction");
							if (null == direction || direction.isEmpty()) {
								DialogUtil.alertWarning("Задайте направление чаши East|West|North|South");
								return;
							} else {
								if (direction.equals("North"))
									angle = 180;
								else if (direction.equals("West"))
									angle = 270;
								else if (direction.equals("East"))
									angle = 90;
							}
						}
					}
					if (angle > 0)
						image.setRotationDegrees(angle);
				}
				section.add(image);
			}

			if (term) {
				String text = kind.getDegree() + ". " + kind.getDescription();
				p = new Paragraph(text, PDFUtil.getAnnotationFont(true));
				p.setSpacingAfter(10);
				section.add(p);
			}
			section.add(new Paragraph(PDFUtil.html2pdf(kind.getText(), font)));
			Font bold = new Font(baseFont, 12, Font.BOLD);
			Font boldred = new Font(baseFont, 12, Font.BOLD, PDFUtil.FONTCOLORED);
			Font red = new Font(baseFont, 12, Font.NORMAL, PDFUtil.FONTCOLORED);
			Font boldgreen = new Font(baseFont, 12, Font.BOLD, PDFUtil.FONTCOLORGREEN);
			Font green = new Font(baseFont, 12, Font.NORMAL, PDFUtil.FONTCOLORGREEN);

//----------тигр

			if (1 == id) {
				try {
				     if (jsonObject != null) {
				    	 JSONObject obj = jsonObject.getJSONObject("cardkind");
				    	 if (obj != null) {
					    	 section.add(Chunk.NEXTPAGE);
				    		 section.add(new Paragraph("Пустая область рисунка — это ваша «мёртвая зона».", boldred));
				    		 section.add(new Paragraph("Две краевые точки мёртвой зоны указывают на то, "
					    		 	+ "чем вам необходимо овладеть в этом воплощении, "
					    		 	+ "что будет способствовать целостности вашей натуры "
					    		 	+ "и что нужно научиться совмещать:", font));
					    	 section.add(Chunk.NEWLINE);

				    		 long pid = obj.getLong("planet");
				    		 long pid2 = obj.getLong("planet2");
				    		 if (pid > 0 && pid2 > 0) {
						    	 PlanetTextService service = new PlanetTextService();
						    	 PlanetText planetText = (PlanetText)service.findByPlanet(pid, "positive");
						    	 if (planetText != null) {
						    		 section.add(new Paragraph("1) " + (term ? planetText.getPlanet().getName() : planetText.getPlanet().getPositive()), boldgreen));
						    		 section.add(new Paragraph("На факторы, указанные в данном пункте, сделайте особый упор. "
						    		 	+ "Они станут главной точкой приложения ваших сил:", PDFUtil.getSuccessFont()));
						    		 section.add(new Paragraph(PDFUtil.html2pdf(planetText.getText(), font)));
						    	 }
						    	 section.add(Chunk.NEWLINE);
						    	 planetText = (PlanetText)service.findByPlanet(pid2, "positive");
						    	 if (planetText != null) {
						    		 section.add(new Paragraph("2) " + (term ? planetText.getPlanet().getName() : planetText.getPlanet().getPositive()) + ":", bold));
						    		 section.add(new Paragraph(PDFUtil.html2pdf(planetText.getText(), font)));
						    	 }
				    		 } else {
				    			 DialogUtil.alertWarning("Задайте обе планеты тигра, обрамляющие пустую зону (левая - planet, правая - planet2, если рассматривать конфигурацию вниз дном)");
				    			 return;
				    		 }

				    		 section.add(new Paragraph("Пустые знаки Зодиака", boldred));
				    		 section.add(new Paragraph("Указанные ниже знаки Зодиака не заполнены в вашем гороскопе, "
				    		 	+ "значит вы ощутите недостаток свойств и черт характера, "
				    		 	+ "которые они олицетворяют:", font));
				    		 String signs = obj.getString("signs");
				    		 if (null == signs || signs.isEmpty()) {
				    			 DialogUtil.alertWarning("Задайте пустые знаки тигра (signs)");
				    			 return;
				    		 } else {
				    			 String[] arr = signs.split(",");
				    			 if (arr.length > 0) {
				    				 com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
				    				 SignService signService = new SignService();
				    				 for (String sid : arr) {
				    					 Sign sign = (Sign)signService.find(Long.valueOf(sid));
				    					 if (sign != null) {
				    						ListItem li = new ListItem();
				    						li.add(new Chunk(sign.getName() + ": ", bold));
				    						li.add(new Chunk(sign.getShortname(), font));
				    						list.add(li);
				    					}
				    				 }
				    				 section.add(list);
						    	 }
				    		 }

				    		 if (event.isHousable()) {
				    			 section.add(Chunk.NEWLINE);
					    		 section.add(new Paragraph("Пустые сферы жизни", boldred));
					    		 section.add(new Paragraph("Указанные ниже сферы жизни изначально не будут важны для вас:", font));
					    		 String houses = obj.getString("houses");
					    		 if (null == houses || houses.isEmpty()) {
					    			 DialogUtil.alertWarning("Задайте пустые вершины домов тигра (houses)");
					    			 return;
					    		 } else {
					    			 String[] arr = houses.split(",");
					    			 if (arr.length > 0) {
					    				 com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
					    				 for (String sid : arr) {
					    					 House house = event.getHouses().get(Long.valueOf(sid));
					    					 if (house != null) {
					    						ListItem li = new ListItem();
					    						li.add(new Chunk(house.getName(), font));
					    						list.add(li);
					    					}
					    				 }
					    				 section.add(list);
							    	 }
					    		 }
				    		 }
				    	 }
				     }
				} catch (JSONException ex) {
				     ex.printStackTrace();
				}

//----------праща

			} else if (8 == id) {
				//планеты напротив чаши
				Long pids[] = {22L, 30L};
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
					if (rule != null) {
						section.add(new Paragraph("Прицел пращи сдвинут влево:", bold));
						section.add(new Paragraph(PDFUtil.removeTags("Rule" + rule.getText(), font)));
					}
				}
				//Если планета-снаряд смещена вправо
				boolean right = true;
				if (right) {
					section.add(Chunk.NEWLINE);
					RuleService rservice = new RuleService();
					Rule rule = (Rule)rservice.find(86L);
					if (rule != null) {
						section.add(new Paragraph("Прицел пращи сдвинут вправо:", bold));
						section.add(new Paragraph(PDFUtil.removeTags("Rule" + rule.getText(), font)));
					}
				}

//----------Колыбель Ньютона

			} else if (13 == id) {
			     if (jsonObject != null) {
			    	 JSONObject obj = jsonObject.getJSONObject("cardkind");
			    	 if (obj != null) {
			    		 long pid1 = obj.getLong("planet");
			    		 long pid2 = obj.getLong("planet2");
			    		 if (pid1 > 0 && pid2 > 0) {
			    			 long[] pids = {pid1, pid2};
				    		 com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
				    		 for (long pid : pids) {
				    			 Planet planet = event.getPlanets().get(Long.valueOf(pid));
				    			 if (planet != null) {
				    				 ListItem li = new ListItem();
				    				 String s = term
				    					? planet.getName() + " в " + planet.getHouse().getDesignation()
				    					: planet.getShortName() + " + " + planet.getHouse().getName();
				    				 anchor = new Anchor(s, fonta);
				    				 anchor.setReference("#" + planet.getAnchor());
				    				 li.add(anchor);
				    				 list.add(li);
				    			 }
				    		 }
				    		 section.add(list);
			    		 } else {
			    			 DialogUtil.alertWarning("Задайте боковые планеты колыбели Ньютона (левые - planet, правые - planet2)");
			    			 return;			    			 
			    		 }
			    	 }
			     }

//----------лук
			} else if (6 == id) {
			     if (jsonObject != null) {
			    	 JSONObject obj = jsonObject.getJSONObject("cardkind");
			    	 if (obj != null) {
				    	 Object pids = obj.get("planet"); //планеты на острие стрелы
				    	 if (null == pids
				    			 || pids.toString().isEmpty()
				    			 || pids.toString().equals("0")) {
			    			 DialogUtil.alertWarning("Задайте планеты на острие лука (planet)");
			    			 return;
				    	 } else {
				    		 com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
				    		 String[] arr = pids.toString().split(",");
				    		 for (String pid : arr) {
				    			 Planet planet = event.getPlanets().get(Long.valueOf(pid));
				    			 if (planet != null) {
				    				 ListItem li = new ListItem();
				    				 boolean negative = planet.isNegative()
				    					|| planet.getCode().equals("Lilith")
				    					|| planet.getCode().equals("Kethu");
				    				 String sign = negative ? " - " : " + ";
				    				 String s = term
				    					? planet.getName() + " в " + planet.getHouse().getDesignation()
				    					: planet.getShortName() + sign + planet.getHouse().getName();
				    				 anchor = new Anchor(s, fonta);
				    				 anchor.setReference("#" + planet.getAnchor());
				    				 li.add(anchor);
				    				 li.add(new Chunk(negative ? " (ваша негативная стрела)" : " (ваша позитивная стрела)", negative ? red : green));
				    				 list.add(li);
				    			 }
				    		 }
				    		 section.add(list);
				    	 }

				    	 //куда направлена стрела лука
				    	 try {
				    		 String direction = obj.getString("direction");
				    		 if (null == direction || direction.isEmpty()) {
				    			 DialogUtil.alertWarning("Задайте направление чаши East|West|North|South (direction)");
				    			 return;
				    		 } else {
				    			 Halfsphere halfsphere = (Halfsphere)new HalfsphereService().find(direction);
				    			 if (halfsphere != null) {
				    				 p = new Paragraph(PDFUtil.removeTags(halfsphere.getBow(), font));
				    				 p.setSpacingBefore(10);
				    				 section.add(p);
				    			 }
				    		 }
				    	 } catch (JSONException ex) {
				    		 ex.printStackTrace();
				    	 }
			    	 }
			     }

//----------чаша
			} else if (4 == id) {
			     if (jsonObject != null) {
			    	 JSONObject obj = jsonObject.getJSONObject("cardkind");
			    	 if (obj != null) {
				    	 int pid1 = obj.getInt("planet"); //левая планета чаши
				    	 if (0 == pid1) {
			    			 DialogUtil.alertWarning("Задайте левую планету чаши (planet), если рассматривать чашу вниз дном");
			    			 return;
				    	 }
				    	 int pid2 = obj.getInt("planet2"); //правая планета чаши
				    	 if (0 == pid2) {
			    			 DialogUtil.alertWarning("Задайте правую планету чаши (planet2), если рассматривать чашу вниз дном");
			    			 return;
				    	 }
				    	 String pids = obj.getString("planet3"); //планета или соединение на дне чаши
				    	 if (null == pids || pids.isEmpty()) {
			    			 DialogUtil.alertWarning("Задайте планеты на вершине тау-квадрата (planet3)");
			    			 return;
				    	 }

				    	 section.add(Chunk.NEXTPAGE);
			    		 section.add(new Paragraph("Чего вам не хватает:", boldred));
				    	 com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
				    	 list.setNumbered(true);
				    	 ListItem li = new ListItem();
	    				 li.add(new Chunk("Слабое место, требующее основательной проработки: ", font));
				    	 String[] arr = pids.split(",");
				    	 int i = 0;
			    		 for (String pid : arr) {
			    			 Planet planet = event.getPlanets().get(Long.valueOf(pid));
			    			 if (planet != null) {
			    				 li.add(Chunk.NEWLINE);
			    				 String s = term
			    					? planet.getName() + " в " + planet.getHouse().getDesignation()
			    					: planet.getShortName() + " + " + planet.getHouse().getName();
			    				 anchor = new Anchor(s, fonta);
			    				 anchor.setReference("#" + planet.getAnchor());
			    				 li.add(anchor);
			    				 if (++i < arr.length)
			    					 li.add(", ");
			    			 }
			    		 }
	    				 list.add(li);

				    	 Planet planet = event.getPlanets().get(Long.valueOf(pid2));
	    				 li = new ListItem();
	    				 String s = term
	    					? planet.getName() + " в " + planet.getHouse().getDesignation()
	    					: planet.getShortName() + " + " + planet.getHouse().getName();
	    				 anchor = new Anchor(s, fonta);
	    				 anchor.setReference("#" + planet.getAnchor());
	    				 li.add(anchor);
	    				 li.add(new Chunk(" — данный фактор связан с тем, к чему вы будете стремиться и что будете накапливать по мере своей жизни", font));
	    				 list.add(li);

				    	 planet = event.getPlanets().get(Long.valueOf(pid1));
	    				 li = new ListItem();
	    				 s = term
	    					? planet.getName() + " в " + planet.getHouse().getDesignation()
	    					: planet.getShortName() + " + " + planet.getHouse().getName();
	    				 anchor = new Anchor(s, fonta);
	    				 anchor.setReference("#" + planet.getAnchor());
	    				 li.add(anchor);
	    				 String factor = planet.isBad()
							? " — данный фактор указывает на сферу жизни, которая потребует от вас чрезмерной отдачи"
							: " — данный фактор поможет вам направить основной импульс в нужное русло";
	    				 li.add(new Chunk(factor, planet.isBad() ? red : font));
	    				 list.add(li);

	    				 if (event.isHousable()) {
	    					 String hids = obj.getString("houses");
	    					 if (null == hids || hids.isEmpty()) {
	    						 DialogUtil.alertWarning("Задайте вершины пустых домов (houses)");
	    						 return;
	    					 } else {
	    						 li = new ListItem();
	    						 li.add(new Chunk("Сферы жизни, которые изначально будут для вас неважны:", font));
	    						 li.add(Chunk.NEWLINE);
	    						 arr = hids.split(",");
	    						 i = -1;
	    						 s = "";
	    						 for (String hid : arr) {
	    							 House house = event.getHouses().get(Long.valueOf(hid));
	    							 if (house != null) {
	    								 if (++i > 0)
	    									 s += ", ";
	    								 
	    								 s += term ? house.getDesignation() + " дом: " : "";
	    								 s += house.getGeneral();
	    							 }
	    						 }
		    					 li.add(new Chunk(s, bold));
		    					 list.add(li);
	    					 }
	    				 }
			    		 section.add(list);
			    		 section.add(Chunk.NEWLINE);

				    	 // down|top|left|right какое полушарие занимает чаша
			    		 section.add(new Paragraph("Расположение чаши внутри космограммы:", boldgreen));
				    	 try {
				    		 String direction = obj.getString("direction");
				    		 if (null == direction || direction.isEmpty()) {
				    			 DialogUtil.alertWarning("Задайте полушарие чаши East|West|North|South (direction)");
				    			 return;
				    		 } else {
				    			 Halfsphere halfsphere = (Halfsphere)new HalfsphereService().find(direction);
				    			 if (halfsphere != null) {
				    				 p = new Paragraph(PDFUtil.removeTags(halfsphere.getCup(), font));
				    				 p.setSpacingBefore(10);
				    				 section.add(p);
				    			 }
				    		 }
				    	 } catch (JSONException ex) {
				    		 ex.printStackTrace();
				    	 }
			    	 }
			     }

//----------табуретка
			} else if (14 == id) {
			     if (jsonObject != null) {
			    	 JSONObject obj = jsonObject.getJSONObject("cardkind");
			    	 if (obj != null) {
				    	 int pid1 = obj.getInt("planet"); //левая планета
				    	 if (0 == pid1) {
			    			 DialogUtil.alertWarning("Задайте левую ножку табуретки (planet), если рассматривать фигуру ножками вниз");
			    			 return;
				    	 }
				    	 int pid2 = obj.getInt("planet2"); //правая планета
				    	 if (0 == pid2) {
			    			 DialogUtil.alertWarning("Задайте правую правую ножку табуретки (planet2), если рассматривать фигуру ножками вниз");
			    			 return;
				    	 }

			    		 section.add(new Paragraph("Ваша опора в жизни:", bold));
				    	 com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
				    	 int[] arr = new int[] {pid1, pid2};
			    		 for (int pid : arr) {
					    	 ListItem li = new ListItem();
			    			 Planet planet = event.getPlanets().get(Long.valueOf(pid));
			    			 if (planet != null) {
			    				 String s = term
			    					? planet.getName() + " в " + planet.getHouse().getDesignation()
			    					: planet.getShortName() + " + " + planet.getHouse().getName();
			    				 anchor = new Anchor(s, fonta);
			    				 anchor.setReference("#" + planet.getAnchor());
			    				 li.add(anchor);
			    			 }
		    				 list.add(li);
			    		 }
			    		 section.add(list);
			    		 section.add(Chunk.NEWLINE);
			    	 }
			     }

//----------айсберг
			} else if (3 == id) {
			     if (jsonObject != null) {
			    	 JSONObject obj = jsonObject.getJSONObject("cardkind");
			    	 if (obj != null) {
				    	 int pid = obj.getInt("planet"); //планета-ядро
				    	 if (0 == pid) {
			    			 DialogUtil.alertWarning("Задайте планету с наибольшим кармическим статусом (planet)");
			    			 return;
				    	 }
			    		 section.add(new Paragraph("Планета-ядро гороскопа", bold));
				    	 Planet planet = event.getPlanets().get(Long.valueOf(pid));
		    			 if (planet != null) {
				    		 section.add(new Paragraph("Центральной считается планета с наибольшим кармическим статусом. В вашем случае это " + planet.getName(), font));
		    				 Paragraph paragraph = new Paragraph();
		    				 String s = term
		    					? planet.getName() + " в " + planet.getHouse().getDesignation() + " доме"
		    					: planet.getShortName() + " + " + planet.getHouse().getName();
		    				 anchor = new Anchor(s, fonta);
		    				 anchor.setReference("#" + planet.getAnchor());
		    				 paragraph.add(anchor);
		    				 boolean bad = planet.isBad() || planet.isNegative();
		    				 paragraph.add(new Chunk(bad ? " (через эту планету вам предстоит работа над собой)" : " (это планета-поддержка, можете опираться на неё)", bad ? red : green));
		    				 section.add(paragraph);
		    			 }
			    		 section.add(Chunk.NEWLINE);
			    	 }
			     }

//---------сгущение

			} else if (15 == id) {
				if (jsonObject != null) {
					JSONObject obj = jsonObject.getJSONObject("cardkind");
					if (obj != null) {
						if (event.isHousable()) {
							String hids = obj.getString("houses");
							if (null == hids || hids.isEmpty()) {
								DialogUtil.alertWarning("Задайте дома сгущения (houses)");
								return;
							} else {
								com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
								String[] arr = hids.split(",");
								for (String hid : arr) {
									House house = event.getHouses().get(Long.valueOf(hid));
									if (house != null) {
										ListItem li = new ListItem();
										String s = term ? house.getDesignation() + " дом: " : "";
					    				s += house.getName();
					    				anchor = new Anchor(s, fonta);
					    				anchor.setReference("#" + house.getCode());
					    				li.add(anchor);
					    				list.add(li);
					    			 }
					    		 }
					    		 section.add(list);
					    	 }
				    	 } else {
				    		 String pids = obj.getString("planet");
				    		 if (null == pids || pids.isEmpty()) {
				    			 DialogUtil.alertWarning("Задайте планеты сгущения (planet)");
				    			 return;
				    		 } else {
				    			 com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
				    			 String[] arr = pids.split(",");
				    			 for (String pid : arr) {
				    				 Planet planet = event.getPlanets().get(Long.valueOf(pid));
				    				 if (planet != null) {
				    					 ListItem li = new ListItem();
				    					 String s = term ? planet.getName() : planet.getDescription();
				    					 li.add(new Chunk(s, font));
				    					 list.add(li);
				    				 }
				    			 }
				    			 section.add(list);
				    		 }				    		 
				    	 }
				     }
				}

//----------качели

			} else if (7 == id) {
			     if (jsonObject != null) {
			    	 JSONObject obj = jsonObject.getJSONObject("cardkind");
			    	 if (obj != null) {
			    		 if (event.isHousable()) {
			    			 section.add(Chunk.NEXTPAGE);
				    		 section.add(new Paragraph("Главные противоположности вашей жизни:", boldred));
			    			 String hids = obj.getString("houses");
			    			 if (null == hids || hids.isEmpty()) {
			    				 DialogUtil.alertWarning("Задайте дома одной зоны, из которых исходят оппозиции (houses)");
			    				 return;
			    			 } else {
			    				 com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			    				 String[] arr = hids.split(",");
			    				 for (String hid : arr) {
		    						 ListItem li = new ListItem();
			    					 House house = event.getHouses().get(Long.valueOf(hid));
			    					 if (house != null) {
			    						 String s = term ? house.getDesignation() + " дом: " : "";
			    						 s += house.getName();
			    						 anchor = new Anchor(s, fonta);
			    						 anchor.setReference("#" + house.getCode());
			    						 li.add(anchor);
			    						 li.add(" – ");
			    					 }
			    					 long hid2 = house.getOppositeId();
			    					 house = event.getHouses().get(hid2);
			    					 if (house != null) {
			    						 String s = term ? house.getDesignation() + " дом: " : "";
			    						 s += house.getName();
			    						 anchor = new Anchor(s, fonta);
			    						 anchor.setReference("#" + house.getCode());
			    						 li.add(anchor);
			    					 }
			    					 list.add(li);
			    				 }
			    				 section.add(list);
			    			 }

			    			 section.add(Chunk.NEWLINE);
				    		 section.add(new Paragraph("Что изначально будет вне ваших интересов:", bold));
			    			 hids = obj.getString("houses2");
			    			 if (null == hids || hids.isEmpty()) {
			    				 DialogUtil.alertWarning("Задайте вершины пустых домов в обеих пустых зонах (houses2)");
			    				 return;
			    			 } else {
			    				 com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			    				 String[] arr = hids.split(",");
			    				 for (String hid : arr) {
			    					 House house = event.getHouses().get(Long.valueOf(hid));
			    					 if (house != null) {
			    						 ListItem li = new ListItem();
			    						 String s = term ? house.getDesignation() + " дом: " : "";
			    						 s += house.getGeneral();
			    						 li.add(new Chunk(s, font));
				    					 list.add(li);
			    					 }
			    				 }
			    				 section.add(list);
					    		 section.add(new Paragraph("Если в этих сферах имеются надуманные проблемы, то они тоже не дадут вам расслабиться.", red));
			    			 }
			    		 }

		    			 section.add(Chunk.NEWLINE);
			    		 section.add(new Paragraph("Факторы, которые смягчат ситуацию:", boldgreen));
			    		 String pids = obj.get("planet").toString();
			    		 if (null == pids
			    				 || pids.isEmpty()
				    			 || pids.equals("0")) {
			    			 DialogUtil.alertWarning("Задайте через запятую крайние планеты качелей без оппозиций (planet). А если таковых нет, укажите планеты без оппозиций или благополучные планеты гороскопа (Кету и Лилит не в счёт)");
			    			 return;
			    		 } else {
			    			 com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			    			 String[] arr = pids.split(",");
			    			 for (String pid : arr) {
			    				 Planet planet = event.getPlanets().get(Long.valueOf(pid));
			    				 if (planet != null) {
		    						 ListItem li = new ListItem();
				    				 String s = term
				    					? planet.getName() + " в " + planet.getHouse().getDesignation()
				    					: planet.getShortName() + " + " + planet.getHouse().getName();
				    				 anchor = new Anchor(s, fonta);
				    				 anchor.setReference("#" + planet.getAnchor());
				    				 li.add(anchor);
			    					 list.add(li);
			    				 }
			    			 }
			    			 section.add(list);
			    		 }				    		 
			    	 }
			     }
			}

			if (id < 4)
				section.add(Chunk.NEXTPAGE);
			if (kind.getHigh() != null) {
				section.add(Chunk.NEWLINE);
				section.add(new Paragraph("На следующих трёх уровнях отражено направление вашего развития с точки зрения прошлых наработок: "
					+ "три варианта развития внутри одного направления. Определите, на каком уровне вы находитесь. "
					+ "Высокий уровень указывает на то, к чему полезно стремиться:", font));

		        BaseColor bcolor = PDFUtil.BORDERCOLOR;
		        float bwidth = PDFUtil.BORDERWIDTH;
		        float padding = PDFUtil.CELLPADDING;

		        PdfPTable table = new PdfPTable(3);
		        table.setTotalWidth(doc.getPageSize().getWidth() - PDFUtil.PAGEBORDERWIDTH * 2);
		        table.setLockedWidth(true);
		        table.setWidths(new float[] { 33, 33, 33 });
		        table.setSpacingBefore(20);

		        String[] texts = {"Низкий уровень", "Средний уровень", "Высокий уровень"};
		        BaseColor[] colors = {new BaseColor(193, 193, 215), new BaseColor(177, 177, 205), new BaseColor(162, 162, 195)};
		        int i = -1;
		        for (String text : texts) {
			        PdfPCell cell = new PdfPCell(new Phrase(text, bold));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setPadding(padding);
					cell.setBackgroundColor(colors[++i]);
					cell.setUseVariableBorders(true);
					cell.setBorderColor(bcolor);
					cell.setBorderWidth(bwidth);
					table.addCell(cell);
		        }

		        texts = new String[] {kind.getLow(), kind.getMedium(), kind.getHigh()};
		        colors = new BaseColor[] {new BaseColor(240, 240, 245), new BaseColor(224, 224, 235), new BaseColor(208, 208, 225)};
		        i = -1;
		        for (String text : texts) {
			        PdfPCell cell = new PdfPCell(new Phrase(text, font));
					cell.setPadding(padding);
					cell.setBackgroundColor(colors[++i]);
					cell.setUseVariableBorders(true);
					cell.setBorderColor(bcolor);
					cell.setBorderWidth(bwidth);
					table.addCell(cell);
		        }
				section.add(table);
			}
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
			Map<Long, Planet> pmap = event.getPlanets();
			if (pmap != null) {
				EventStatistics stat = new EventStatistics(event);
				stat.getPlanetSigns(false);
				Map<String, Integer> signMap = stat.getSignPlanets();

				String type = "";
				Planet sun = pmap.get(19L);
				Planet moon = pmap.get(20L);
				
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
						Section section = PDFUtil.printSection(chapter, "Самораскрытие", null);
						if (term) {
							section.add(new Paragraph(cardType.getName(), fonth5));
							section.add(new Paragraph(cardType.getDescription(), PDFUtil.getAnnotationFont(true)));
						}
						section.add(new Paragraph(PDFUtil.removeTags(cardType.getText(), font)));
						section.add(Chunk.NEWLINE);
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
			Section section = PDFUtil.printSection(chapter, "Сильные стороны", "planetstrong");
			PlanetTextService service = new PlanetTextService();
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				if (!event.isHousable() && planet.getCode().equals("Moon"))
					continue;

				PlanetText planetText = null;
				if (planet.isSword()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "sword");
					if (planetText != null) {
						section.addSection(new Paragraph((term ? planet.getName() : planet.getGoodName()) + "-меч", fonth5));
						if (term) {
							String s = "Планета-меч (Возничий) идёт впереди Солнца. "
									+ "Меч олицетворяет качества, через которые вы будете пробивать себе дорогу. "
									+ "Это символ уверенности и самоутверждения, через который выразится ваша энергия, воля и сущность.";
							if (!planet.isFictitious())
								s += " Планета-меч не фиктивная, значит, она имеет прямое отношение к профессии, указывая, к чему у вас есть склонность";
							section.add(new Paragraph(s, PDFUtil.getAnnotationFont(true)));
							section.add(Chunk.NEWLINE);
						}
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));

						Rule rule = EventRules.rulePlanetSword(planet, female);
						if (rule != null) {
							section.add(Chunk.NEWLINE);
							section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
						}
						PDFUtil.printGender(section, planetText, female, child, true);
						section.add(Chunk.NEWLINE);

						if (planet.isDamaged() && planetText.getTextDamaged() != null) {
							section.add(Chunk.NEWLINE);
							section.add(new Paragraph(PDFUtil.removeTags(planetText.getTextDamaged(), font)));							
						}
					}
				} else if (planet.isShield()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "shield");
					if (planetText != null) {
						section.addSection(new Paragraph((term ? planet.getName() : planet.getGoodName()) + "-щит", fonth5));
						if (term) {
							Font afont = PDFUtil.getAnnotationFont(true);
							section.add(new Paragraph("Планета-щит (Дорифорий) следует сразу за Солнцем. "
								+ "Она имеет отношение к вашей профессии, указывая направление для самовыражения. "
								+ "Астрологический дом, в котором находится Щит, определяет сферу жизни, которая будет часто выдвигаться на первый план, требуя от вас решения:", afont));

			    			 if (event.isHousable()) {
			    				 House house = planet.getHouse();
			    				 String s = house.getDesignation() + " дом – " + house.getName();
			    				 Anchor anchor = new Anchor(s, fonta);
			    				 anchor.setReference("#" + planet.getAnchor());
			    				 section.add(anchor);
			    			 }
			    			 section.add(new Paragraph("Чем ближе Щит к Солнцу, тем быстрее придётся решать эти вопросы, что добавит вам опыта в реализации солнечного потенциала. "
								+ "Чем дальше Щит от Солнца, тем больше у вас будет времени для согласования вопросов перед тем, как приступить к действию. "
								+ "В прогностике Щит тоже важен: прежде чем сформировать транзит к вашему Солнцу, планетам придётся пройти сквозь систему защиты планеты " + planet.getName(), afont));
							section.add(Chunk.NEWLINE);
						}
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));
						PDFUtil.printGender(section, planetText, female, child, true);
						section.add(Chunk.NEWLINE);

						if (planet.isDamaged() && planetText.getTextDamaged() != null) {
							section.add(Chunk.NEWLINE);
							section.add(new Paragraph(PDFUtil.removeTags(planetText.getTextDamaged(), font)));							
						}
					}
				}
				if (planet.isPerfect() && !planet.isBroken()) {
					if (planet.inMine())
						section.add(new Paragraph("Планета " + planet.getName() + " не вызывает напряжения, так что вы сумеете проработать недостатки, описанные в разделе «" + planet.getShortName() + " в шахте»", fonth5));
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "perfect");
					if (planetText != null) {
						section.addSection(new Paragraph((term ? planet.getName() : planet.getGoodName()) + "-гармония", fonth5));
						if (term) {
							section.add(new Paragraph("Гармоничная планета не имеет негативных аспектов и олицетворяет собой безупречность. "
								+ "Через неё вы всегда сможете пополнить запас сил, поскольку энергия сама будет притекать к вам", PDFUtil.getAnnotationFont(true)));
							section.add(Chunk.NEWLINE);
						}
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));
						PDFUtil.printGender(section, planetText, female, child, true);
						section.add(Chunk.NEWLINE);
					}
				}
				if (planet.isLord() && !planet.isKethued()) {
					section.addSection(new Paragraph((term ? planet.getName() : planet.getGoodName()) + (term ? " – владыка гороскопа" : "-сила"), fonth5));
					if (term) {
						section.add(new Paragraph("Владыка гороскопа – это планета, у которой больше всего сильных аспектов. "
							+ "Через неё будет происходить самый сильный энергообмен. "
							+ "В вашем характере и поведении проявится как позитивная, так и негативная природа планеты", PDFUtil.getAnnotationFont(true)));
						section.add(Chunk.NEWLINE);
					}
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "strong");
					if (planetText != null) {
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));
						PDFUtil.printGender(section, planetText, female, child, true);
						section.add(Chunk.NEWLINE);
					}
				}
				if (planet.isKing() && !planet.isKethued() && !planet.isLilithed()) {
					section.addSection(new Paragraph((term ? planet.getName() : planet.getGoodName()) + (term ? " – король аспектов" : "-позитив"), fonth5));
					if (term) {
						section.add(new Paragraph("Король аспектов – это планета с наибольшим количеством позитивных аспектов. "
							+ "Через неё будет происходить самый позитивный энергообмен", PDFUtil.getAnnotationFont(true)));
						section.add(Chunk.NEWLINE);
					}
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "king");
					if (planetText != null) {
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));
						PDFUtil.printGender(section, planetText, female, child, true);
						section.add(Chunk.NEWLINE);
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
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				if (!event.isHousable() && planet.getCode().equals("Moon"))
					continue;

				if (planet.inMine() || planet.isDamaged())
					weaks.add(planet);
			}
			if (weaks.isEmpty())
				return;

			Section section = PDFUtil.printSection(chapter, "Слабые стороны", "planetweak");
			PlanetTextService service = new PlanetTextService();
			for (Planet planet : weaks) {
				PlanetText planetText = null;

				if (planet.inMine()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "mine");
					if (planetText != null) {
						section.addSection(new Paragraph((term ? planet.getName() : planet.getGoodName()) + " в шахте", fonth5));
						if (term) {
							section.add(new Paragraph("У планеты в шахте нет сильных аспектов, а значит она не сможет участвовать в постоянном энергообмене. "
								+ "От рождения качества этой планеты слаборазвиты и не проявятся в полную силу, "
								+ "но будут время от времени активироваться через дирекции и транзиты. "
								+ "Когда именно это произойдёт, - покажет краткосрочный и долгосрочный прогноз", PDFUtil.getAnnotationFont(true)));
							section.add(Chunk.NEWLINE);
						}
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));

						PlanetService planetService = new PlanetService();
						Sign sign = planet.getSign();
						Planet ruler = planetService.getRuler(sign.getId(), true, false);
						if (ruler != null) {
							PlanetText text = (PlanetText)service.findByPlanet(ruler.getId(), "positive");
							if (text != null) {
								String s = term ? "В этом вам помогут качества созвездия " + sign.getName() + ", в котором находится " + planet.getName() : "В этом вам помогут следующие качества и сферы жизни";
								Paragraph p = new Paragraph(s + ":", new Font(baseFont, 12, Font.BOLD));
								p.setSpacingBefore(10);
								section.add(p);
								section.add(new Paragraph(PDFUtil.html2pdf(text.getText(), font)));
							}
						}
						PDFUtil.printGender(section, planetText, female, child, true);
						section.add(Chunk.NEWLINE);
					}
				} else if (planet.isDamaged()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "damaged");
					if (planetText != null) {
						section.addSection(new Paragraph((term ? planet.getName() : planet.getBadName()) + "-дисгармония", fonth5));
						if (term) {
							section.add(new Paragraph("У поражённой планеты нет благоприятных аспектов, а значит через неё от вас будет утекать больше всего энергии. "
								+ "То, что другим даётся на халяву, вам придётся долго отвоёвывать, бережно хранить и иногда жить напрасной надеждой. "
								+ "Поражённая планета не даёт права на ошибку. "
								+ "Однако при прохождении позитивных дирекций и транзитов по данной планете вы ощутите её положительную природу и сможете наверстать упущенное. "
								+ "Когда именно это произойдёт, - покажет краткосрочный и долгосрочный прогноз", PDFUtil.getAnnotationFont(true)));
							section.add(Chunk.NEWLINE);
						}
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));
						PDFUtil.printGender(section, planetText, female, child, true);
						section.add(Chunk.NEWLINE);
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
			Collection<Planet> planets = event.getPlanets().values();
			//фильтрация списка типов аспектов
			List<Model> types = new AspectTypeService().getList();
			String[] codes = {
				"NEUTRAL", "NEGATIVE", "NEGATIVE_HIDDEN", "POSITIVE", "POSITIVE_HIDDEN", "CREATIVE", "KARMIC", "SPIRITUAL", "PROGRESSIVE"
			};
			List<Bar> items = new ArrayList<Bar>();
			Map<String, Bar[]> pmap = new HashMap<String, Bar[]>();
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

				List<Bar> pitems = new ArrayList<Bar>();
		    	String name = term ? mtype.getName() : mtype.getKeyword();
		    	int value = 0;
		    	for (Planet planet : planets) {
					int val = planet.getAspectCountMap().get(type.getCode());
					value += val;

			    	Bar bar = new Bar();
			    	bar.setName(planet.getName().substring(0, 3));
			    	bar.setValue(val);
					bar.setColor(mtype.getColor());
					bar.setCategory(name);
					pitems.add(bar);
		    	}
		    	pmap.put(name, pitems.toArray(new Bar[] {}));
		    	if (0 == value)
		    		continue;

		    	boolean exists = false;
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
			    	bar.setCode(mtype.getCode());
			    	bar.setValue(value);
					bar.setColor(mtype.getColor());
					bar.setCategory("Аспекты");
					items.add(bar);
		    	}
		    }
		    int size = items.size();
		    Bar[] bars = new Bar[size];
		    Map<String, Double> map = new HashMap<>();
		    for (int i = 0; i < size; i++) {
		    	Bar bar = items.get(i);
		    	bars[i] = bar;
		    	map.put(bar.getCode(), bar.getValue());
		    }
			Section section = PDFUtil.printSection(chapter, "Соотношение аспектов планет", null);
			section.add(PDFUtil.printBars(writer, "Соотношение аспектов планет", null, "Аспекты", "Баллы", bars, 500, 300, false, false, true));

			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
			String text = "";
			if (map.containsKey("PROGRESSIVE") && map.get("PROGRESSIVE") > 10) {
				text = "Количество испытаний велико, значит нужно научиться противодействовать соблазнам, "
					+ "не быть слишком наивным и податливым для искушений, которые духовно ослабят вас и подтолкнут к ошибке";
		        li.add(new Chunk(text, new Font(baseFont, 12, Font.NORMAL, new BaseColor(51, 153, 153))));
		        list.add(li);
			}
			if (map.containsKey("NEGATIVE_HIDDEN") && map.containsKey("NEGATIVE")) {
				if (map.get("NEGATIVE_HIDDEN") > map.get("NEGATIVE")) {
					text = "Стресса у вас больше, чем конфликтов, значит нужно искать разрядку своим негативным эмоциям, "
						+ "рассказывать о своих проблемах людям, которым вы доверяете. Не держите обиды и напряжение в себе";
			        li.add(new Chunk(text, font));
			        list.add(li);
				}
			}
			if (map.containsKey("POSITIVE_HIDDEN") && map.containsKey("POSITIVE")) {
				if (map.get("POSITIVE_HIDDEN") > map.get("POSITIVE")) {
					text = "Скрытого позитива у вас больше, чем лёгкости, значит нужно выражать больше эмоций, "
						+ "не сдерживать радость, делиться своими успехами с любимыми и интересными вам людьми";
					li = new ListItem();
			        li.add(new Chunk(text, new Font(baseFont, 12, Font.NORMAL, BaseColor.RED)));
			        list.add(li);
				}
			}
			if (map.containsKey("POSITIVE_HIDDEN") && map.containsKey("NEGATIVE_HIDDEN")) {
				if (map.get("POSITIVE_HIDDEN") + map.get("NEGATIVE_HIDDEN") < 10) {
					text = "У вас низкий процент скрытых реакций и переживаний, и это хорошо, потому что переживания не засядут глубоко в душе. "
						+ "И горестями и радостями вы будете сразу делиться, что поможет быстрее разрешить конфликт и даст ощущение реализованности";
					li = new ListItem();
			        li.add(new Chunk(text, new Font(baseFont, 12, Font.NORMAL, BaseColor.RED)));
			        list.add(li);
				}
			}
			if (map.containsKey("KARMIC") && map.containsKey("NEGATIVE")) {
				if (map.get("KARMIC") > map.get("NEGATIVE")) {
					Phrase phrase = new Phrase();
					Font lifont = new Font(baseFont, 12, Font.NORMAL, BaseColor.BLUE);
					phrase.add(new Chunk("Воздаяния за ошибки больше, чем конфликтов, значит непоняток в жизни будет больше, чем продуктивных споров. "
						+ "Причины многих неудач таятся в вашем прошлом поведении и мышлении. "
						+ "Испытания даны вам для того, чтобы очиститься от старых грехов и обременяющих установок (в т.ч. чужих), но отыскать и осознать их будет непросто. "
						+ "Подсказкой здесь послужат синие фигуры в разделе ", lifont));
					Anchor anchor = new Anchor("Фигуры гороскопа", fonta);
					anchor.setReference("#aspectconfiguration");
					phrase.add(anchor);
					li = new ListItem();
			        li.add(phrase);
			        list.add(li);
				}
			}
			if (map.containsKey("KARMIC") && map.containsKey("CREATIVE")) {
				text = null;
				BaseColor color = BaseColor.BLUE;
				if (map.get("KARMIC") > map.get("CREATIVE"))
					text = "Воздаяния за ошибки больше, чем свободы, значит вы столкнётесь с ограничениями, "
						+ "и не всегда будет возможность самостоятельно сделать выбор";
				else if (map.get("CREATIVE") > map.get("KARMIC")) {
					text = "Свободы больше, чем воздаяния за ошибки, значит ограничения вам не страшны, "
						+ "и будет возможность самостоятельно сделать выбор";
					color = new BaseColor(0, 102, 51);
				}
				if (text != null) {
					li = new ListItem();
			        li.add(new Chunk(text, new Font(baseFont, 12, Font.NORMAL, color)));
			        list.add(li);
				}
			}

			double hidden = 0, clear = 0;
			if (map.containsKey("NEGATIVE_HIDDEN"))
				hidden += map.get("NEGATIVE_HIDDEN");
			if (map.containsKey("POSITIVE_HIDDEN"))
				hidden += map.get("POSITIVE_HIDDEN");
			if (map.containsKey("NEGATIVE"))
				clear += map.get("NEGATIVE");
			if (map.containsKey("POSITIVE"))
				clear += map.get("POSITIVE");
			if (hidden > clear) {
				text = "Стресса и скрытого позитива больше, чем конфликтов и лёгкости, значит больше активности будет происходить за кулисами жизни, на внутреннем плане";
				li = new ListItem();
		        li.add(new Chunk(text, new Font(baseFont, 12, Font.NORMAL, BaseColor.GRAY)));
		        list.add(li);
			}

			if (map.containsKey("SPIRITUAL")) {
				double val = map.get("SPIRITUAL");
				text = (val > 6)
					? "Благодаря испытаниям прошлой жизни вы достигли высокого уровня духовности, который теперь надо использовать на благо других"
					: "Чем больше духовности – тем более высокого уровня развития вы достигли";
				li = new ListItem();
		        li.add(new Chunk(text, new Font(baseFont, 12, Font.NORMAL, BaseColor.MAGENTA)));
		        list.add(li);
			}
			if (map.containsKey("NEUTRAL") && map.get("NEUTRAL") > 0) {
				text = "Чем больше насыщенности – тем больше изменений будет регулярно происходить в жизни за короткий период времени: события не будут только плохими и хорошими, - будет всё вперемешку";
				li = new ListItem();
		        li.add(new Chunk(text, new Font(baseFont, 12, Font.NORMAL, new BaseColor(255, 153, 51))));
		        list.add(li);
			}

			//определяем тип аспекта с экстремально высоким значением
			String[] exclude = { "NEUTRAL", "SPIRITUAL", "PROGRESSIVE" };
			for (String code : codes) {
				if (!map.containsKey(code))
					continue;
				if (Arrays.asList(exclude).contains(code))
					continue;

				double val = map.get(code);
				double max = 0;
				for (String c : codes) {
					if (c.equals(code))
						continue;
					if (map.containsKey(c)) {
						double v = map.get(c);
						if (v > max)
							max = v;
					}
				}
				if (val >= max) {
					BaseColor color = BaseColor.BLACK;
					Font lifont = new Font(baseFont, 12, Font.NORMAL, color);
					Phrase phrase = new Phrase();
					if (code.equals("KARMIC")) {
						color = BaseColor.BLUE;
						lifont = new Font(baseFont, 12, Font.NORMAL, color);
						phrase.add(new Chunk("Кармические аспекты зашкаливают, значит многое неизбежное, что происходит в вашей жизни, обусловлено прошлыми ошибками и негативной наследственностью. Это отражено в синих фигурах в разделе ", lifont));
						Anchor anchor = new Anchor("Фигуры гороскопа", fonta);
						anchor.setReference("#aspectconfiguration");
						phrase.add(anchor);
						phrase.add(new Chunk(". Чтобы возврат к прошлому не мешал продвижению вперёд, старайтесь вовремя завершать начатое, а не копить проблемы, для решения которых придётся тратить своё драгоценное время", lifont));
					} else if (code.equals("CREATIVE")) {
						color = new BaseColor(0, 102, 51);
						lifont = new Font(baseFont, 12, Font.NORMAL, color);
						text = "Творческие аспекты зашкаливают, так что у вас в распоряжении достаточно свободы и возможности преобразить мир! Это очень редкая комбинация, которая говорит о том, что вы не ограничены в своих проявлениях, сможете жить, действовать и принимать решения независимо от других";
						phrase = new Phrase(text, lifont);
					} else if (code.equals("NEGATIVE")) {
						text = "Уровень конфликтов зашкаливает, значит отток энергии через трудности и выяснение отношений будет довольно сильным. Развивайте силу духа, научитесь управлять рисками и преуменьшать их";
						phrase = new Phrase(text, lifont);
					} else if (code.equals("POSITIVE")) {
						color = BaseColor.RED;
						lifont = new Font(baseFont, 12, Font.NORMAL, color);
						text = "Уровень позитива зашкаливает, значит приток энергии будет довольно сильным. Вы счастливчик!";
						phrase = new Phrase(text, lifont);
					} else if (code.equals("POSITIVE_HIDDEN")) {
						color = new BaseColor(153, 102, 102);
						lifont = new Font(baseFont, 12, Font.NORMAL, color);
						text = "Уровень скрытого позитива зашкаливает, значит внутренняя мотивация очень сильна. Внутри себя вы будете полны энергии, несмотря на внешние обстоятельства и проявления";
						phrase = new Phrase(text, lifont);
					} else if (code.equals("NEGATIVE_HIDDEN")) {
						color = BaseColor.GRAY;
						lifont = new Font(baseFont, 12, Font.NORMAL, color);
						text = "Уровень стресса зашкаливает, значит накоплено много скрытого негатива. Старайтесь не растрачивать энергию на неприятные мысли, не зацикливайтесь на внутренних проблемах, а вытаскивайте их на поверхность и решайте";
						phrase = new Phrase(text, lifont);
					}
					li = new ListItem();
			        li.add(phrase);
			        list.add(li);
				}
			}
			li = new ListItem();
	        li.add(new Chunk("В остальном показатели среднестатистические", PDFUtil.getAnnotationFont(false)));
	        list.add(li);
			section.add(list);
			chapter.add(Chunk.NEXTPAGE);

			section = PDFUtil.printSection(chapter, term ? "Выраженность аспектов по каждой планете" : "Аспекты планет по сферам жизни", null);
			section.add(new Paragraph("Диаграмма показывает, в какой сфере будет больше стресса, лёгкости, свободы, переживаний и испытаний:", font));

		    com.itextpdf.text.Image image = PDFUtil.printMultiStackChart(writer, term ? "Аспекты планет" : "", "Планеты", "Баллы", pmap, 500, 0, true);
			section.add(image);

			section.add(Chunk.NEWLINE);
			list = new com.itextpdf.text.List(false, false, 10);
	    	for (Model model : planets) {
	    		Planet planet = (Planet)model;
	    		li = new ListItem();
	    		li.add(new Chunk(planet.getName() + " – " + planet.getPositive(), font));
	    		list.add(li);
			}
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
	 * @param aspectType код типа аспектов POSITIVE|NEGATIVE
	 * @todo добавлять в толкование любые аспекты между светилами!!
	 */
	private void printAspects(Chapter chapter, Event event, String title, String aspectType) {
		try {
			Section section = PDFUtil.printSection(chapter, title, null);
			Paragraph p = null;
			if (aspectType.equals("NEGATIVE"))
				p = new Paragraph("В данном разделе описаны факторы, через которые вы будете расходовать энергию, "
					+ "а также качества вашей личности, проявляющиеся в критические моменты жизни:", PDFUtil.getDangerFont());
			else
				p = new Paragraph("В данном разделе описаны факторы, которые будут наполнять вас энергией:", PDFUtil.getSuccessFont());
			p.setSpacingAfter(10);
			section.add(p);

			PlanetAspectService service = new PlanetAspectService();
			Map<Long, Planet> planets = event.getPlanets();
			boolean exists = false;
			List<SkyPointAspect> spas = new ArrayList<>();
			boolean housable = event.isHousable();

			//отсутствие аспекта между светилами
			if (housable)
				if (aspectType.equals("NEGATIVE")) {
					Planet sun = planets.get(19L);
					String amoon = sun.getAspectMap().get("Moon");
					if (null == amoon) {
						SkyPointAspect spa = new SkyPointAspect(sun, planets.get(20L), (Aspect)new AspectService().find(46L));
						spa.setTexts(service.finds(spa));
						spas.add(spa);
					}
				}

			for (Planet planet1 : planets.values()) {
				if (!housable && planet1.getCode().equals("Moon"))
					continue;

				List<SkyPointAspect> aspects = planet1.getAspectList();
				if (null == aspects)
					continue;

				for (SkyPointAspect aspect : aspects) {
					if (!planet1.isMain()
							&& !aspect.getAspect().getCode().equals("CONJUNCTION"))
						continue;

					long asplanetid = aspect.getAspect().getPlanetid();
					if (asplanetid > 0 && asplanetid != planet1.getId())
						continue;

					Planet planet2 = (Planet)aspect.getSkyPoint2();
					if (!event.isHousable() && planet2.getCode().equals("Moon"))
						continue;

					if (aspect.getAspect().getPoints() < 2
							&& !(planet1.getCode().equals("Sun")
								&& planet2.getCode().equals("Moon")))
						continue;

					if (planet1.getNumber() > planet2.getNumber())
						continue;

					if (aspect.getAspect().getCode().equals("OPPOSITION")) {
						if (planet1.isKethued()
								&& planet2.getCode().equals("Rakhu"))
							continue;

						if (planet1.isRakhued()
								&& planet2.getCode().equals("Kethu"))
							continue;
					}

					if (aspect.getAspect().getCode().equals("CONJUNCTION")) {
						if (planet1.isLilithed() && planet1.isSelened())
							if (planet2.getCode().equals("Lilith")
									|| planet2.getCode().equals("Selena"))
							continue;
					}

					boolean positive = true;
					List<Model> dicts = service.finds(aspect);
					if (dicts != null && !dicts.isEmpty()) {
						PlanetAspectText dict = (PlanetAspectText)dicts.get(0);
						positive = dict.isPositive();
						aspect.setTexts(dicts);
					}
					if (positive && aspectType.equals("POSITIVE"))
						spas.add(aspect);
					else if (!positive && aspectType.equals("NEGATIVE"))
						spas.add(aspect);
				}
			}
			Font afont = new Font(PDFUtil.getAstroFont(), 14, Font.NORMAL, PDFUtil.FONTCOLORGRAY);
			String[] pnegative = {"Lilith", "Kethu"};
			Font aifont = PDFUtil.getAnnotationFont(true);

			for (SkyPointAspect aspect : spas) {
				List<Model> dicts = aspect.getTexts();
				Planet aspl1 = planets.get(aspect.getSkyPoint1().getId());
				Planet aspl2 = planets.get(aspect.getSkyPoint2().getId());
				AspectType type = aspect.getAspect().getType();

				PlanetAspectText dict = new PlanetAspectText();
				dict.setPlanet1(aspl1);
				dict.setPlanet2(aspl2);
				dict.setType(aspect.getAspect().getType());

				p = new Paragraph("", fonth5);
				if (dict != null) {
    				p.add(new Chunk(dict.getMark(), fonth5));
    				if (term)
						p.add(new Chunk(dict.getPlanet1().getName() + " " + 
							type.getSymbol() + " " + 
							dict.getPlanet2().getName(), fonth5));
    				else {
						boolean bad = type.getPoints() < 0
								|| (type.getCode().equals("NEUTRAL")
									&& (Arrays.asList(pnegative).contains(dict.getPlanet1().getCode())
										|| Arrays.asList(pnegative).contains(dict.getPlanet2().getCode())));
		    				String pname = bad ? dict.getPlanet1().getBadName() : dict.getPlanet1().getGoodName();
		    				String pname2 = bad ? dict.getPlanet2().getBadName() : dict.getPlanet2().getGoodName();

						p.add(new Chunk(pname + " " + type.getSymbol() + " " + pname2, fonth5));
    				}
				}
				section.addSection(p);

				if (term) {
					p = new Paragraph(aspect.getAspect().getName() + " планеты " + aspl1.getName() + " ", aifont);
					p.add(new Chunk(aspl1.getSymbol(), afont));
//		    		if (aspect.getAspect().getCode().equals("CONJUNCTION") || aspect.getAspect().getCode().equals("OPPOSITION"))
//		    			p.add(new Chunk(aspect.getAspect().getSymbol(), afont));
	    			p.add(new Chunk((aspect.getAspect().getCode().equals("CONJUNCTION") ? " с планетой " : " к планете ") + aspl2.getName() + " ", aifont));
		    		p.add(new Chunk(aspl2.getSymbol(), afont));
		    		section.add(p);

					if (aspect.getAspect().getId() != null) {
						Font markFont = PDFUtil.getWarningFont();
						int markPoints = aspect.getMarkPoints();
						if (aspectType.equals("POSITIVE")) {
							if (markPoints < 0)
								markFont = PDFUtil.getSuccessFont();
							else if (markPoints > 0)
								markFont = PDFUtil.getDangerFont();
						} else if (aspectType.equals("NEGATIVE")) {
							if (markPoints <= 0)
								markFont = PDFUtil.getDangerFont();
							else if (markPoints > 0)
								markFont = PDFUtil.getSuccessFont();
						}					
						section.add(new Paragraph(aspect.getMark() + " " + aspect.getMarkDescr(), markFont));
					}
				}

				if (dicts != null) {
					for (Model model : dicts) {
						dict = (PlanetAspectText)model;
						if (dict != null) {
							exists = true;
							section.add(new Paragraph(PDFUtil.removeTags(dict.getText(), font)));
		
							Rule rule = EventRules.rulePlanetAspect(aspect, female);
							if (rule != null) {
			    				section.add(Chunk.NEWLINE);
								section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font))); 
							}
							PDFUtil.printGender(section, dict, female, child, true);
							section.add(Chunk.NEWLINE);
						}
					}
				}
			}
			if (!exists && aspectType.equals("NEGATIVE")) {
				p = new Paragraph("В вашем гороскопе нет резко негативных аспектов, влияющих на вашу личность. "
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
	private void printConfigurations(Document doc, Chapter chapter, Event event) {
		try {
			Map<String, List<AspectConfiguration>> map = new LinkedHashMap<>();
		    List<AspectConfiguration> confs = null;
		    AspectConfigurationService service = new AspectConfigurationService();
			AspectConfiguration conf = null;
			Font bold = new Font(baseFont, 12, Font.BOLD);

//----------стеллиум 0° 0° 0° 0°

			String code = "stellium";
		    Map<String, Integer> signMap = new HashMap<String, Integer>();
		    for (Planet planet : event.getPlanets().values()) {
		    	if (!planet.isMain())
		    		continue;
		    	Object object = signMap.get(planet.getSign().getCode());
		    	int	value = (object != null) ? (Integer)object : 0;
		    	signMap.put(planet.getSign().getCode(), ++value);
		    }
		    Iterator<Map.Entry<String, Integer>> iterator = signMap.entrySet().iterator();
		    SignService signService = new SignService();
		    Sign sign = null;
		    while (iterator.hasNext()) {
		    	Entry<String, Integer> entry2 = iterator.next();
		    	double val = entry2.getValue();
		    	if (val > 2) {
		    		sign = (Sign)signService.find(entry2.getKey());
		    		break;
		    	}
		    }
		    if (sign != null) {
		    	confs = new ArrayList<>();
			    conf = (AspectConfiguration)service.find(code);
			    confs.add(conf);
				map.put(code, confs);
		    }

		    //конфигурации из базы
		    List<EventConfiguration> econfs = new EventConfigurationService().findByEvent(event.getId());
		    for (EventConfiguration econf : econfs) {
		    	AspectConfiguration configuration = econf.getConf();
		    	code = configuration.getCode();
		    	confs = map.get(code);
		    	if (null == confs)
		    		confs = new ArrayList<>();
				confs.add(configuration);
				map.put(code, confs);
		    }

			if (map.size() > 0) {
				Section section = PDFUtil.printSection(chapter, "Фигуры гороскопа", "aspectconfiguration");
			    Paragraph p = new Paragraph("Рисунок вашего гороскопа состоит из геометрических фигур, "
			    	+ "которые отражают взаимосвязи планет между собой. У всех людей они разные. "
			    	+ "Каждая фигура обобщает ваши сильные и слабые стороны, показывает главные источники роста и напряжения", font);
			    p.setSpacingAfter(10);
			    section.add(p);

			    RuleService ruleService = new RuleService();
				PlanetTextService ptservice = new PlanetTextService();
				ElementService elementService = new ElementService();
				CrossService crossService = new CrossService();

			    for (Map.Entry<String, List<AspectConfiguration>> entry : map.entrySet()) {
			    	//заголовок
			    	confs = entry.getValue();
			    	if (null == confs || confs.isEmpty())
			    		continue;

			    	code = entry.getKey();
			    	conf = (AspectConfiguration)service.find(code);
					section.addSection(new Paragraph(conf.getName(), fonth5));
					if (term) {
						String text = "";
						if (!code.equals("stellium")) {
							if (conf.getDegree() != null)
								text += "Конфигурация аспектов: " + conf.getDegree() + ". ";
							if (conf.getElementid() > 0) {
								kz.zvezdochet.bean.Element element = (kz.zvezdochet.bean.Element)elementService.find(conf.getElementid());
								if (element != null)
									text += "Стихия: " + element.getName() + ". ";
							}
						}
						if (conf.getDescription() != null)
							text += conf.getDescription();
	    				section.add(new Paragraph(text, PDFUtil.getAnnotationFont(true)));
					}
					
					//изображения
					Paragraph shapes = new Paragraph();
					String descr = null;
					int j = 0;
					for (AspectConfiguration configuration : confs) {
						++j;
						String shape = configuration.getShape();
						if (shape.equals("triangle")) {
							if (code.equals("triangle")) {
								Planet vertex = configuration.getVertex()[0];
								vertex.setSign(event.getPlanets().get(vertex.getId()).getSign());
								kz.zvezdochet.bean.Element element = vertex.getSign().getElement();
								if (element != null)
									configuration.setElement(element);
							}
							shapes.add(printTriangle(event, configuration));
						} else if (shape.equals("rhombus"))
							shapes.add(printRhombus(event, configuration));
						else if (shape.equals("tetragon"))
							shapes.add(printTetragon(event, configuration));
						else if (shape.equals("pentagon"))
							shapes.add(printPentagon(event, configuration));
						else if (shape.equals("hexagon"))
							shapes.add(printHexagon(event, configuration));
						else if (shape.equals("hexahedron"))
							shapes.add(printHexahedron(event, configuration));
						else if (shape.equals("octagon"))
							shapes.add(printOctagon(event, configuration));
						else if (code.equals("stellium")) {
							com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(configuration.getImageUrl());
							if (image != null) {
								float sidex = 74f;
								float sidey = 71f;
								image.scaleAbsolute(sidex, sidey);
								float x = (doc.right() - doc.left()) / 2 - (sidex / 2);
								image.setIndentationLeft(x);

								Paragraph par = new Paragraph();
								par.add(image);
								shapes.add(par);
							}
						}

				    	//индивидуальное описание
						if (!code.equals("stellium") && !code.equals("necklace")) {
							descr = configuration.getDescription();
							if (descr != null)
								shapes.add(new Paragraph(descr, font));

							//дополнение
							shapes.add(Chunk.NEWLINE);
							if (code.equals("taucross")) {
								shapes.add(new Paragraph("Напряжённые факторы треугольника:", fonth5));

								Planet[] planets = configuration.getVertex();
								for (Planet vertex : planets) {
									PlanetText ptext = (PlanetText)ptservice.findByPlanet(vertex.getId(), configuration.isVertexPositive() ? "positive" : "negative");
									if (ptext != null) {
										if (term)
											shapes.add(new Paragraph(vertex.getName(), bold));
										shapes.add(new Paragraph(PDFUtil.html2pdf(ptext.getText(), font)));
									}
								}
								sign = event.getPlanets().get(planets[0].getId()).getSign();
								Cross cross = (Cross)crossService.find(sign.getCrossId());
								if (cross != null) {
									String str = term ? cross.getName() : "Ваша реакция на указанные факторы";
									shapes.add(new Paragraph(str + ":", bold));
									shapes.add(new Paragraph(PDFUtil.removeTags(cross.getTau(), font)));
									shapes.add(Chunk.NEWLINE);
								}

							} else if (code.equals("triangle")) {
								if (configuration.getElement() != null) {
									if (term)
										shapes.add(new Paragraph(configuration.getElement().getDescription(), fonth5));
									shapes.add(new Paragraph("Качества, благодаря которым вам обеспечена лёгкость и успех:", bold));
									shapes.add(new Paragraph(configuration.getElement().getTriangle(), font));
									shapes.add(Chunk.NEWLINE);
								}

							} else if (code.equals("cross")) {
								Cross cross = (Cross)crossService.find(1L);
								if (cross != null) {
									if (term) {
										String str = "Ваша реакция на проблемные сферы";
										str += " (" + cross.getName() + ")";
										shapes.add(new Paragraph(str + ":", bold));
									}
									shapes.add(new Paragraph(PDFUtil.removeTags(cross.getTau(), font)));
									shapes.add(Chunk.NEWLINE);
								}

							} else if (code.equals("stretcher")) {
								if (!female) {
									Rule rule = (Rule)ruleService.find(101L);
									if (rule != null)
										shapes.add(new Paragraph(PDFUtil.removeTags("Rule" + rule.getText(), font)));
								}

							} else if (code.equals("buoy")) {
								for (Planet planet : configuration.getVertex()) {
									PlanetText ptext = (PlanetText)ptservice.findByPlanet(planet.getId(), configuration.isVertexPositive() ? "positive" : "negative");
									if (ptext != null) {
										String s = configuration.isVertexPositive() ? planet.getPositive() : planet.getNegative();
										if (term)
											s += " (" + ptext.getPlanet().getName() + ")";
										shapes.add(new Paragraph(s + ":", bold));
										shapes.add(new Paragraph(PDFUtil.html2pdf(ptext.getText(), font)));
									}
								}
							}

						//ожерелье
						} else if (code.equals("necklace") && event.isHousable()) {
							shapes.add(new Paragraph("Этапы жизни, которые будут последовательно активироваться под влиянием людей и факторов, указанных на рисунке:", bold));
							com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
							list.setNumbered(true);

							int NECKLACE_STEP = 8;
							long start = (long)configuration.getData();
							long finish = start + NECKLACE_STEP;

							for (long i = start; i <= finish; i++) {
								long hid = (i > 177) ? i - 36 : i; 
								House house = event.getHouses().get((long)hid);
								if (house != null && house.getStage() != null) {
									ListItem li = new ListItem();
									li.add(new Chunk(house.getStage(), font));
									list.add(li);
								}
							}
							shapes.add(list);
							shapes.add(Chunk.NEWLINE);

							int points = configuration.getPoints();
							if (points > 0) {
								shapes.add(new Paragraph("В данный момент вы находитесь на уровне №" + points, PDFUtil.getWarningFont()));
								shapes.add(Chunk.NEWLINE);
							}
							
							descr = configuration.getDescription();
							if (descr != null)
								shapes.add(new Paragraph(descr, font));

							Rule rule = EventRules.ruleConfiguration(configuration);
							if (rule != null) {
			    				section.add(Chunk.NEWLINE);
								section.add(new Paragraph(PDFUtil.removeTags("Rule" + rule.getText(), font)));
							}
							if (confs.size() != j)
								shapes.add(Chunk.NEXTPAGE);
						}
						if (!code.equals("necklace") && confs.size() > 1 && confs.size() != j)
							PDFUtil.printHr(shapes, 1, PDFUtil.FONTCOLORGRAY);
					}
					section.add(shapes);

					//описание из справочника
			    	String text = conf.getText();
					if (code.equals("stellium")) {
						if (sign != null) {
							text = text.replace("{sign}", sign.getName());
							text = text.replace("{merit}", sign.getKeyword());
						}
						section.add(new Paragraph(PDFUtil.removeTags(text, font)));
					} else if (term || null == descr) {
						section.add(new Paragraph("Общее описание фигуры:", bold));
						section.add(new Paragraph(PDFUtil.removeTags(text, font)));
					}

					//несколько однотипных конфигураций
					Paragraph appendix = new Paragraph();
					if (confs.size() > 1) {
						if (code.equals("bisextile")) {
							String ctext = "Треугольников несколько, и это очень хорошо: означает постоянную стимуляцию творчества, подталкивает к активной деятельности, не давая лениться и застаиваться. Вас ожидает относительная тишина и благоденствие в указанных на вершинах сферах, изменение к лучшему, переосмысление";
							appendix.add(new Paragraph(PDFUtil.removeTags(ctext, font)));

						} else if (code.equals("javelin")) {
							String ctext = "Т.к. треугольников несколько, то их напряжение будет раздражать. Научитесь преодолевать эти невидимые внутренние препятствия, не портя себе нервы. Для этого надо регулярно снимать напряжение: не тонуть в нём, а выныривать и возвышаться над суетой. Не ведитесь на внешний негатив и тогда не попадёте в опасную крайность, результатом которой станет нарушение закона и принудительная изоляция  (что характерно для людей, которым нечего терять)";
							appendix.add(new Paragraph(PDFUtil.removeTags(ctext, font)));
						}
					}
					section.add(Chunk.NEWLINE);
					section.add(appendix);
					section.add(Chunk.NEXTPAGE);
			    }
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация диаграмм домов
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param event событие
	 * @param statistics объект статистики события
	 */
	private void printHouses(PdfWriter writer, Chapter chapter, Event event, EventStatistics statistics) {
		try {
			Section section = PDFUtil.printSection(chapter, "Сферы жизни", null);
			section.add(new Paragraph("Сферы жизни отражают ваши врождённые возможности, багаж и опыт, с которым вы пришли в этот мир. "
				+ "Пригодится он вам или нет – покажет время. "
				+ "В любом случае, это отправная точка корабля событий, на котором вы поплывёте по морю жизни и реализуете свою миссию.", font));
			section.add(Chunk.NEWLINE);
			section.add(new Paragraph("Приведённые здесь сферы жизни важны для вас от рождения. "
				+ "Даже если в будущем приоритеты поменяются, указанные в диаграмме факторы сохранят свою силу. "
				+ "Чем длиннее столбик, тем больше мыслей и событий с ним будет связано", font));

			if (term) {
				section.add(Chunk.NEWLINE);
				section.add(new Paragraph("Данный график построен на основе непустых астрологических домов, "
					+ "в которых находятся ваши натальные планеты:", font));
			}
			Map<Long, Double> houses = statistics.getPlanetHouses();

			Bar[] bars = new Bar[houses.size()];
			Iterator<Map.Entry<Long, Double>> iterator = houses.entrySet().iterator();
		    int i = -1;
		    while (iterator.hasNext()) {
		    	Entry<Long, Double> entry = iterator.next();
		    	House h = statistics.getHouse(entry.getKey());
		    	Bar bar = new Bar();
		    	bar.setName(term ? h.getName() : h.getName());
		    	double val = entry.getValue();
		    	bar.setValue(val);
				bar.setColor(h.getColor());
				bar.setCategory("Сферы жизни");
				bars[++i] = bar;
		    }
			section.add(PDFUtil.printBars(writer, "", null, "Сферы жизни", "Баллы", bars, 500, 350, false, false, false));

			//определяем дома с 3+ планетами
			Font green = PDFUtil.getSuccessFont();
			Collection<House> ehouses = event.getHouses().values();
			if (null == ehouses) return;
			List<House> houses3 = new ArrayList<>();
			for (House h : ehouses) {
				if (h.isLilithed() || h.isKethued() || h.isDamaged())
					continue;

				if (h.getPoints() > 2)
					houses3.add(h);
			}
			int cnt = houses3.size();
			if (cnt > 1)
				section.add(new Paragraph("У вас " + cnt + " главных миссии в жизни:", font));

			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			for (House h : houses3) {
				String mission = h.getMission();
				ListItem li = new ListItem();
		        li.add(new Chunk(null == mission ? h.getDescription() : mission, green));
		        list.add(li);
			}
	        section.add(list);
			section.add(Chunk.NEWLINE);

	        Anchor anchor = new Anchor("Реализация личности", fonta);
            anchor.setReference("#planethouses");
			Paragraph p = new Paragraph();
			p.add(new Chunk("Более подробно важные сферы описаны в разделе ", font));
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
		if (!event.isHousable()) return;
		String text = "Этот раздел в меньшей степени рассказывает о вашем характере и в большей степени говорит о том, "
	    	+ "что произойдёт в реальности, и как вы к этому отнесётесь. Здесь описаны важные для вас сферы жизни";
		if (term)
			text += ", основанные на положении планет в астрологических домах и положении вершин домов в знаках Зодиака";
	    Paragraph p = new Paragraph(text, font);
	    p.setSpacingAfter(10);
		chapter.add(p);

		Collection<House> houses = event.getHouses().values();
		if (null == houses) return;
		Map<Long, Planet> eplanets = event.getPlanets();
		Collection<Planet> cplanets = eplanets.values();
		try {
			PlanetHouseService service = new PlanetHouseService();
			HouseSignService hservice = new HouseSignService();
			PlanetHouseRuleService phruleService = new PlanetHouseRuleService();
			HouseSignRuleService hsruleService = new HouseSignRuleService();
			PlanetHousePositionService positionService = new PlanetHousePositionService();

			AspectTypeService atservice = new AspectTypeService();
			AspectType negativeType = (AspectType)atservice.find("NEGATIVE");
			AspectType positiveType = (AspectType)atservice.find("POSITIVE");
			Font fonth6 = PDFUtil.getSubheaderFont();
			Font hafont = PDFUtil.getHeaderAstroFont();
			Font grayfont = PDFUtil.getAnnotationFont(false);

			for (House house : houses) {
				boolean hmain = house.isMain();
				//Определяем количество планет в доме
				List<Planet> planets = new ArrayList<Planet>();
				for (Planet planet : cplanets) {
					if (planet.getCode().equals("Selena")
							&& house.isSelened()
							&& (house.isLilithed()
								|| house.isKethued()))
						continue;

					if (planet.getCode().equals("Rakhu")
							&& house.isRakhued()
							&& house.isLilithed())
						continue;

					if (planet.getHouse().getId().equals(house.getId()))
						planets.add(planet);
				}
				//Создаем информационный блок, только если дом не пуст
				Section section = null;
				if (planets.size() > 0) {
					section = PDFUtil.printSection(chapter, house.getName(), house.getCode());

	    			if (term) {
	    				section.add(new Paragraph("Данный сектор " + house.getDesignation() + " дома отвечает за следующие сферы жизни: " + house.getDescription().toLowerCase(), font));
	    				section.add(Chunk.NEWLINE);
	    			}

					for (Planet planet : planets) {
						boolean negative = planet.isDamaged()
								|| ((planet.getCode().equals("Lilith")
										|| planet.getCode().equals("Kethu"))
									&& !planet.isLord() && !planet.isPerfect());
						String sign = negative ? "-" : "+";

						Phrase ph = new Phrase("", fonth5);
		    			if (term) {
							String mark = planet.getMark("house", term);
							if (mark.length() > 0) {
			    				ph.add(new Chunk(mark + " ", fonth5));
			    				ph.add(new Chunk(planet.getSymbol() + " ", hafont));
							}
							String hname = house.isAngled()
								? " на " + house.getDesignation()
								: " в секторе «" + house.getName() + "» " + house.getDesignation() + " дома";
		    				ph.add(new Chunk(" " + planet.getName() + hname, fonth5));
		    				ph.add(Chunk.NEWLINE);
		    			} else {
		    				ph.add(new Chunk(house.getName() + " " + sign + " " + (negative ? planet.getNegative() : planet.getPositive()), fonth5));
		    			}
		            	Anchor anchorTarget = new Anchor(ph);
		            	anchorTarget.setName(planet.getAnchor());
		            	p = new Paragraph("", fonth5);
		            	p.add(anchorTarget);
		    			section.addSection(p);

		    			if (term) {
		    				PlanetHousePosition position = positionService.find(planet);
		    				if (position != null) {
		    					String s = position.getDescription() + ". " + position.getType().getDescription();
		    					if (planet.isDamaged())
		    						s += ". Поскольку планета " + planet.getName() + " поражена, то это затруднит её проявление";
		    					else if (planet.inMine())
		    						s += ". Поскольку планета " + planet.getName() + " находится в шахте, то это ослабит её проявление";
		    					section.add(new Paragraph(s, grayfont));
		    					section.add(Chunk.NEWLINE);
		    				}
		    			}

						AspectType type = positiveType;
						if (planet.isDamaged() || planet.getCode().equals("Kethu"))
							type = negativeType;
						else if (planet.getCode().equals("Lilith") && !planet.isPerfect())
							type = negativeType;

						PlanetHouseText dict = (PlanetHouseText)service.find(planet, house, type);
						if (dict != null) {
							section.add(new Paragraph(PDFUtil.removeTags(dict.getText(), font)));
	
							List<Rule> rules = EventRules.rulePlanetHouse(planet, house, female);
							if (rules != null && !rules.isEmpty()) {
								for (Rule rule : rules) {
									if (null == rule)
										continue;
									section.add(Chunk.NEWLINE);
									section.add(new Paragraph(PDFUtil.removeTags("Rule" + rule.getText(), font)));
									PDFUtil.printGender(section, rule, female, child, true);
								}
							}
							PDFUtil.printGender(section, dict, female, child, true);

							//правила домов
							List<PlanetHouseRule> houseRules = phruleService.find(planet, house);
							for (PlanetHouseRule rule : houseRules) {
								AspectType aspectType = rule.getAspectType();
								Aspect aspect = rule.getAspect();
								Planet planet2 = rule.getPlanet2();
								House house2 = rule.getHouse2();
								Sign rsign = rule.getSign();
								int owner = rule.getSignOwner();

								if (null == aspectType) {
									//2 планеты в доме с аспектом или без
									if (null == rsign) {
										Planet p2 = eplanets.get(planet2.getId());
										if (house2.getId().equals(p2.getHouse().getId())) {
											section.add(Chunk.NEWLINE);
											String header = house.getName() + " + " + planet2.getShortName();
											section.add(new Paragraph(header, fonth6));
							    			if (term) {
							    				section.add(new Paragraph("Планеты " + planet.getName() + " и " + planet2.getName() +
							    					(house.isAngled() ? " на " + house.getDesignation() :
							    						" в секторе «" + house.getName() + "» " + house.getDesignation() + " дома"), grayfont));
							    			}
											section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
											PDFUtil.printGender(section, rule, female, child, true);
										}											
									} else {
										if (0 == owner) {
											//толкуем знак первой планеты в доме
											if (planet.getSign().getId().equals(rsign.getId())) {
												section.add(Chunk.NEWLINE);
												String header = planet.getShortName() + " + " + rsign.getShortname();
												section.add(new Paragraph(header, fonth6));
												section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
												PDFUtil.printGender(section, rule, female, child, true);
											}
										} else if (1 == owner) {
											//толкуем знак куспида
											if (house.getSign().getId().equals(rsign.getId())) {
												section.add(Chunk.NEWLINE);
												String header = house.getName() + " + " + rsign.getShortname();
												section.add(new Paragraph(header, fonth6));
												section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
												PDFUtil.printGender(section, rule, female, child, true);
											}
										} else {
											//толкуем знак второй планеты
											Planet p2 = eplanets.get(planet2.getId());
											if (p2.getSign().getId().equals(rsign.getId())) {
												section.add(Chunk.NEWLINE);
												String header = planet2.getShortName() + " + " + rsign.getShortname();
												section.add(new Paragraph(header, fonth6));
												section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
												PDFUtil.printGender(section, rule, female, child, true);
											}
										}
									}
								} else if (house2 != null && null == planet2) {
									//аспект планеты в доме с другим куспидом
									List<SkyPointAspect> aspects = planet.getAspectHouseList();
									for (SkyPointAspect spa : aspects) {
										if (aspect != null
												&& !aspect.getId().equals(spa.getAspect().getId()))
											continue;

										if (!aspectType.getId().equals(spa.getAspect().getTypeid()))
											continue;

										SkyPoint sp = spa.getSkyPoint2();
										if (aspectType.getId().equals(spa.getAspect().getTypeid())) {
											if (house2.getId().equals(sp.getId())) {
												section.add(Chunk.NEWLINE);
												boolean negative2 = spa.isNegative();
												String sign2 = negative2 ? "-" : "+";
												String header = (negative2 ? planet.getNegative() : planet.getPositive()) +
													" " + sign2 + " " + 
													house2.getName();
												section.add(new Paragraph(header, fonth6));
												section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
												PDFUtil.printGender(section, rule, female, child, true);												
											}
										}
									}
								} else if (rsign != null && null == planet2 && null == house2 && 2 == owner) {
									//аспект планеты в доме с другим знаком
									List<SkyPointAspect> aspects = planet.getAspectList();
									for (SkyPointAspect spa : aspects) {
										if (aspect != null
												&& !aspect.getId().equals(spa.getAspect().getId()))
											continue;

										if (!aspectType.getId().equals(spa.getAspect().getTypeid()))
											continue;

										SkyPoint sp = spa.getSkyPoint2();
										if (rsign.getId().equals(sp.getSign().getId())) {
											section.add(Chunk.NEWLINE);
											boolean negative2 = spa.isNegative();
											String sign2 = negative2 ? "-" : "+";
											String header = (negative2 ? planet.getNegative() : planet.getPositive()) +
												" " + sign2 + " " + 
												rsign.getName();
											section.add(new Paragraph(header, fonth6));
											section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
											PDFUtil.printGender(section, rule, female, child, true);												
										}
									}
								} else if (rsign != null && null == planet2 && null == house2) {
									//планета в доме в знаке
									if (rsign.getId().equals(planet.getSign().getId())) {
										if (2 == aspectType.getId() && planet.isDamaged()) {
											section.add(Chunk.NEWLINE);
											String header = planet.getNegative() + " - " + rsign.getName();
											section.add(new Paragraph(header, fonth6));
											section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
											PDFUtil.printGender(section, rule, female, child, true);
										}
									}
								} else if (null == rsign && null == planet2 && null == house2) {
									//планета в доме поражённая или не поражённая
									boolean damaged = planet.isDamaged();
									boolean match = (2 == aspectType.getId() && damaged)
										|| (3 == aspectType.getId() && !damaged);
									if (match) {
										section.add(Chunk.NEWLINE);
										String header = damaged
											? planet.getBadName() + "-дисгармония"
											: planet.getGoodName() + "-гармония";
										section.add(new Paragraph(header, fonth6));
										section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));
										PDFUtil.printGender(section, rule, female, child, true);
									}
								} else {
									//толкуем планету в доме с аспектом другой планеты
									List<SkyPointAspect> aspects = planet.getAspectList();
									for (SkyPointAspect spa : aspects) {
										if (aspect != null
												&& !aspect.getId().equals(spa.getAspect().getId()))
											continue;

//										if (null == planet2)
//											System.out.println(rule);

										SkyPoint sp = spa.getSkyPoint2();
										if (aspectType.getId().equals(spa.getAspect().getTypeid())) {
											if (planet2.getId().equals(sp.getId())) {
												if (house2 != null
														&& !house2.getId().equals(sp.getHouse().getId()))
													continue;
	
												section.add(Chunk.NEWLINE);
												boolean negative2 = spa.isNegative();
												String sign2 = negative2 ? "-" : "+";
												String header = house.getName() + " " + 
													sign2 + " " + 
													(negative2 ? planet2.getNegative() : planet2.getPositive());
												section.add(new Paragraph(header, fonth6));
								    			if (term) {
								    				String sector = hmain ? "(" + house.getDesignation() + ")" : house.getDesignation() + " дома";
								    				String s = spa.getAspect().getName();
								    				boolean conj = spa.getAspect().getCode().equals("CONJUNCTION");
								    				if (conj && (null == house2 || house.getId().equals(house2.getId())))
							    						s += " планет " + planet.getName() + " и " + planet2.getName() + " в секторе «" + house.getName() + "» " + sector;
								    				else {
								    					s += " планеты " + planet.getName() + " из сектора «" + house.getName() + "» " + sector;
								    					s += (conj ? " с планетой " : " к планете ") + planet2.getName();
								    					if (house2 != null) {
								    						s += house2.isAngled()
							    								? " на " + house2.getDesignation()
								    							: " в секторе «" + house2.getName() + "» " + house2.getDesignation() + " дома";
								    					}
								    				}
								    				section.add(new Paragraph(s, grayfont));
								    			}
												section.add(new Paragraph(PDFUtil.removeTags(rule.getText(), font)));												
												PDFUtil.printGender(section, rule, female, child, true);
											}
										}
									}
								}
							}
							section.add(Chunk.NEWLINE);
						}
					}
				}
				
				//добавляем информацию о доме в знаке
				if (!house.isExportOnSign())
					continue;

				Sign sign = SkyPoint.getSign(house.getLongitude(), event.getBirthYear());
				HouseSignText dict = (HouseSignText)hservice.find(house, sign);
				if (dict != null) {
					if (null == section)
						section = PDFUtil.printSection(chapter, house.getName(), null);
					if (term) {
						String s = "";
						if (hmain)
							s = "Вершина " + house.getDesignation();
						else
							s = "Куспид сектора «" + house.getName() + "» " + house.getDesignation() + " дома";
						s += " в созвездии " + sign.getName();
						section.addSection(new Paragraph(s, fonth5));
					} else
						section.addSection(new Paragraph(house.getName() + " + " + sign.getShortname(), fonth5));

					if (160 == house.getId()) {
						section.add(new Paragraph("Судьба всегда даёт нам в качестве партнёра человека, который является нашей противоположностью и способен нас дополнять, учить новому и помогать исполнять нашу жизненную миссию. Поэтому вас ждёт встреча с человеком, образ которого описан ниже:", font));
						section.add(Chunk.NEWLINE);
					}
					p = house.getCode().equals("II_2")
						? new Paragraph(PDFUtil.html2pdf(dict.getText(), font))
						: new Paragraph(PDFUtil.removeTags(dict.getText(), font));
					section.add(p);

					Rule rule = EventRules.ruleHouseSign(house, sign, event);
					if (rule != null) {
						section.add(Chunk.NEWLINE);
						section.add(new Paragraph(PDFUtil.removeTags("Rule" + rule.getText(), font)));
					}

					List<HouseSignRule> houseRules = hsruleService.find(house, sign);
					for (HouseSignRule rule2 : houseRules) {
						Planet planet = rule2.getPlanet();
						Sign psign = rule2.getSign2();
						Planet eplanet = eplanets.get(planet.getId());
						if (eplanet.getSign().getId().equals(psign.getId())) {
							section.add(Chunk.NEWLINE);
							String header = planet.getShortName() + " + " + psign.getShortname();
							section.add(new Paragraph(header, fonth6));
							section.add(new Paragraph(PDFUtil.removeTags(rule2.getText(), font)));												
						}
					}
					PDFUtil.printGender(section, dict, female, child, true);
				}
				if (section != null)
					section.add(Chunk.NEWLINE);
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
	 * @param event событие
	 */
	private void printElements(PdfWriter writer, Chapter chapter, EventStatistics statistics, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Темперамент", null);

			Map<String, Double> planetMap = statistics.getPlanetElements();
			Map<String, Double> houseMap = event.isHousable() ? statistics.getHouseElements() : new HashMap<String, Double>();

			TreeSet<String> elements = new TreeSet<String>();
			List<Bar> bars = new ArrayList<Bar>();
			ElementService service = new ElementService();
			for (Map.Entry<String, Double> entry : planetMap.entrySet()) {
				String key = entry.getKey();
				double val = planetMap.get(key);
				if (val > 0)
					elements.add(key);
		    	Bar bar = new Bar();
		    	kz.zvezdochet.bean.Element element = (kz.zvezdochet.bean.Element)service.find(key);
		    	bar.setName(element.getTemperament());
		    	bar.setValue(val * (-1));
		    	bar.setColor(element.getColor());
		    	bar.setCategory("в мыслях");
		    	bars.add(bar);
		    }
		    
			//определение выраженной стихии
		    kz.zvezdochet.bean.Element element = null;
		    List<Model> elist = service.getList(false);
		    for (Model model : elist) {
		    	kz.zvezdochet.bean.Element e = (kz.zvezdochet.bean.Element)model;
		    	String[] codes = e.getCode().split("_");
		    	if (codes.length == elements.size()) {
			    	Arrays.sort(codes);
		    		boolean match = Arrays.equals(codes, elements.toArray());
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
		    		section.add(new Paragraph("Ниболее выражены стихии: " + element.getName() +
		    			" (на основе положения планет в знаках Зодиака и астрологических домах)", PDFUtil.getAnnotationFont(true)));
		    	section.add(new Paragraph(PDFUtil.removeTags(element.getText(), font)));
		    	PDFUtil.printGender(section, element, female, child, true);
		    }

		    section.add(Chunk.NEWLINE);
			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("Категория \"в мыслях\" показывает вашу идеальную модель: "
	        		+ "какой темперамент преобладает в вашем характере, "
					+ "на чём мысленно вы сконцентрированы, какие проявления для вас важны, необходимы и естественны.", font));
	        list.add(li);

	        if (event.isHousable()) {
				li = new ListItem();
		        li.add(new Chunk("Категория \"в поступках\" показывает, "
	        		+ "какой темперамент начинает преобладать в вашем поведении, когда приходит время действовать.", font));
		        list.add(li);
	        }
	        section.add(list);

			for (Map.Entry<String, Double> entry : houseMap.entrySet()) {
				String key = entry.getKey();
		    	Bar bar = new Bar();
		    	element = (kz.zvezdochet.bean.Element)service.find(key);
		    	bar.setName(element.getTemperament());
		    	bar.setValue(houseMap.get(key));
		    	bar.setColor(element.getColor());
		    	bar.setCategory("в поступках");
		    	bars.add(bar);
		    }
		    com.itextpdf.text.Image image = PDFUtil.printStackChart(writer, "Сравнение темпераментов", "Аспекты", "Баллы", bars.toArray(new Bar[bars.size()]), 500, 0, true);
			section.add(image);

			list = new com.itextpdf.text.List(false, false, 10);
			li = new ListItem();
	        li.add(new Chunk("Холерик – быстрый, порывистый, страстный, способный преодолевать значительные трудности, но неуравновешенный, склонный к бурным эмоциям и резким сменам настроения. Чувства возникают быстро и ярко отражаются в речи, жестах и мимике", PDFUtil.getDangerFont()));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Флегматик – медлительный, спокойный, с устойчивыми стремлениями и более или менее постоянным настроением (внешне слабо выражает своё душевное состояние). Тип нервной системы: сильный, уравновешенный, инертный. Хорошая память, высокий интеллект, склонность к продуманным, взвешенным решениям, без риска", PDFUtil.getSuccessFont()));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Сангвиник – живой, подвижный, сравнительно легко переживающий неудачи и неприятности. Мимика разнообразна и богата, темп речи быстрый. Эмоции преимущественно положительные, – быстро возникают и меняются", PDFUtil.getWarningFont()));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Меланхолик – легкоранимый, глубоко переживает даже незначительные неудачи, внешне вяло реагирует на происходящее. Тип нервной системы: высокочувствительный. Тонкая реакция на малейшие оттенки чувств. Переживания глубоки, эмоциональны и очень устойчивы", PDFUtil.getNeutralFont()));
	        list.add(li);
	        section.add(list);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация инь-ян
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param statistics объект статистики
	 * @param event событие
	 */
	private void printYinYang(PdfWriter writer, Chapter chapter, EventStatistics statistics, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Мужское и женское начало", null);
			
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
		    	bar.setCategory("в мыслях");
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
		    		section.add(new Paragraph("Наиболее выражены " + s + " знаки: " + yinyang.getDescription() +
		    			" (на основе положения планет в знаках Зодиака и астрологических домах)", PDFUtil.getAnnotationFont(true)));
		    	}
		    	section.add(new Paragraph(PDFUtil.removeTags(yinyang.getText(), font)));
		    	PDFUtil.printGender(section, yinyang, female, child, true);
		    }

		    section.add(Chunk.NEWLINE);
			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("Категория \"в мыслях\" показывает, насколько вы активны в мыслях и принятии решений наедине с самим собой.", font));
	        list.add(li);

			boolean housable = event.isHousable();
	        if (housable) {
				li = new ListItem();
		        li.add(new Chunk("Категория \"в поступках\" показывает, как меняется ваша активность на событийном уровне, когда приходит время действовать.", font));
		        list.add(li);
	        }
			section.add(list);

			if (housable) {
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
	 * @param event событие
	 */
	private void printHalfSpheres(PdfWriter writer, Chapter chapter, EventStatistics statistics, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Тип личности", null);
			
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
		    		section.add(new Paragraph(sphere.getDescription() +
		    			", на основе положения планет в знаках Зодиака и астрологических домах", PDFUtil.getAnnotationFont(true)));
		    	section.add(new Paragraph(PDFUtil.removeTags(sphere.getText(), font)));
		    	PDFUtil.printGender(section, sphere, female, child, true);
		    }

		    section.add(Chunk.NEWLINE);
			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("Открытость выражается в самоутверждении и сотрудничестве", font));
	        list.add(li);

			li = new ListItem();
	        li.add(new Chunk("Закрытость – в духовности и материализме", font));
	        list.add(li);
			section.add(list);

			if (event.isHousable()) {
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
	 * @param event событие
	 */
	private void printSquares(PdfWriter writer, Chapter chapter, EventStatistics statistics, Map<String, Double> signMap, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Зрелость", null);
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
		    	bar.setCategory("в мыслях");
		    	bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		square = element;
		    	}
		    }

		    if (event.isHousable()) {
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
		    }
		    if (square != null) {
		    	if (term) {
		    		section.add(new Paragraph(square.getName(), fonth5));
		    		section.add(new Paragraph("Наиболее выражен " + square.getDescription() +
		    			" (на основе положения планет в знаках Зодиака и астрологических домах)", PDFUtil.getAnnotationFont(true)));
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
		    image = PDFUtil.printPie(writer, "В каких качествах выражена зрелость мыслей:", bars, 500, 0, false);
			section.add(image);

			//дома
			if (event.isHousable()) {
				Map<Long, Double> houseMap2 = statistics.getMainPlanetHouses(); //TODO найти более оптимальный вариант, мат.формулу
				bars = new Bar[houseMap2.size()];
				Iterator<Map.Entry<Long, Double>> iterator2 = houseMap2.entrySet().iterator();
				i = -1;
				while (iterator2.hasNext()) {
				   	Entry<Long, Double> entry = iterator2.next();
			    	double val = entry.getValue();
			    	if (0 == val)
			    		continue;
				   	Bar bar = new Bar();
					//по индексу трети определяем дом, в котором она находится
				   	House element = event.getHouses().get(entry.getKey());
				   	bar.setName(element.getDiaName());
				   	bar.setValue(val);
				   	bar.setColor(element.getColor());
				   	bars[++i] = bar;
				}
			    image = PDFUtil.printPie(writer, "В каких качествах выражена зрелость поступков:", bars, 500, 0, false);
				section.add(image);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация крестов
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param statistics объект статистики
	 * @param event событие
	 */
	private void printCrosses(PdfWriter writer, Chapter chapter, EventStatistics statistics, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Стратегия", null);
			
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
		    	bar.setCategory("в мыслях");
		    	bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		cross = element;
		    	}
		    }

		    if (event.isHousable()) {
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
		    }
		    if (cross != null) {
		    	if (term) {
		    		section.add(new Paragraph(cross.getName(), fonth5));
		    		section.add(new Paragraph("Ниболее выражены знаки: " + cross.getDescription() +
		    			" – на основе положения планет в знаках Зодиака и астрологических домах", PDFUtil.getAnnotationFont(true)));
		    	}
		    	section.add(new Paragraph(PDFUtil.removeTags(cross.getText(), font)));
		    	PDFUtil.printGender(section, cross, female, child, true);
		    }
		    Paragraph p = new Paragraph("Диаграмма показывает, какой тип стратегии присущ вашим мыслям в состоянии покоя, " +
				"и как эта стратегия меняется, когда приходит время действовать и принимать решения:", font);
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
		    image = PDFUtil.printPie(writer, "В каких качествах выражена стратегия намерений:", bars, 500, 0, false);
			section.add(image);

			//дома
			if (event.isHousable()) {
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
			    image = PDFUtil.printPie(writer, "В каких качествах выражена стратегия действий:", bars, 500, 0, false);
				section.add(image);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация зон
	 * @param writer обработчик генерации документа
	 * @param chapter раздел
	 * @param statistics объект статистики
	 * @param event событие
	 */
	private void printZones(PdfWriter writer, Chapter chapter, EventStatistics statistics, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Развитие духа", null);
			
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
		    	bar.setName(element.getDiaName());
		    	bar.setValue(entry.getValue() * (-1));
		    	bar.setColor(element.getColor());
		    	bar.setCategory("в мыслях");
		    	bars[++i] = bar;
		    	//определяем наиболее выраженный элемент
		    	if (entry.getValue() > score) {
		    		score = entry.getValue();
		    		zone = element;
		    	}
		    }

		    if (event.isHousable()) {
				zoneMap = statistics.getHouseZones();
				iterator = zoneMap.entrySet().iterator();
				i = 2;
			    while (iterator.hasNext()) {
			    	Entry<String, Double> entry = iterator.next();
			    	Bar bar = new Bar();
			    	Zone element = (Zone)service.find(entry.getKey());
			    	bar.setName(element.getDiaName());
			    	bar.setValue(entry.getValue());
			    	bar.setColor(element.getColor());
			    	bar.setCategory("в поступках");
			    	bars[++i] = bar;
			    }
		    }
		    if (zone != null) {
		    	if (term) {
		    		section.add(new Paragraph(zone.getName(), fonth5));
		    		section.add(new Paragraph("Наиболее выражены знаки " + zone.getDescription() +
		    			" – на основе положения планет в знаках Зодиака и астрологических домах", PDFUtil.getAnnotationFont(true)));
		    	}
		    	section.add(new Paragraph(PDFUtil.removeTags(zone.getText(), font)));
		    	PDFUtil.printGender(section, zone, female, child, true);
		    }
		    Paragraph p = new Paragraph("Диаграмма показывает, какие приоритеты вы ставите для своего развития, " +
				"и как на событийном уровне (в действии) они меняются:", font);
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
			Collection<Planet> planets = event.getPlanets().values();
		    Bar[] bars = new Bar[planets.size()];
		    int i = -1;
		    for (Planet planet : planets) {
		    	Bar bar = new Bar();
		    	bar.setName(term ? planet.getName() : planet.getPositive());
		    	bar.setValue(planet.getPoints());
				bar.setColor(planet.getColor());
				bar.setCategory("Планеты");
				bars[++i] = (bar);
		    }
		    String text = term ? "Соотношение силы планет" : "Соотношение силы качеств";
			Section section = PDFUtil.printSection(chapter, text, null);
		    text = term
		    	? "Чем выше значение, тем легче и активнее планета выражает свои качества. "
		    		+ "Сравнение основано на положении планет в знаках Зодиака и астрологических домах, "
		    		+ "а также качестве их аспектов"
		    	: "Чем выше значение, тем легче и активнее проявляются качества. "
		    		+ "Если значение минимальное, это не значит, что качество никак не проявляется. "
		    		+ "Но в соотношении с другими оно слабовыражено, имеет меньшую важность "
		    			+ "или из-за некоторых негативных факторов гороскопа его ценность в итоге нивелировалась";
	    	section.add(new Paragraph(text, font));
			section.add(PDFUtil.printBars(writer, "", null, "Планеты", "Баллы", bars, 500, 500, false, false, false));
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
	        li.add(new Chunk("\u2193 — ослабленная планета, чьё проявление связано с неуверенностью и стрессом", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("R — ретроградная планета, проявление качеств которой неочевидно и связано с необходимостью заново пережить прошлый опыт, вернуться к тому, что не доведено до конца", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("влд — владыка гороскопа, самая сильная планета", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("грм — гармоничная планета, способная преодолеть негатив", font));
	        ilist.add(li);

			li = new ListItem();
	        li.add(new Chunk("изг — планета в изгнании, проявлению качеств которой что-то мешает", font));
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
	        li.add(new Chunk("сбз — планета-источник проблем, соблазнов и бездействия", font));
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
			if (event.getPlanets() != null) {
		    	Sign sign = null;
				Font bold = PDFUtil.getSubheaderFont();

				Section section = PDFUtil.printSection(chapter, "Ваше предназначение", null);
				if (term) {
					section.add(new Paragraph("Минорные натальные планеты характеризуют 5 граней вашей личности:"
						+ "", font));
					com.itextpdf.text.List ilist = new com.itextpdf.text.List(false, false, 10);
					ListItem li = new ListItem();
			        li.add(new Chunk("если все они собраны в одном созвездии, значит вы являетесь типичным представителем своего знака и одинаково проя́вите себя во всех пяти ипостасях;", font));
			        ilist.add(li);

					li = new ListItem();
			        li.add(new Chunk("если личные планеты разбросаны по Зодиаку, то они размоют вашу принадлежность к какому-то одному знаку, потому что в разных сферах жизни вы поведёте себя совершенно по-разному", font));
			        ilist.add(li);
			        section.add(ilist);
			        section.add(Chunk.NEWLINE);
				}

				Map<Long, Planet> planets = event.getPlanets();
				Font gray = new Font(PDFUtil.getAstroFont(), 14, Font.NORMAL, PDFUtil.FONTCOLORGRAY);
				com.itextpdf.text.List ilist = new com.itextpdf.text.List(false, false, 10);
				ListItem li = new ListItem();
		        li.add(new Chunk("Предназначение Духа: ", bold));
				Planet planet = planets.get(19L);
				sign = planet.getSign();
				boolean ophiuchus = sign.getCode().equals("Ophiuchus");
		        li.add(new Chunk(sign.getSlogan(), font));
		        if (term) {
		        	li.add(new Chunk(" – " + planet.getSymbol(), gray));
		        	li.add(new Chunk(" в ", font));
		        	li.add(new Chunk(ophiuchus ? "∞" : sign.getSymbol(), ophiuchus ? font : gray));
		        }
		        ilist.add(li);

				li = new ListItem();
		        li.add(new Chunk("Предназначение Души: ", bold));
				planet = planets.get(20L);
				sign = planet.getSign();
				ophiuchus = sign.getCode().equals("Ophiuchus");
		        li.add(new Chunk(sign.getSlogan(), font));
		        if (term) {
		        	li.add(new Chunk(" – " + planet.getSymbol(), gray));
		        	li.add(new Chunk(" в ", font));
		        	li.add(new Chunk(ophiuchus ? "∞" : sign.getSymbol(), ophiuchus ? font : gray));
		        }
		        ilist.add(li);

				li = new ListItem();
		        li.add(new Chunk("Предназначение Ума: ", bold));
				planet = planets.get(23L);
				sign = planet.getSign();
				ophiuchus = sign.getCode().equals("Ophiuchus");
		        li.add(new Chunk(sign.getSlogan(), font));
		        if (term) {
		        	li.add(new Chunk(" – " + planet.getSymbol(), gray));
		        	li.add(new Chunk(" в ", font));
		        	li.add(new Chunk(ophiuchus ? "∞" : sign.getSymbol(), ophiuchus ? font : gray));
		        }
		        ilist.add(li);

				li = new ListItem();
		        li.add(new Chunk("Предназначение Любви: ", bold));
				planet = planets.get(24L);
				sign = planet.getSign();
				ophiuchus = sign.getCode().equals("Ophiuchus");
		        li.add(new Chunk(sign.getSlogan(), font));
		        if (term) {
		        	li.add(new Chunk(" – " + planet.getSymbol(), gray));
		        	li.add(new Chunk(" в ", font));
		        	li.add(new Chunk(ophiuchus ? "∞" : sign.getSymbol(), ophiuchus ? font : gray));
		        }
		        ilist.add(li);

				li = new ListItem();
		        li.add(new Chunk("Предназначение Силы: ", bold));
				planet = planets.get(25L);
				sign = planet.getSign();
				ophiuchus = sign.getCode().equals("Ophiuchus");
		        li.add(new Chunk(sign.getSlogan(), font));
		        if (term) {
		        	li.add(new Chunk(" – " + planet.getSymbol(), gray));
		        	li.add(new Chunk(" в ", font));
		        	li.add(new Chunk(ophiuchus ? "∞" : sign.getSymbol(), ophiuchus ? font : gray));
		        }
		        ilist.add(li);
		        section.add(ilist);

		        if (sign != null) {
					section = PDFUtil.printSection(chapter, "Символы удачи", null);
					ilist = new com.itextpdf.text.List(false, false, 10);

					li = new ListItem();
			        int number = Numerology.getNumber(event.getBirth());
			        li.add(new Chunk("Число рождения – " + String.valueOf(number) + ": ", bold));
			        Numerology numerology = (Numerology)new NumerologyService().find(number);
			        if (numerology != null && numerology.getDescription() != null)
			        	li.add(PDFUtil.removeTags(numerology.getDescription(), font));
			        ilist.add(li);

					li = new ListItem();
					li.add(new Chunk("Счастливое число: ", bold));
					String text = sign.getNumbers() + ". Согласно Каббале, счастливые числа обладают особенной вибрацией, поэтому на них имеет смысл опираться при выборе даты, длительности или любого предмета, имеющего номер";
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
			Section section = PDFUtil.printSection(chapter, "Лояльность и категоричность", null);

			int loyalty = 0, flatness = 0, loyalty2 = 0, flatness2 = 0;
			Map<Long, Integer> map = new HashMap<Long, Integer>();
			Map<Long, Integer> map2 = new HashMap<Long, Integer>();

			Map<Long, Planet> planets = event.getPlanets();
			boolean housable = event.isHousable();
			for (Planet planet : planets.values()) {
				if (housable) {
					boolean loyal2 = planet.getHouse().getElement().isLoyalty();
			    	if (loyal2)
			    		++loyalty2;
			    	else
			    		++flatness2;
				}
		    	if (!planet.isMain())
					continue;
				boolean loyal = planet.getSign().getElement().isLoyalty();
		    	if (loyal) {
		    		++loyalty;
		    		map.put(planet.getId(), 1);
		    	} else {
		    		++flatness;
		    		map2.put(planet.getId(), -1);
		    	}
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
		    } else {
			    text = "Категоричность – это нежелание адаптироваться, занижать планку и быть снисходительным. ";
			    int d = Math.abs(diff);
		    	if (d > 2)
		    		title = "Категоричный тип";
		    	else
		    		title = "Склонность больше к категоричности, чем к лояльности";
		    }
	    	section.add(new Paragraph(title, fonth5));
	    	if (term) {
	    		String s = (diff > 0)
	    			? "Наиболее выражены стихии Воздуха и Воды"
   	    			: "Наиболее выражены стихии Огня и Земли";
	    		s += " (на основе положения планет в знаках Зодиака и астрологических домах)";
		    	section.add(new Paragraph(s, PDFUtil.getAnnotationFont(true)));
	    	}
	    	section.add(new Paragraph(text, font));

			Bar[] bars = new Bar[4];
	    	Bar bar = new Bar();
	    	bar.setName("Лояльность");
		    bar.setValue(loyalty * (-1));
	    	bar.setCategory("в мыслях");
	    	bars[0] = bar;

	    	bar = new Bar();
	    	bar.setName("Категоричность");
		    bar.setValue(flatness * (-1));
	    	bar.setCategory("в мыслях");
	    	bars[1] = bar;

	    	if (housable) {
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
	    	}
	    	section.add(Chunk.NEWLINE);
			com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
			ListItem li = new ListItem();
	        li.add(new Chunk("Категория \"в мыслях\" показывает вашу привычную модель: насколько лояльно вы относитесь к миру.", font));
	        list.add(li);

	        if (housable) {
				li = new ListItem();
		        li.add(new Chunk("Категория \"в поступках\" показывает, как уровень лояльности меняется на практике, когда приходит время действовать.", font));
		        list.add(li);
	        }
	        section.add(list);
		    com.itextpdf.text.Image image = PDFUtil.printStackChart(writer, "Сравнение лояльности и категоричности", "Аспекты", "Баллы", bars, 500, 0, true);
			section.add(image);

			//лояльность по планетам
			List<Bar> bars2 = new ArrayList<Bar>();
		    for (Map.Entry<Long, Integer> entry : map.entrySet()) {
		    	bar = new Bar();
		    	bar.setName(planets.get(entry.getKey()).getLoyalty());
			    bar.setValue(entry.getValue());
		    	bar.setCategory("лояльны");
		    	bars2.add(bar);
		    }
		    for (Map.Entry<Long, Integer> entry : map2.entrySet()) {
		    	bar = new Bar();
		    	bar.setName(planets.get(entry.getKey()).getLoyalty());
			    bar.setValue(entry.getValue());
		    	bar.setCategory("категоричны");
		    	bars2.add(bar);
		    }

		    if (loyalty > 0 && flatness > 0) {
		    	section.add(Chunk.NEWLINE);
		    	if (OsUtil.getOS().equals(OsUtil.OS.LINUX))
		    		section.add(PDFUtil.printStackChart(writer, "К кому вы лояльны, а к кому категоричны:", "Аспекты", "Баллы", bars2.toArray(new Bar[map.size() * 2]), 500, 0, true));
		    	else {
			    	section.add(new Paragraph("К кому вы лояльны:", font));
		    		section.add(PDFUtil.printTableChart(writer, bars2.toArray(new Bar[map.size() * 2]), "К кому вы лояльны, а к кому категоричны:", false));
		    	}
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Отрисовка треугольной конфигурации
	 * @param event событие
	 * @param conf конфигурация аспектов
	 * @return параграф с инфографикой
	 */
	private Paragraph printTriangle(Event event, AspectConfiguration conf) {
		try {
	        PdfPTable table = new PdfPTable(3);
	        table.setWidthPercentage(100);
	        Font font = PDFUtil.getSmallFont();
	        Color color = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
			if (conf.getCode().equals("triangle")) {
				if (conf.getElement() != null)
					color = conf.getElement().getDimColor();
			} else
				color = conf.getColor();
	        font.setColor(color.getRed(), color.getGreen(), color.getBlue());

	        kz.zvezdochet.bean.Element element = conf.getElement();
	        String[] passive = {"earth", "water"};
	        boolean headOverHeels = (element != null) && Arrays.asList(passive).contains(element.getCode());

	        //вершина
			String text = "";
			String htext = "";
			PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			if (headOverHeels) {
				for (Planet planet : conf.getLeftFoot()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
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
				htext = "";
				for (Planet planet : conf.getVertex()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isVertexPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
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
				htext = "";
				for (Planet planet : conf.getRightFoot()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
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
					String filename = PlatformUtil.getPath(kz.zvezdochet.Activator.PLUGIN_ID, "/icons/conf/" + conf.getElement().getCode() + ".gif").getPath();
					image = com.itextpdf.text.Image.getInstance(filename);
				}
			} else {
				String url = conf.getImageUrl();
				image = (null == url) ? null : com.itextpdf.text.Image.getInstance(conf.getImageUrl());
			}
			cell = (null == image) ? new PdfPCell() : new PdfPCell(image);
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
				htext = "";
				for (Planet planet : conf.getLeftFoot()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
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
				htext = "";
				for (Planet planet : conf.getVertex()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isVertexPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
				}
				Paragraph p = new Paragraph(text, font);
				p.setAlignment(Element.ALIGN_CENTER);
				cell.addElement(p);
			}
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			text = "";
			htext = "";
			if (!headOverHeels) {
				for (Planet planet : conf.getRightFoot()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
				}
				Paragraph p = new Paragraph(text, font);
				cell.addElement(p);
			}
			table.addCell(cell);

			Paragraph paragraph = new Paragraph();
			table.setSpacingBefore(1);
			table.setSpacingAfter(5);
			paragraph.add(table);
			return paragraph;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Отрисовка ромбовидной конфигурации
	 * @param event событие
	 * @param conf конфигурация аспектов
	 * @return параграф с инфографикой
	 */
	private Paragraph printRhombus(Event event, AspectConfiguration conf) {
		try {
	        PdfPTable table = new PdfPTable(3);
	        table.setWidthPercentage(100);
	        Font font = PDFUtil.getSmallFont();
	        Color color = conf.getColor();
	        font.setColor(color.getRed(), color.getGreen(), color.getBlue());

	        //вершина
			PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			String text = "";
			String htext = "";
			for (Planet planet : conf.getVertex()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isVertexPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
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
			htext = "";
			for (Planet planet : conf.getLeftFoot()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			cell = new PdfPCell();
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			String url = conf.getImageUrl();
			if (null == url)
				cell = new PdfPCell();
			else {
				com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(url);
				cell = new PdfPCell(image);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			text = "";
			htext = "";
			for (Planet planet : conf.getRightFoot()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
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
			htext = "";
			for (Planet planet : conf.getBase()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isBasePositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
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
			table.setSpacingBefore(1);
			table.setSpacingAfter(5);
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
	 * @link http://mirkosmosa.ru/lunar-calendar/phase-moon/2019/february/3
	 * @link http://goroskop.org/luna/form.shtml
	 * @link https://www.timeanddate.com/moon/phases/russia/moscow?year=1981
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
					Section section = PDFUtil.printSection(chapter, "Лунный день", null);

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
					if (moonday.isPositive()) {
						p = new Paragraph("Подобную иллюстрацию можно нарисовать и повесить в месте вашего вдохновения", font);
						p.setSpacingBefore(10);
						section.add(p);
					}
				}
			}
			chapter.add(Chunk.NEXTPAGE);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Отрисовка четырёхугольной конфигурации
	 * @param event событие
	 * @param conf конфигурация аспектов
	 * @return параграф с инфографикой
	 */
	private Paragraph printTetragon(Event event, AspectConfiguration conf) {
		try {
	        PdfPTable table = new PdfPTable(3);
	        table.setWidthPercentage(100);
	        Font font = PDFUtil.getSmallFont();
	        Color color = conf.getColor();
	        font.setColor(color.getRed(), color.getGreen(), color.getBlue());

	        //верх
			String text = "";
			String htext = "";
			for (Planet planet : conf.getLeftHand()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isLeftHandPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			Paragraph p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			cell.addElement(p);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			htext = "";
			for (Planet planet : conf.getRightHand()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isRightHandPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			cell.addElement(p);
			table.addCell(cell);

			//изображение
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			String url = conf.getImageUrl();
			if (null == url)
				cell = new PdfPCell();
			else {
				com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(url);
				cell = new PdfPCell(image);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			//низ
			text = "";
			htext = "";
			for (Planet planet : conf.getLeftFoot()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
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
			htext = "";
			for (Planet planet : conf.getRightFoot()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			Paragraph paragraph = new Paragraph();
			table.setSpacingBefore(5);
			table.setSpacingAfter(5);
			paragraph.add(table);
			return paragraph;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Отрисовка пятиугольной конфигурации
	 * @param event событие
	 * @param conf конфигурация аспектов
	 * @return параграф с инфографикой
	 */
	private Paragraph printPentagon(Event event, AspectConfiguration conf) {
		try {
	        PdfPTable table = new PdfPTable(3);
	        table.setWidthPercentage(100);
	        Font font = PDFUtil.getSmallFont();
	        Color color = conf.getColor();
	        font.setColor(color.getRed(), color.getGreen(), color.getBlue());
	        boolean headOverHeels = conf.getCode().equals("necklace");

	        //верх
	        PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			String text = "";
			String htext = "";
			Paragraph p = null;
			cell = new PdfPCell();
			if (!headOverHeels) {
				for (Planet planet : conf.getVertex()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isVertexPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
				}
				p = new Paragraph(text, font);
				p.setAlignment(Element.ALIGN_CENTER);
				cell.addElement(p);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

	        //середина
			text = "";
			htext = "";
			cell = new PdfPCell();
			if (conf.getLeftHand() != null) {
				for (Planet planet : conf.getLeftHand()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isLeftHandPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
				}
				p = new Paragraph(text, font);
				p.setAlignment(Element.ALIGN_RIGHT);
				cell.addElement(p);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			String url = conf.getImageUrl();
			if (null == url)
				cell = new PdfPCell();
			else {
				com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(url);
				cell = new PdfPCell(image);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			text = "";
			htext = "";
			cell = new PdfPCell();
			if (conf.getRightHand() != null) {
				for (Planet planet : conf.getRightHand()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isRightHandPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
				}
				p = new Paragraph(text, font);
				cell.addElement(p);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			//низ
			text = "";
			htext = "";
			cell = new PdfPCell();
			if (conf.getLeftFoot() != null) {
				for (Planet planet : conf.getLeftFoot()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
				}
				p = new Paragraph(text, font);
				p.setAlignment(Element.ALIGN_RIGHT);
				cell.addElement(p);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			htext = "";
			cell = new PdfPCell();
			if (headOverHeels) {
				if (conf.getBase() != null) {
					for (Planet planet : conf.getBase()) {
						if (event.isHousable()) {
							House house = planet.getHouse();
							String hname = term ? house.getDesignation() + " дом" : house.getName();
							htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
						}
						text += (term ? planet.getName() : conf.isBasePositive() ? planet.getPositive() : planet.getNegative()) + htext;
						text += "\n";
					}
					p = new Paragraph(text, font);
					p.setAlignment(Element.ALIGN_CENTER);
					cell.addElement(p);
				}
			}
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			htext = "";
			cell = new PdfPCell();
			if (conf.getRightFoot() != null) {
				for (Planet planet : conf.getRightFoot()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
				}
				p = new Paragraph(text, font);
				cell.addElement(p);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			Paragraph paragraph = new Paragraph();
			table.setSpacingBefore(1);
			table.setSpacingAfter(5);
			paragraph.add(table);
			return paragraph;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Отрисовка шестиугольной конфигурации с вершинами
	 * @param event событие
	 * @param conf конфигурация аспектов
	 * @return параграф с инфографикой
	 */
	private Paragraph printHexagon(Event event, AspectConfiguration conf) {
		try {
	        PdfPTable table = new PdfPTable(3);
	        table.setWidthPercentage(100);
	        Font font = PDFUtil.getSmallFont();
	        Color color = conf.getColor();
	        font.setColor(color.getRed(), color.getGreen(), color.getBlue());

	        //верх
	        PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

	        String text = "";
			String htext = "";
			for (Planet planet : conf.getVertex()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isVertexPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
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

	        //верхняя середина
			text = "";
			htext = "";
			for (Planet planet : conf.getLeftHand()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isLeftHandPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			float fontSize = font.getSize();
			PDFUtil.setCellVertical(cell, fontSize, font.getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize));
			table.addCell(cell);

			String url = conf.getImageUrl();
			if (null == url)
				cell = new PdfPCell();
			else {
				com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(url);
				cell = new PdfPCell(image);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setRowspan(2);
			table.addCell(cell);

			text = "";
			htext = "";
			for (Planet planet : conf.getRightHand()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isRightHandPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			PDFUtil.setCellVertical(cell, fontSize, font.getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize));
			table.addCell(cell);

			//нижняя середина
			text = "";
			htext = "";
			for (Planet planet : conf.getLeftFoot()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			PDFUtil.setCellVertical(cell, fontSize, font.getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize));
			table.addCell(cell);

			text = "";
			htext = "";
			for (Planet planet : conf.getRightFoot()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			PDFUtil.setCellVertical(cell, fontSize, font.getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize));
			table.addCell(cell);

	        //низ
	        cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

	        text = "";
			htext = "";
			for (Planet planet : conf.getBase()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isBasePositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
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
			table.setSpacingBefore(1);
			table.setSpacingAfter(5);
			paragraph.add(table);
			return paragraph;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Отрисовка 9-угольной конфигурации
	 * @param event событие
	 * @param conf конфигурация аспектов
	 * @return параграф с инфографикой
	 */
	private Paragraph printOctagon(Event event, AspectConfiguration conf) {
		try {
	        PdfPTable table = new PdfPTable(5);
	        table.setWidthPercentage(100);
	        Font font = PDFUtil.getSmallFont();
	        Color color = conf.getColor();
	        font.setColor(color.getRed(), color.getGreen(), color.getBlue());

	        //верх
	        PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

	        String text = "";
			String htext = "";
			cell = new PdfPCell();
	        if (conf.getVertex() != null) {
				for (Planet planet : conf.getVertex()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isVertexPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
				}
				Paragraph p = new Paragraph(text, font);
				p.setAlignment(Element.ALIGN_CENTER);
				cell.addElement(p);
	        }
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

	        //второй ярус
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			htext = "";
			cell = new PdfPCell();
			if (conf.getLeftHorn() != null) {
				for (Planet planet : conf.getLeftHorn()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isLeftHornPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
				}
				Paragraph p = new Paragraph(text, font);
				p.setAlignment(Element.ALIGN_RIGHT);
				cell.addElement(p);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

	        cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			htext = "";
			cell = new PdfPCell();
			if (conf.getRightHorn() != null) {
				for (Planet planet : conf.getRightHorn()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isRightHornPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
				}
				Paragraph p = new Paragraph(text, font);
				cell.addElement(p);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

	        cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

	        //середина
			text = "";
			htext = "";
			cell = new PdfPCell();
			float fontSize = font.getSize();
			if (conf.getLeftHand() != null) {
				for (Planet planet : conf.getLeftHand()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isLeftHandPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
				}
				Paragraph p = new Paragraph(text, font);
				p.setAlignment(Element.ALIGN_RIGHT);
				cell.addElement(p);
				PDFUtil.setCellVertical(cell, fontSize, font.getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize));
			}
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

	        cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			String url = conf.getImageUrl();
			if (null == url)
				cell = new PdfPCell();
			else {
				com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(url);
				cell = new PdfPCell(image);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

	        cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			htext = "";
			cell = new PdfPCell();
			if (conf.getRightHand() != null) {
				for (Planet planet : conf.getRightHand()) {
					if (event.isHousable()) {
						House house = planet.getHouse();
						String hname = term ? house.getDesignation() + " дом" : house.getName();
						htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
					}
					text += (term ? planet.getName() : conf.isRightHandPositive() ? planet.getPositive() : planet.getNegative()) + htext;
					text += "\n";
				}
				Paragraph p = new Paragraph(text, font);
				cell.addElement(p);
				PDFUtil.setCellVertical(cell, fontSize, font.getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize));
			}
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			//предпоследний ярус
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			htext = "";
			for (Planet planet : conf.getLeftFoot()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			Paragraph p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			text = "";
			htext = "";
			for (Planet planet : conf.getRightFoot()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

	        //низ
	        cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

	        text = "";
			htext = "";
			for (Planet planet : conf.getBase()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isBasePositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
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

			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			Paragraph paragraph = new Paragraph();
			table.setSpacingBefore(1);
			table.setSpacingAfter(5);
			paragraph.add(table);
			return paragraph;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Генерация нестандартных планет
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printPlanetRetro(Chapter chapter, Event event) {
		try {
			List<Planet> retro = new ArrayList<>();
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				if (!event.isHousable() && planet.getCode().equals("Moon"))
					continue;

				if (planet.isFictitious())
					continue;

				if (planet.isRetrograde())
					retro.add(planet);
			}
			if (retro.isEmpty())
				return;

			Section section = PDFUtil.printSection(chapter, term ? "Ретроградные планеты" : "Нестандартные качества", null);
			if (term) {
				section.add(new Paragraph("Ретроградность не является недостатком, "
					+ "она просто говорит о том, что качества ретро-планеты в вашем гороскопе имеют свои особенности, далёкие от стандартов. "
					+ "Проявление такой планеты не всегда очевидно, потому что она действует не так прямолинейно, как директные планеты. "
					+ "Из-за этого иногда появляется ощущение типа «в моей жизни всё не как у людей…»", PDFUtil.getAnnotationFont(false)));
				section.add(Chunk.NEWLINE);
			}
			PlanetTextService service = new PlanetTextService();
			for (Planet planet : retro) {
				section.addSection(new Paragraph((term ? "ретро-" + planet.getName() : planet.getGoodName()), fonth5));
				PlanetText planetText = (PlanetText)service.findByPlanet(planet.getId(), "retro");
				if (planetText != null && planetText.getText() != null) {
					section.add(new Paragraph(PDFUtil.removeTags(planetText.getText(), font)));
					if (planet.isDamaged() && planetText.getTextDamaged() != null)
						section.add(new Paragraph(PDFUtil.removeTags(planetText.getTextDamaged(), font)));
					PDFUtil.printGender(section, planetText, female, child, true);
					section.add(Chunk.NEWLINE);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Отрисовка шестиугольной конфигурации без вершин
	 * @param event событие
	 * @param conf конфигурация аспектов
	 * @return параграф с инфографикой
	 */
	private Paragraph printHexahedron(Event event, AspectConfiguration conf) {
		try {
	        PdfPTable table = new PdfPTable(3);
	        table.setWidthPercentage(100);
	        Font font = PDFUtil.getSmallFont();
	        Color color = conf.getColor();
	        font.setColor(color.getRed(), color.getGreen(), color.getBlue());

	        //верх
	        String text = "";
			String htext = "";
			for (Planet planet : conf.getLeftHorn()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isLeftHornPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
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
			htext = "";
			for (Planet planet : conf.getRightHorn()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isRightHornPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

	        //середина
			text = "";
			htext = "";
			for (Planet planet : conf.getLeftHand()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isLeftHandPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_RIGHT);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			float fontSize = font.getSize();
			PDFUtil.setCellVertical(cell, fontSize, font.getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize));
			table.addCell(cell);

			String url = conf.getImageUrl();
			if (null == url)
				cell = new PdfPCell();
			else {
				com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(url);
				cell = new PdfPCell(image);
			}
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			text = "";
			htext = "";
			for (Planet planet : conf.getRightHand()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isRightHandPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			PDFUtil.setCellVertical(cell, fontSize, font.getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize));
			table.addCell(cell);

			//низ
			text = "";
			htext = "";
			for (Planet planet : conf.getLeftFoot()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isLeftFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
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
			htext = "";
			for (Planet planet : conf.getRightFoot()) {
				if (event.isHousable()) {
					House house = planet.getHouse();
					String hname = term ? house.getDesignation() + " дом" : house.getName();
					htext = text.contains(hname) ? "" : (term ? " " : "\n") + "(" + hname + ")";
				}
				text += (term ? planet.getName() : conf.isRightFootPositive() ? planet.getPositive() : planet.getNegative()) + htext;
				text += "\n";
			}
			p = new Paragraph(text, font);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			cell.addElement(p);
			table.addCell(cell);

			Paragraph paragraph = new Paragraph();
			paragraph.setSpacingBefore(1);
			paragraph.setSpacingAfter(5);
			paragraph.add(table);
			return paragraph;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
