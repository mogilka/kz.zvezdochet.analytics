package kz.zvezdochet.analytics.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import kz.zvezdochet.core.handler.Handler;

public class MainHandler extends Handler {

	@Execute
	public void execute(MApplication app, EModelService service, EPartService partService) {
		MPerspective perspective = (MPerspective)service.find("kz.zvezdochet.analytics.perspective.graphics", app);
		if (perspective != null)
			partService.switchPerspective(perspective);
	}
}