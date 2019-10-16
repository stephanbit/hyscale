package io.hyscale.ctl.controller.plugins;

import io.hyscale.ctl.commons.component.ComponentInvokerPlugin;
import io.hyscale.ctl.commons.config.SetupConfig;
import io.hyscale.ctl.commons.exception.HyscaleException;
import io.hyscale.ctl.commons.utils.HyscaleFilesUtil;
import io.hyscale.ctl.controller.constants.WorkflowConstants;
import io.hyscale.ctl.controller.model.WorkflowContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Plugin to clean service directory to remove files no longer required
 *
 */
@Component
public class ServiceDirCleanUpPlugin implements ComponentInvokerPlugin<WorkflowContext> {

	private static final Logger logger = LoggerFactory.getLogger(ServiceDirCleanUpPlugin.class);

	@Autowired
	private SetupConfig setupConfig;

	@Autowired
	private HyscaleFilesUtil filesUtil;

	@Override
	public void doBefore(WorkflowContext context) throws HyscaleException {
		if (context.getServiceName() != null && context.getAttribute(WorkflowConstants.CLEAN_UP_SERVICE_DIR) != null
				&& context.getAttribute(WorkflowConstants.CLEAN_UP_SERVICE_DIR).equals(true)) {
			String serviceDir = setupConfig.getServiceDir(context.getAppName(), context.getServiceName());
			filesUtil.deleteDirectory(serviceDir);
			logger.debug("Cleaning up service dir in the apps");
		}
	}

	@Override
	public void doAfter(WorkflowContext context) throws HyscaleException {

	}

	@Override
	public void onError(WorkflowContext context, Throwable th) {

	}
}
