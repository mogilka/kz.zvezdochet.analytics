package kz.zvezdochet.analytics.part;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.ui.view.ListView;
import kz.zvezdochet.core.ui.view.View;
import kz.zvezdochet.core.util.DateUtil;

/**
 * Представление графиков
 * @author Natalie Didenko
 *
 */
public class GraphicPart extends ListView {

	@Inject
	public GraphicPart() {}
	
	@PostConstruct @Override
	public View create(Composite parent) {
		return super.create(parent);
	}

	private DateTime dt;
	private DateTime dt2;

	@Override
	public boolean check(int mode) throws Exception {
		Date date = getInitialDate();
		Date date2 = getFinalDate();
		if (!DateUtil.isDateRangeValid(date, date2)) {
			DialogUtil.alertWarning("Укажите правильный период");
			return false;
		}
		return true;
	}

	public Date getInitialDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, dt.getDay());
		calendar.set(Calendar.MONTH, dt.getMonth());
		calendar.set(Calendar.YEAR, dt.getYear());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public Date getFinalDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, dt2.getDay());
		calendar.set(Calendar.MONTH, dt2.getMonth());
		calendar.set(Calendar.YEAR, dt2.getYear());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	@Override
	public void initFilter(Composite parent) {
		grFilter = new Group(parent, SWT.NONE);
		grFilter.setText("Период");

		Label lb = new Label(grFilter, SWT.NONE);
		lb.setText("Начало");
		dt = new DateTime(grFilter, SWT.DROP_DOWN);

		lb = new Label(grFilter, SWT.NONE);
		lb.setText("Конец");
		dt2 = new DateTime(grFilter, SWT.DROP_DOWN);

		GridLayoutFactory.swtDefaults().numColumns(10).applyTo(grFilter);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(grFilter);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(dt);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(dt2);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	protected void initControls() throws DataAccessException {
		super.initControls();
		try {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			int month = calendar.get(Calendar.MONTH);
			dt.setDate(calendar.get(Calendar.YEAR), month, calendar.get(Calendar.DAY_OF_MONTH));

			int date = 31;
			if (1 == month)
				date = calendar.isLeapYear(calendar.get(Calendar.YEAR)) ? 28 : 29;
			else if (Arrays.asList(new int[] {4, 6, 9, 11}).contains(month))
				date = 30;
			dt2.setDate(calendar.get(Calendar.YEAR), month, date);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String[] initTableColumns() {
		// TODO Auto-generated method stub
		return null;
	}
}
