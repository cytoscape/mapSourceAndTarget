package edu.ucsf.rbvi.mapSourceAndTarget.internal;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_EXAMPLE_JSON;
import static org.cytoscape.work.ServiceProperties.COMMAND_LONG_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.COMMAND_SUPPORTS_JSON;
import static org.cytoscape.work.ServiceProperties.ID;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.IN_TOOL_BAR;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.LARGE_ICON_URL;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;
import static org.cytoscape.work.ServiceProperties.TOOL_BAR_GRAVITY;
import static org.cytoscape.work.ServiceProperties.TOOLTIP;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import edu.ucsf.rbvi.mapSourceAndTarget.internal.model.MapSandTManager;
import edu.ucsf.rbvi.mapSourceAndTarget.internal.tasks.MapTaskFactory;


public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		final CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);
    MapSandTManager manager = new MapSandTManager(serviceRegistrar);
    registerService(bc, manager, NetworkAddedListener.class, new Properties());

		{
			MapTaskFactory expExp = new MapTaskFactory(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(IN_MENU_BAR, "true");
			props.setProperty(PREFERRED_MENU, "Apps");
			props.setProperty(TITLE, "mapSourceAndTarget");
			props.setProperty(COMMAND_NAMESPACE, "mapSourceAndTarget");
			props.setProperty(COMMAND, "map");
			props.setProperty(COMMAND_DESCRIPTION, "Map node columns into the edge table");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
			registerService(bc, expExp, TaskFactory.class, props);
		}

	}
}
