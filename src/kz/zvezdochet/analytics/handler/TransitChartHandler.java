package kz.zvezdochet.analytics.handler;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.jfree.data.category.DefaultCategoryDataset;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.ChapterAutoNumber;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import kz.zvezdochet.analytics.Activator;
import kz.zvezdochet.analytics.part.GraphicPart;
import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.Ingress;
import kz.zvezdochet.bean.Place;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.SkyPoint;
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.core.util.PlatformUtil;
import kz.zvezdochet.export.handler.PageEventHandler;
import kz.zvezdochet.export.util.PDFUtil;
import kz.zvezdochet.service.PlanetService;

/**
 * Генерация графиков глобального прогноза за указанный период
 * @author Natalie Didenko
 */
public class TransitChartHandler extends Handler {
	private BaseFont baseFont;

	public TransitChartHandler() {
		super();
		try {
			baseFont = PDFUtil.getBaseFont();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Execute
	public void execute(@Active MPart activePart) {
		Document doc = new Document(PageSize.A4.rotate());
		try {
			long duration = System.currentTimeMillis();
			long run = duration;
			GraphicPart periodPart = (GraphicPart)activePart.getObject();
			if (!periodPart.check(0)) return;

			Map<Long, Model> planets = new PlanetService().getMap();
			updateStatus("Расчёт транзитов на период", false);

			Date initDate = periodPart.getInitialDate();
			Date finalDate = periodPart.getFinalDate();
			Calendar start = Calendar.getInstance();
			start.setTime(initDate);

			Calendar end = Calendar.getInstance();
			end.setTime(finalDate);

			String filename = PlatformUtil.getPath(Activator.PLUGIN_ID, "/out/charts.pdf").getPath();
			PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(filename));
	        writer.setPageEvent(new PageEventHandler());
	        doc.open();

	    	Font font = PDFUtil.getRegularFont();

	        //metadata
	        PDFUtil.getMetaData(doc, "Транзиты", "ru");

	        //раздел
			Chapter chapter = new ChapterAutoNumber("Транзиты");
			chapter.setNumberDepth(0);

			//шапка
			Paragraph p = new Paragraph();
			PDFUtil.printHeader(p, "Транзиты", null);
			chapter.add(p);

			SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy");
			String text = sdf.format(initDate) + " — " + sdf.format(finalDate);
			p = new Paragraph(text, font);
	        p.setAlignment(Element.ALIGN_CENTER);
			chapter.add(p);

			Font fontgray = PDFUtil.getAnnotationFont(false);
			text = "Дата составления: " + DateUtil.fulldtf.format(new Date());
			p = new Paragraph(text, fontgray);
	        p.setAlignment(Element.ALIGN_CENTER);
			chapter.add(p);

			p = new Paragraph();
	        p.setAlignment(Element.ALIGN_CENTER);
			p.setSpacingAfter(20);
	        p.add(new Chunk("Автор: ", fontgray));
	        Chunk chunk = new Chunk(PDFUtil.getAuthor("ru"), new Font(baseFont, 10, Font.UNDERLINE, PDFUtil.FONTCOLOR));
	        chunk.setAnchor(PDFUtil.getWebsite("ru"));
	        p.add(chunk);
	        chapter.add(p);

			List<Long> ydates = new ArrayList<Long>();
			Map<Long, Map<Long, List<SkyPointAspect>>> yplanets = new TreeMap<Long, Map<Long, List<SkyPointAspect>>>();

			System.out.println("Prepared for: " + (System.currentTimeMillis() - run));
			run = System.currentTimeMillis();

			//разбивка дат по годам и месяцам
			for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				long time = date.getTime(); 
				ydates.add(time);
			}
			Collections.sort(ydates);

			/**
			 * коды ингрессий, используемых в отчёте
			 */
			String[] icodes = new String[] {
				Ingress._EXACT,
				Ingress._SEPARATION,
				Ingress._RETRO, Ingress._DIRECT,
				Ingress._REPEAT
			};

			//создаём аналогичный массив, но с домами вместо дат
			int k = -1;
			for (Long time : ydates) {
				++k;
				Date date = new Date(time);
				String sdate = DateUtil.formatCustomDateTime(date, "yyyy-MM-dd") + " 12:00:00";
				Event event = new Event();
				Date edate = DateUtil.getDatabaseDateTime(sdate);
				event.setBirth(edate);
				event.setPlace(new Place().getDefault());
				event.setZone(0);
				event.calc(true);

				Map<String, List<Object>> ingressList = event.getPrev().initIngresses(event, false);
				if (ingressList.isEmpty())
					continue;

				for (Map.Entry<String, List<Object>> ientry : ingressList.entrySet()) {
					String key = ientry.getKey();
					if (!Arrays.asList(icodes).contains(key))
						continue;

					if (key.contains("REPEAT")
							&& (k > 0
								&& !DateUtil.formatDate(date).equals(DateUtil.formatDate(finalDate))))
						continue;

					List<Object> ingresses = ientry.getValue();
					for (Object object : ingresses) {
						if (object instanceof Planet)
							continue;
						SkyPointAspect spa = (SkyPointAspect)object;
						Planet skyPoint = (Planet)spa.getSkyPoint1();
						long pid = skyPoint.getId();
						if (20 == pid)
							continue;
						if (26 == pid)
							continue;

						SkyPoint skyPoint2 = spa.getSkyPoint2();
						long pid2 = skyPoint2.getId();
						if (20 == pid2)
							continue;
						if (22 == pid2)
							continue;
						if (26 == pid2)
							continue;
						if (pid == pid2)
							continue;
						if (21 == pid2 && 22 == pid)
							continue;

						Map<Long, List<SkyPointAspect>> dmap = yplanets.containsKey(pid2) ? yplanets.get(pid2) : new TreeMap<Long, List<SkyPointAspect>>();
						List<SkyPointAspect> pmap = dmap.containsKey(time) ? dmap.get(time) : new ArrayList<SkyPointAspect>();
						pmap.add(spa);
						dmap.put(time, pmap);
						yplanets.put(pid2, dmap);
					}
				}
			}
			ydates = null;
			System.out.println("Composed for: " + (System.currentTimeMillis() - run));
			SimpleDateFormat sdf2 = DateUtil.dbdf;

			//генерируем транзиты планет
			for (Map.Entry<Long, Map<Long, List<SkyPointAspect>>> entry : yplanets.entrySet()) {
				Planet planet = (Planet)planets.get(entry.getKey());

	        	Map<Long, List<SkyPointAspect>> map = entry.getValue();
				if (map.isEmpty())
					continue;

				DefaultCategoryDataset dataset = new DefaultCategoryDataset();
				for (Map.Entry<Long, List<SkyPointAspect>> entry3 : map.entrySet()) {
					List<SkyPointAspect> series = entry3.getValue();
					if (null == series || series.isEmpty())
						continue;

					Long d = entry3.getKey();
					for (int j = 0; j < series.size(); j++) {
						SkyPointAspect spa = series.get(j);
						Planet planet2 = (Planet)planets.get(spa.getSkyPoint1().getId());
						Aspect aspect = spa.getAspect();
						String sign = "";
						if (!aspect.getCode().equals("CONJUNCTION"))
							sign += aspect.getType().getSymbol();
						dataset.addValue(planet2.getNumber(), planet2.getCode() + sign, sdf2.format(new Date(d)));
					}
				}
				if (dataset.getColumnCount() > 0) {
		        	Section section = PDFUtil.printSection(chapter, planet.getName(), null);
					Image image = PDFUtil.printLineChart(writer, "", "", "Баллы", dataset, 750, 300, true);
					section.add(image);
					section.add(Chunk.NEXTPAGE);
				}
			}
			yplanets = null;
			doc.add(chapter);
			doc.add(Chunk.NEWLINE);
	        doc.add(PDFUtil.printCopyright("ru"));

	        long time = System.currentTimeMillis();
			System.out.println("Finished for: " + (time - run));
			System.out.println("Duration: " + (time - duration));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
	        doc.close();
		}
	}
}
