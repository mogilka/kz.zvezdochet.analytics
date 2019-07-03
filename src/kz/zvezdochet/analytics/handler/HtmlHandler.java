package kz.zvezdochet.analytics.handler;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import kz.zvezdochet.analytics.exporter.HTMLExporter;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.part.EventPart;

/**
 * Обработка события для html-экспорта индивидуального гороскопа
 * @author Natalie Didenko
 *
 */
public class HtmlHandler extends Handler {
	@Execute
	public void execute(@Active MPart activePart) {
		try {
			final EventPart eventPart = (EventPart)activePart.getObject();
			final Event event = (Event)eventPart.getModel(EventPart.MODE_CALC, true);
			if (null == event) {
				DialogUtil.alertWarning("Произведите расчёт");
				return;
			}
			updateStatus("Экспорт индивидуального гороскопа", false);
			final Display display = Display.getDefault();
    		BusyIndicator.showWhile(display, new Runnable() {
    			@Override
    			public void run() {
    				String html = new HTMLExporter().generate(event);
    				eventPart.onCalc(html);
    			}
    		});
			updateStatus("Экспорт завершён", false);
		} catch (Exception e) {
			DialogUtil.alertError(e.getMessage());
			updateStatus("Ошибка экспорта", true);
			e.printStackTrace();
		}
	}
}