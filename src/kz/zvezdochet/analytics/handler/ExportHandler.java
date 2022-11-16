package kz.zvezdochet.analytics.handler;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import kz.zvezdochet.analytics.exporter.PDFExporter;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.part.EventPart;

/**
 * PDF-экспорт натальной карты
 * @author Natalie Didenko
 *
 */
public class ExportHandler extends Handler {
	@Execute
	public void execute(@Active MPart activePart) {
		try {
			final EventPart eventPart = (EventPart)activePart.getObject();
			final Event event = (Event)eventPart.getModel(EventPart.MODE_ASPECT_PLANET_PLANET, true);
			if (null == event) {
				DialogUtil.alertWarning("Произведите расчёт");
				return;
			}
			updateStatus("Экспорт индивидуального гороскопа", false);
			final Display display = Display.getDefault();
    		BusyIndicator.showWhile(display, new Runnable() {
    			@Override
    			public void run() {
    				new PDFExporter(display).generate(event, eventPart.isTerm());
    			}
    		});
			updateStatus("Экспорт завершён", false);
		} catch (Exception e) {
			DialogUtil.alertError(e);
			updateStatus("Ошибка экспорта", true);
			e.printStackTrace();
		}
	}
}
