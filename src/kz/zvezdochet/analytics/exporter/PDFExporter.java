package kz.zvezdochet.analytics.exporter;

import java.io.FileOutputStream;
import java.io.IOException;
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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.ChapterAutoNumber;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
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
	private boolean child = false;
	private boolean female = false;
	private Display display;
	private BaseFont baseFont;
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
		child = event.getAge() < event.MAX_TEEN_AGE;
		female = event.isFemale();

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
				+ "Судьба и так сделает всё, чтобы помочь вам закалить ваш характер", font));

			//космограмма
			printCard(doc, chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//знаменитости
			printCelebrities(chapter, event.getBirth());
			printSimilar(chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//градус рождения
			if (!child)
				printDegree(chapter, event);

			//дома
			EventStatistics statistics = new EventStatistics(event.getConfiguration());
			statistics.initPlanetHouses();
			printHouses(writer, chapter, statistics);
			chapter.add(Chunk.NEXTPAGE);

			//знаки
			Map<String, Double> signMap = statistics.getPlanetSigns(true);
			printSigns(writer, chapter, signMap);
			doc.add(chapter);


			chapter = new ChapterAutoNumber("Характеристика личности");
			chapter.setNumberDepth(0);

			p = new Paragraph();
			PDFUtil.printHeader(p, "Характеристика личности");
			chapter.add(p);

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
			printPlanets(chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//позитивные аспекты
			printAspects(chapter, event, "Позитивные сочетания", "POSITIVE");
			//негативные аспекты
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
			Map<String, Double> houseMap = statistics.getPlanetHouses();
			printPlanetHouses(chapter, event, houseMap);
			doc.add(chapter);

			
			chapter = new ChapterAutoNumber("Диаграммы");
			chapter.setNumberDepth(0);

			p = new Paragraph();
			PDFUtil.printHeader(p, "Диаграммы");
			chapter.add(p);

			//координаты планет
			printCoords(chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//аспекты
			printAspectTypes(writer, chapter, event);
			chapter.add(Chunk.NEXTPAGE);

			//стихии
			statistics.initPlanetDivisions();
			statistics.initHouseDivisions();
			printElements(writer, chapter, event, statistics);
			chapter.add(Chunk.NEXTPAGE);
	
			//инь-ян
			printYinYang(writer, chapter, event, statistics);
			chapter.add(Chunk.NEXTPAGE);
			
			//полусферы
			printHalfSpheres(writer, chapter, event, statistics);
			chapter.add(Chunk.NEXTPAGE);
			
			//квадраты
			printSquares(writer, chapter, event, statistics, signMap);
			chapter.add(Chunk.NEXTPAGE);
			
			//кресты
			printCrosses(writer, chapter, event, statistics);
			chapter.add(Chunk.NEXTPAGE);
			
			//зоны
			printZones(writer, chapter, event, statistics);

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
		    com.itextpdf.text.Image image = PDFUtil.printPie(writer, "Выраженные знаки Зодиака", bars, 0, 0, false);
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
					section.add(new Paragraph(degree.getId() + "° " + degree.getCode(), fonth5));
					section.add(new Paragraph(degree.getDescription(), new Font(baseFont, 12, Font.ITALIC, PDFUtil.FONTCOLORGRAY)));
					section.add(new Paragraph(StringUtil.removeTags(degree.getText()), font));
			    }
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация космограммы
	 * @param event событие
	 * @param cell тег-контейнер для вложенных тегов
	 */
	private void printCard(Document doc, Chapter chapter, Event event) {
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
				    			Section section = PDFUtil.printSection(chapter, category.getName());
				    			section.add(PDFUtil.html2pdf(object.getText()));
				    			PDFUtil.printGender(section, object, female, child);
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

	        String css = "p { font-family: Ubuntu; }";
	        String filename = PDFUtil.FONTDIR + "/" + PDFUtil.FONTFILE;
	        FontFactory.register(filename);
	        BaseFont baseHtmlFont = BaseFont.createFont(filename, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
	        Font htmlfont = new Font(baseHtmlFont, fontsize, Font.NORMAL, BaseColor.BLACK);

	        PdfPTable table = new PdfPTable(5);
	        table.setSpacingBefore(10);
			int i = -1;
			for (Model model : event.getConfiguration().getPlanets()) {
				BaseColor color = (++i % 2 > 0) ? new BaseColor(255, 255, 255) : new BaseColor(230, 230, 250);
				Planet planet = (Planet)model;

				PdfPCell cell = new PdfPCell(new Phrase(CalcUtil.roundTo(planet.getCoord(), 2) + "°", font));
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
				table.addCell(cell);

				Color scolor = planet.getColor();
//				cell = new PdfPCell();
//				cell.addElement(new Phrase(planet.getSymbol(), new Font(baseFont, fontsize, Font.NORMAL, new BaseColor(scolor.getRed(), scolor.getGreen(), scolor.getBlue()))));
//		        cell.setBorder(PdfPCell.NO_BORDER);
//		        cell.setBackgroundColor(color);
//		        table.addCell(cell);

				cell = new PdfPCell();
				cell.addElement(new Phrase(planet.getName(), new Font(baseFont, fontsize, Font.NORMAL, new BaseColor(scolor.getRed(), scolor.getGreen(), scolor.getBlue()))));
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
		        table.addCell(cell);

//				cell = new PdfPCell(new Phrase(planet.getDescription(), font));
//		        cell.setBorder(PdfPCell.NO_BORDER);
//		        cell.setBackgroundColor(color);
//				table.addCell(cell);

				Sign sign = planet.getSign();
				scolor = sign.getColor();
//		        cell = new PdfPCell();
//		        for (Element e : XMLWorkerHelper.parseToElementList(sign.getSymbol(), css))
//		            cell.addElement(new Phrase(e.toString(), new Font(baseHtmlFont, fontsize, Font.NORMAL, new BaseColor(scolor.getRed(), scolor.getGreen(), scolor.getBlue()))));
//		        cell.setBorder(PdfPCell.NO_BORDER);
//		        cell.setBackgroundColor(color);
//				table.addCell(cell);
				
		        cell = new PdfPCell();
				cell.addElement(new Phrase(sign.getName(), new Font(baseFont, fontsize, Font.NORMAL, new BaseColor(scolor.getRed(), scolor.getGreen(), scolor.getBlue()))));
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
				table.addCell(cell);

				House house = planet.getHouse();
				cell = new PdfPCell(new Phrase(CalcUtil.roundTo(house.getCoord(), 2) + "°", font));
		        cell.setBorder(PdfPCell.NO_BORDER);
		        cell.setBackgroundColor(color);
				table.addCell(cell);

//				cell = new PdfPCell();
//				if (house.isMain()) {
//					scolor = house.getColor();
//					cell.addElement(new Phrase(house.getDesignation(), new Font(baseFont, fontsize, Font.NORMAL, new BaseColor(scolor.getRed(), scolor.getGreen(), scolor.getBlue()))));
//				}
//		        cell.setBorder(PdfPCell.NO_BORDER);
//		        cell.setBackgroundColor(color);
//				table.addCell(cell);

				cell = new PdfPCell();
				cell.addElement(new Phrase(house.getShortName(), new Font(baseFont, fontsize, Font.NORMAL, new BaseColor(scolor.getRed(), scolor.getGreen(), scolor.getBlue()))));
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
			section.add(new Paragraph("Вид космограммы — это вид сверху на рисунок карты рождения. "
				+ "Здесь важна общая картина, которая не в деталях, а глобально описывает ваше предназначение и кармический опыт прошлого. "
				+ "Определите, на каком уровне вы находитесь. Отследите по трём уровням своё развитие.", font));

			CardKind kind = (CardKind)new CardKindService().find(3L);
			section.add(new Paragraph(kind.getName(), fonth5));
			String html = kind.getText();
			Phrase phrase = PDFUtil.html2pdf(html);
			section.add(phrase);
			
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
							if (max < value) max = value;
						}
						type = (max > 2) ? "planetary" : "scattered";
					}
				}
			
				if (type.length() > 0) {
				    Model model = new CardTypeService().find(type);
				    if (model != null) {
				    	TextGenderDictionary cardType = (TextGenderDictionary)model;
						Section section = PDFUtil.printSection(chapter, "Самораскрытие");
						section.add(new Paragraph(cardType.getName(), fonth5));
						section.add(new Paragraph(StringUtil.removeTags(cardType.getText()), font));
				    }
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация планет
	 * @param chapter раздел
	 * @param event событие
	 */
	private void printPlanets(Chapter chapter, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Сильные и слабые стороны");
			PlanetTextService service = new PlanetTextService();
			List<Model> planets = event.getConfiguration().getPlanets();
			for (Model model : planets) {
				Planet planet = (Planet)model;
				PlanetText planetText = null;
				if (planet.isSword()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "sword");
					if (planetText != null) {
						section.add(new Paragraph(planet.getShortName() + "-меч", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(planetText.getText()), font));

						Rule rule = EventRules.rulePlanetSword(planet);
						if (rule != null)
							section.add(PDFUtil.html2pdf(rule.getText()));

						PDFUtil.printGender(section, planetText, female, child);
					}
				} else if (planet.isShield()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "shield");
					if (planetText != null) {
						section.add(new Paragraph(planet.getShortName() + "-щит", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(planetText.getText()), font));
						PDFUtil.printGender(section, planetText, female, child);
					}
				}
				if (planet.inMine()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "mine");
					if (planetText != null) {
						section.add(new Paragraph(planet.getShortName() + " в шахте", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(planetText.getText()), font));

						PlanetService planetService = new PlanetService();
						Planet ruler = planetService.getRuler(planet.getSign(), true);
						if (ruler != null) {
							planetText = (PlanetText)service.findByPlanet(ruler.getId(), "positive");
							if (planetText != null) {
								section.add(new Paragraph("В этом вам помогут следующие сферы жизни:", font));
								section.add(PDFUtil.html2pdf(planetText.getText()));
							}
						}
						PDFUtil.printGender(section, planetText, female, child);
					}
				}
				if (planet.isDamaged()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "damaged");
					if (planetText != null) {
						section.add(new Paragraph(planet.getShortName() + "-дисгармония", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(planetText.getText()), font));
						PDFUtil.printGender(section, planetText, female, child);
					}
				} else if (planet.isPerfect()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "perfect");
					if (planetText != null) {
						section.add(new Paragraph(planet.getShortName() + "-гармония", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(planetText.getText()), font));
						PDFUtil.printGender(section, planetText, female, child);
					}
				}
				if (planet.isRetrograde()) {
					planetText = (PlanetText)service.findByPlanet(planet.getId(), "retro");
					if (planetText != null) {
						section.add(new Paragraph(planet.getShortName() + "-ретроград", fonth5));
						section.add(new Paragraph(StringUtil.removeTags(planetText.getText()), font));
						PDFUtil.printGender(section, planetText, female, child);
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

		    com.itextpdf.text.Image image = PDFUtil.printBars(writer, "Соотношение аспектов планет", "Аспекты", "Баллы", bars, 500, 300, false);
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
	        li.add(new Chunk("Больше нейтральных — больше проявлений реализуются в симбиозе, а не поодиночке", new Font(baseFont, 12, Font.NORMAL, new BaseColor(255, 153, 51))));
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
			PlanetAspectService service = new PlanetAspectService();
			List<SkyPointAspect> aspects = event.getConfiguration().getAspects();
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
						&& (planet1.isLilithed() || planet2.isLilithed()))
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
					List<Model> dicts = service.finds(planet1, planet2, aspect.getAspect());
					for (Model model : dicts) {
						PlanetAspectText dict = (PlanetAspectText)model;
						if (dict != null) {
							section.add(new Paragraph(dict.getPlanet1().getShortName() + " " + 
								type.getSymbol() + " " + 
								dict.getPlanet2().getShortName(), fonth5));
							section.add(new Paragraph(StringUtil.removeTags(dict.getText()), font));

							Rule rule = EventRules.rulePlanetAspect(aspect);
							if (rule != null)
								section.add(PDFUtil.html2pdf(rule.getText()));

							PDFUtil.printGender(section, dict, female, child);
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
			Section section = PDFUtil.printSection(chapter, "Комплексный анализ личности");

			List<Model> confs = new AspectConfigurationService().getList();
			String[] codes = {
				"stellium",		//0° 0° 0° 0°
//				"semivehicle",	//60° 180° 120°
				"cross",		//90° 90° 90° 90°
//				"taucross",		//90° 180° 90°
				"dagger",		//135° 45° 45° 135°
				"poleaxe",		//135° 90° 135°
				"javelin",		//45° 90° 45°
				"davidstar",	//60° 60° 60° 60° 60° 60°
				"trapezoid",	//60° 60° 60° 180°
//				"sail",			//120° 60° 60° 120°
				"triangle",		//120° 120° 120°
				"bisextile",	//60° 120° 60°
				"boomerang",	//150° 30° 30° 150°
//				"pitchfork",	//150° 60° 150°
//				"vehicle",		//60° 120° 60° 120°
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

			for (Model model : confs) {
				AspectConfiguration conf = (AspectConfiguration)model;
				String code = conf.getCode();
		    	if (Arrays.asList(codes).contains(code))
		    		continue;

				String filename = PlatformUtil.getPath(Activator.PLUGIN_ID, "/icons/conf/" + conf.getCode() + ".gif").getPath();
				com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(filename);
				section.add(image);

				section.add(new Paragraph(conf.getName(), fonth5));
				section.add(new Paragraph(StringUtil.removeTags(conf.getText()), font));

				if (code.equals("semivehicle")) {
					com.itextpdf.text.List list = new com.itextpdf.text.List(false, false, 10);
					ListItem li = new ListItem();
			        li.add(new Chunk("11 лет - становление мужчины, зрелость, осознание необходимости следовать правилам и дисциплине", font));
				    list.add(li);

				    li = new ListItem();
			        li.add(new Chunk("16 лет - материальная обеспеченность - главный жизненный фактор", font));
				    list.add(li);

				    li = new ListItem();
			        li.add(new Chunk("17 лет - раскрытие чувств, появление любви и важной женщины в жизни", font));
				    list.add(li);

				    li = new ListItem();
			        li.add(new Chunk("29 лет - физические данные играют важную роль в жизни, появляются новые привычки", font));
				    list.add(li);

				    li = new ListItem();
				    li.add(new Chunk("42 года - собственные возможности и способности в приоритете", font));
				    list.add(li);

				    li = new ListItem();
				    li.add(new Chunk("55 лет - время личных перемен, изменение модели поведения", font));
				    list.add(li);
				    section.add(list);

				} else if (code.equals("taucross")) {
					text = (PlanetText)service.findByPlanet(20L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(27L, "negative");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					Cross cross = (Cross)new CrossService().find(1L);
					if (cross != null) {
						section.add(new Paragraph(cross.getName(), fonth5));
						section.add(new Paragraph(StringUtil.removeTags(cross.getTau()), font));
					}

				} else if (code.equals("sail")) {
					text = (PlanetText)service.findByPlanet(21L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

				} else if (code.equals("pitchfork")) {
					text = (PlanetText)service.findByPlanet(28L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

				} else if (code.equals("vehicle")) {
					text = (PlanetText)service.findByPlanet(25L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(29L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));

					text = (PlanetText)service.findByPlanet(32L, "positive");
					if (text != null)
						section.add(PDFUtil.html2pdf(text.getText()));
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
				+ "Пригодится он вам или нет - покажет время. "
				+ "В любом случае, это отправная точка корабля событий, на котором вы поплывёте по морю жизни и реализуете свою миссию", font));
			
			Map<String, Double> houses = statistics.getPlanetHouses();
			Bar[] bars = new Bar[houses.size()];
			Iterator<Map.Entry<String, Double>> iterator = houses.entrySet().iterator();
	    	int i = -1;
	    	double maxval = 0;
		    while (iterator.hasNext()) {
		    	Entry<String, Double> entry = iterator.next();
		    	double val = entry.getValue();
		    	if (val > maxval)
		    		maxval = val;

		    	Bar bar = new Bar();
		    	House house = statistics.getHouse(entry.getKey());
		    	bar.setName(house.getShortName());
		    	bar.setValue(val);
		    	bar.setColor(house.getColor());
		    	bars[++i] = bar;
		    }
		    PdfPTable table = PDFUtil.printTableChart(writer, maxval, bars, "Сферы жизни");
			section.add(table);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Генерация планет в домах и домов в знаках
	 * @param chapter раздел
	 * @param event событие
	 * @param houseMap карта домов
	 */
	private void printPlanetHouses(Chapter chapter, Event event, Map<String, Double> houseMap) {
		List<Model> houses = event.getConfiguration().getHouses();
		List<Model> cplanets = event.getConfiguration().getPlanets();
		if (null == houses) return;
		try {
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
					section = PDFUtil.printSection(chapter, house.getHeaderName());
			
					PlanetHouseService service = new PlanetHouseService();
					for (Planet planet : planets) {
						PlanetHouseText dict = (PlanetHouseText)service.find(planet, house, null);
						if (dict != null) {
							String sign = planet.isDamaged() || planet.isLilithed() ? "-" : "+";
							section.add(new Paragraph(planet.getShortName() + " " + sign + " " + house.getShortName(), fonth5));
							section.add(new Paragraph(StringUtil.removeTags(dict.getText()), font));
							PDFUtil.printGender(section, dict, female, child);
						}
					}
				}

				//добавляем информацию о доме в знаке
				if (!house.isExportOnSign())
					continue;

				Sign sign = SkyPoint.getSign(house.getCoord(), event.getBirthYear());
				HouseSignText dict = (HouseSignText)new HouseSignService().find(house, sign);
				if (dict != null) {
					if (null == section)
						section = PDFUtil.printSection(chapter, house.getHeaderName());
					section.add(new Paragraph(house.getShortName() + " + " + sign.getDescription(), fonth5));
					section.add(new Paragraph(StringUtil.removeTags(dict.getText()), font));
					PDFUtil.printGender(section, dict, female, child);
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
	 * @param event событие
	 * @param statistics объект статистики
	 */
	private void printElements(PdfWriter writer, Chapter chapter, Event event, EventStatistics statistics) {
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
		    kz.zvezdochet.bean.Element element = null;
		    for (Model model : service.getList()) {
		    	element = (kz.zvezdochet.bean.Element)model;
		    	String[] codes = element.getCode().split("_");
		    	if (codes.length == elements.length) {
		    		boolean match = true;
		    		for (String code : codes)
		    			if (!Arrays.asList(elements).contains(code)) {
		    				match = false;
		    				break;
		    			}
		    		if (match)
		    			break;
		    		else
		    			continue;
		    	}
		    }
		    if (element != null) {
		    	section.add(new Paragraph(element.getName() + " (" + element.getTemperament() + ")", fonth5));
		    	section.add(new Paragraph(StringUtil.removeTags(element.getText()), font));
		    	PDFUtil.printGender(section, element, female, child);
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
	 * @param event событие
	 * @param statistics объект статистики
	 */
	private void printYinYang(PdfWriter writer, Chapter chapter, Event event, EventStatistics statistics) {
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
		    	section.add(new Paragraph(yinyang.getDescription(), fonth5));
		    	section.add(new Paragraph(StringUtil.removeTags(yinyang.getText()), font));
		    	PDFUtil.printGender(section, yinyang, female, child);
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
	 * @param event событие
	 * @param statistics объект статистики
	 */
	private void printHalfSpheres(PdfWriter writer, Chapter chapter, Event event, EventStatistics statistics) {
		try {
			Section section = PDFUtil.printSection(chapter, "Экстраверт и интроверт");
			
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
		    	section.add(new Paragraph(sphere.getDescription(), new Font(baseFont, 12, Font.ITALIC, PDFUtil.FONTCOLORGRAY)));
		    	section.add(new Paragraph(StringUtil.removeTags(sphere.getText()), font));
		    	PDFUtil.printGender(section, sphere, female, child);
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
	 * @param event событие
	 * @param statistics объект статистики
	 * @param signMap карта знаков Зодиака
	 */
	private void printSquares(PdfWriter writer, Chapter chapter, Event event, EventStatistics statistics, Map<String, Double> signMap) {
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
		    	section.add(new Paragraph(square.getDescription(), fonth5));
		    	section.add(new Paragraph(StringUtil.removeTags(square.getText()), font));
		    	PDFUtil.printGender(section, square, female, child);
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
	 * @param event событие
	 * @param statistics объект статистики
	 */
	private void printCrosses(PdfWriter writer, Chapter chapter, Event event, EventStatistics statistics) {
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
		    	section.add(new Paragraph(cross.getName() + ": " + cross.getDescription(), fonth5));
		    	section.add(new Paragraph(StringUtil.removeTags(cross.getText()), font));
		    	PDFUtil.printGender(section, cross, female, child);
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
	 * @param event событие
	 * @param statistics объект статистики
	 */
	private void printZones(PdfWriter writer, Chapter chapter, Event event, EventStatistics statistics) {
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
		    	section.add(new Paragraph(zone.getDescription(), fonth5));
		    	section.add(new Paragraph(StringUtil.removeTags(zone.getText()), font));
		    	PDFUtil.printGender(section, zone, female, child);
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
}
