package kz.zvezdochet.analytics.handler;

import kz.zvezdochet.analytics.exporter.HTMLExporter;
import kz.zvezdochet.analytics.exporter.PDFExporter;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.part.EventPart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CacheRequest;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Обработка события для html-экспорта индивидуального гороскопа
 * @author Nataly Didenko
 *
 */
public class ExportHandler extends Handler {
	@Execute
	public void execute(@Active MPart activePart) {
		try {
			EventPart eventPart = (EventPart)activePart.getObject();
			final Event event = (Event)eventPart.getModel(EventPart.MODE_CALC, true);
			if (null == event) return;
			if (null == event.getConfiguration()) {
				DialogUtil.alertWarning("Произведите расчёт");
				return;
			}
			updateStatus("Экспорт индивидуального гороскопа", false);
			final Display display = Display.getDefault();
    		BusyIndicator.showWhile(display, new Runnable() {
    			@Override
    			public void run() {
//    				new HTMLExporter(display).generate(event);
    				new PDFExporter(display).generate(event);
//    				test(event);
    			}
    		});
			updateStatus("Экспорт завершён", false);
		} catch (Exception e) {
			DialogUtil.alertError(e.getMessage());
			updateStatus("Ошибка экспорта", true);
			e.printStackTrace();
		}
	}

	private void test(Event event) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Cancer", 4);
		dataset.setValue("Leo", 3);
		dataset.setValue("Taurus", 1.5);
		JFreeChart chart = ChartFactory.createPieChart("Signs", dataset, true, true, false);

		float width = 640;
		float height = 480;
		Document pdf = new Document(new Rectangle(width, height));
		try {
			PdfWriter writer = PdfWriter.getInstance(pdf, new FileOutputStream("/home/nataly/workspace/kz.zvezdochet.export/out/test.pdf"));
			pdf.open();
			PdfContentByte cb = writer.getDirectContent();
			PdfTemplate tpl = cb.createTemplate(width, height);
			Graphics2D g2d = tpl.createGraphics(width, height, new DefaultFontMapper());
			Rectangle2D r2d = new Rectangle2D.Double(0, 0, width, height);
			chart.draw(g2d, r2d);
			g2d.dispose();
			cb.addTemplate(tpl, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pdf.close();
	}
}
