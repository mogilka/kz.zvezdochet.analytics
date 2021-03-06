package kz.zvezdochet.analytics.part;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.experimental.chart.swt.ChartComposite;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.ui.util.GUIutil;
import kz.zvezdochet.core.ui.view.View;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.service.EventService;
import kz.zvezdochet.service.PlanetService;

/**
 * Представление графиков
 * @author Natalie Didenko
 *
 */
public class GraphicPart extends View {

	@Inject
	public GraphicPart() {}
	
	@PostConstruct @Override
	public View create(Composite parent) {
		return super.create(parent);
	}

	@Override
	protected void init(Composite parent) {
		super.init(parent);
		Group grFilter = new Group(sashForm, SWT.NONE);
		grFilter.setText("Поиск");
		grFilter.setLayout(new GridLayout());

		Label lb = new Label(grFilter, SWT.NONE);
		lb.setText("Начало");
		final DateTime dt = new DateTime(grFilter, SWT.DROP_DOWN);
//		dt.setNullText(""); //$NON-NLS-1$
//		dt.setSelection(selection);

		lb = new Label(grFilter, SWT.NONE);
		lb.setText("Конец");
		final DateTime dt2 = new DateTime(grFilter, SWT.DROP_DOWN);
//		dt2.setNullText(""); //$NON-NLS-1$

		Button bt = new Button(grFilter, SWT.NONE);
		bt.setText("Искать");
		bt.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				if (null == dt.getSelection() || null == dt2.getSelection())
//					return;
				try {
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.DAY_OF_MONTH, dt.getDay());
					calendar.set(Calendar.MONTH, dt.getMonth());
					calendar.set(Calendar.YEAR, dt.getYear());
					Date date = calendar.getTime();

					calendar = Calendar.getInstance();
					calendar.set(Calendar.DAY_OF_MONTH, dt2.getDay());
					calendar.set(Calendar.MONTH, dt2.getMonth());
					calendar.set(Calendar.YEAR, dt2.getYear());
					Date date2 = calendar.getTime();

					setData(date, date2);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		GridLayoutFactory.swtDefaults().applyTo(grFilter);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grFilter);

		group = new Group(parent, SWT.EMBEDDED);
		group.setText("Инфографика");
//		group.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		GridLayoutFactory.swtDefaults().applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
//		setData(new Date(1517206791), new Date(1517293190));
	}

	protected void setData(Date initDate, Date finalDate) {
		try {
//			for(Control control : group.getChildren())
//				if (!control.isDisposed())
//					control.dispose();
			//разбивка дат по периоду
			Calendar start = Calendar.getInstance();
			start.setTime(initDate);
	
			Calendar end = Calendar.getInstance();
			end.setTime(finalDate);
	
			Map<Long, Event> dates = new HashMap<>();
			EventService service = new EventService();
			for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
	
				//ищем событие даты
				Event event = null;
				String sdate = DateUtil.formatCustomDateTime(date, "yyyy-MM-dd");
				List<Event> events = service.findByDate(sdate, 1);
				if (null == events || 0 == events.size()) {
					//если нет, создаём
					event = new Event(DateUtil.getDatabaseDateTime(sdate + " 12:00:00"), "Мой гороскоп");
					event.calc(true);
					event.setCalculated(true);
					service.save(event);
					System.out.println("Новый добавлен: " + event.toLog() + "\n");
				} else {
					event = events.get(0);
					event.initData(false);
				}
				long time = event.getBirth().getTime(); 
				if (!dates.containsKey(time))
					dates.put(time, event);
			}
	//		Collections.sort(dates); TODO
	
			Map<Long, List<TimeSeriesDataItem>> items = new TreeMap<Long, List<TimeSeriesDataItem>>();
			for (Map.Entry<Long, Event> entry : dates.entrySet()) {
				Date date = new Date(entry.getKey());
				Event event = entry.getValue();
				Collection<Planet> planets = event.getPlanets().values();
	
				for (Planet planet : planets) {
					long pid = planet.getId();
					List<TimeSeriesDataItem> series = items.containsKey(pid) ? items.get(pid) : new ArrayList<TimeSeriesDataItem>();
					TimeSeriesDataItem tsdi = new TimeSeriesDataItem(new Day(date), planet.getLongitude());
					if (!series.contains(tsdi))
						series.add(tsdi);
					items.put(pid, series);
				}
			}
	
			//генерируем диаграмму орбит
			TimeSeriesCollection dataset = new TimeSeriesCollection();
			PlanetService pservice = new PlanetService();
			List<Model> planets = pservice.getList();
	       	for (Model model : planets) {
	       		Planet planet = (Planet)model;
	       		long pid = planet.getId();
	       		List<TimeSeriesDataItem> series = items.containsKey(pid) ? items.get(pid) : new ArrayList<TimeSeriesDataItem>();
	       		if (null == series || 0 == series.size())
	       			continue;
				TimeSeries timeSeries = new TimeSeries(planet.getSymbol());
				for (TimeSeriesDataItem tsdi : series)
					timeSeries.add(tsdi);
				dataset.addSeries(timeSeries);
	       	}
	       	if (dataset.getSeriesCount() > 0) {
	       		JFreeChart chart = ChartFactory.createTimeSeriesChart("Орбиты", "Даты", "Координаты", dataset, true, true, true);
	            XYPlot plot = (XYPlot)chart.getPlot();
	            plot.setBackgroundPaint(new java.awt.Color(230, 230, 250));

                DateAxis axis = (DateAxis)plot.getDomainAxis();
                axis.setDateFormatOverride(new SimpleDateFormat("dd.MM"));
                axis.setAutoTickUnitSelection(false);
                axis.setVerticalTickLabels(true);

                final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();
                renderer.setBaseStroke(new BasicStroke(3));
                Stroke dashed = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {3.0f}, 0.0f);
                int scnt = dataset.getSeries().size();
                for (int i = 0; i < scnt; i++) {
                	Stroke stroke = renderer.getBaseStroke();
                	if (i % 2 > 0)
						stroke = dashed;
                	renderer.setSeriesStroke(i, stroke);
                }
	            final Display display = Display.getDefault();
	            Shell shell = new Shell(display);
	            Point point = GUIutil.getScreenSize();
	            shell.setSize(point.x - 800, point.y - 400);
	            shell.setLayout(new FillLayout());
	            shell.setText("Орбиты");
	            ChartComposite frame = new ChartComposite(shell, SWT.NONE, chart, true);
	            frame.setDisplayToolTips(true);
	            frame.setHorizontalAxisTrace(false);
	            frame.setVerticalAxisTrace(false);
	            shell.open();
	            while (!shell.isDisposed()) {
	                if (!display.readAndDispatch())
	                    display.sleep();
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Контейнер для графика
	 */
	private Group group;
}
