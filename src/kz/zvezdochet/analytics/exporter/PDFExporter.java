package kz.zvezdochet.analytics.exporter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.MessageDialog;
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
import kz.zvezdochet.analytics.bean.CardKind;
import kz.zvezdochet.analytics.bean.Category;
import kz.zvezdochet.analytics.bean.Degree;
import kz.zvezdochet.analytics.bean.PlanetSignText;
import kz.zvezdochet.analytics.service.CardKindService;
import kz.zvezdochet.analytics.service.DegreeService;
import kz.zvezdochet.analytics.service.PlanetSignService;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Place;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.bean.TextGender;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.core.util.PlatformUtil;
import kz.zvezdochet.core.util.StringUtil;
import kz.zvezdochet.export.bean.Bar;
import kz.zvezdochet.export.handler.PageEventHandler;
import kz.zvezdochet.export.util.PDFUtil;
import kz.zvezdochet.service.EventService;
import kz.zvezdochet.service.SignService;
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
	private Display display;
	private BaseFont baseFont;
	private Font font, fonta, fonth5;

	public PDFExporter(Display display) {
		this.display = display;
		try {
			baseFont = PDFUtil.getBaseFont();
			font = PDFUtil.getRegularFont(baseFont);
			fonta = PDFUtil.getLinkFont(baseFont);
			fonth5 = PDFUtil.getHeaderFont(baseFont);
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
		saveCard(event);
		try {
			Document doc = new Document();
			String filename = PlatformUtil.getPath(Activator.PLUGIN_ID, "/out/horoscope.pdf").getPath();
			PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(filename));
	        writer.setPageEvent(new PageEventHandler(doc));
	        doc.open();

	        //metadata
	        PDFUtil.getMetaData(doc, "Индивидуальный гороскоп");

	        //раздел
			Chapter chapter = new ChapterAutoNumber("Общая информация");
			chapter.setNumberDepth(0);

			//дата события
			Paragraph p = new Paragraph();
			Place place = event.getPlace();
			if (null == place)
				place = new Place().getDefault();
			String text = DateUtil.fulldtf.format(event.getBirth()) +
				" " + (event.getZone() >= 0 ? "UTC+" : "") + event.getZone() +
				" " + (event.getDst() >= 0 ? "DST+" : "") + event.getDst() + 
				" " + place.getName() +
				" " + place.getLatitude() + "°" +
				", " + place.getLongitude() + "°";
			PDFUtil.printHeader(p, text, baseFont);
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

			//знаки
			EventStatistics statistics = new EventStatistics(event.getConfiguration());
			Map<String, Double> signMap = statistics.getPlanetSigns(true);
			printSigns(writer, chapter, signMap);
			statistics.initPlanetHouses();
			doc.add(chapter);


			chapter = new ChapterAutoNumber("Характеристика личности");
			chapter.setNumberDepth(0);

			//планеты в знаках
			printPlanetSign(chapter, event);
			doc.add(chapter);


			chapter = new ChapterAutoNumber("Анализ карты рождения");
			chapter.setNumberDepth(0);

			//вид космограммы
			printCardKind(doc, writer, chapter, event);
			doc.add(chapter);

			//тип космограммы
//			Map<String, Integer> signPlanetsMap = statistics.getSignPlanets();
//			generateCardType(event, table, signPlanetsMap);
//			
//			//планеты
//			generatePlanets(event, table);
//			
//			//аспекты
//			generateAspectTypes(event, table);
//			//позитивные аспекты
//			generateAspects(event, table, "Позитивные сочетания", "POSITIVE");
//			//негативные аспекты
//			generateAspects(event, table, "Негативные сочетания", "NEGATIVE");
//			//конфигурации аспектов
//			generateAspectConfigurations(event, table);
//
//			//дома
//			generateHouseChart(statistics, table);
//
//			//планеты в домах
//			Map<String, Double> houseMap = statistics.getPlanetHouses();
//			generatePlanetInHouses(event, table, houseMap);
//
//			//стихии
//			statistics.initPlanetDivisions();
//			statistics.initHouseDivisions();
//			generateElements(event, table, statistics);
//	
//			//инь-ян
//			generateYinYang(event, table, statistics);
//			
//			//полусферы
//			generateHalfSpheres(event, table, statistics);
//			
//			//квадраты
//			generateSquares(event, table, statistics, signMap);
//			
//			//кресты
//			generateCrosses(event, table, statistics);
//			
//			//зоны
//			generateZones(event, table, statistics);

			
			
			
			
			
			chapter = new ChapterAutoNumber("Диаграммы");
			chapter.setNumberDepth(0);

			//координаты планет
			printCoords(chapter, event);
			doc.add(chapter);

			
	        doc.add(PDFUtil.printCopyright(baseFont));
	        doc.close();			
		} catch(Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Ошибка", e.getMessage());
			e.printStackTrace();
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
        int n = reader.getNumberOfPages();
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
				Section section = PDFUtil.printSection(chapter, "Однодневки", baseFont);
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
				Section section = PDFUtil.printSection(chapter, "Близкие по духу", baseFont);
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
			Section section = PDFUtil.printSection(chapter, "Знаки Зодиака", baseFont);

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
			section = PDFUtil.printSection(chapter, "Кредо вашей жизни", baseFont);
		    PdfPTable table = PDFUtil.printTableChart(writer, maxval, bars2, "Кредо вашей жизни", baseFont);
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
			Section section = PDFUtil.printSection(chapter, "Символ рождения", baseFont);

			if (event.getConfiguration().getHouses() != null &&
					event.getConfiguration().getHouses().size() > 0) {
				House house = (House)event.getConfiguration().getHouses().get(0);
				if (null == house) return;
				int value = (int)house.getCoord();
				Model model = new DegreeService().find(new Long(String.valueOf(value)));
			    if (model != null) {
			    	Degree degree = (Degree)model;
					section.add(new Paragraph(degree.getId() + "° " + degree.getCode(), fonth5));
					section.add(new Paragraph(degree.getDescription(), new Font(baseFont, 12, Font.ITALIC, new BaseColor(102, 102, 102))));
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
			Section section = PDFUtil.printSection(chapter, "Карта рождения", baseFont);

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
				    			Section section = PDFUtil.printSection(chapter, category.getName(), baseFont);
				    			section.add(new Paragraph(StringUtil.removeTags(object.getText()), font));
								
								List<TextGender> genders = object.getGenderTexts(event.isFemale(), child);
								for (TextGender gender : genders) {
									Paragraph p = new Paragraph(PDFUtil.getGenderHeader(gender.getType()), fonth5);
									p.setSpacingBefore(10);
									section.add(p);
									section.add(new Paragraph(StringUtil.removeTags(gender.getText()), font));
								};
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
			Section section = PDFUtil.printSection(chapter, "Координаты планет", baseFont);
			float fontsize = 8;
			Font font = new Font(baseFont, fontsize, Font.NORMAL, BaseColor.BLACK);
			section.add(new Paragraph("Планеты в знаках Зодиака и астрологических домах:", font));

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
	 * @param event событие
	 * @param cell тег-контейнер для вложенных тегов
	 * TODO фиктивные планеты при определении вида космограмм не считаются!!!
	 */
	private void printCardKind(Document doc, PdfWriter writer, Chapter chapter, Event event) {
		try {
			Section section = PDFUtil.printSection(chapter, "Кармический потенциал", baseFont);
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
}
