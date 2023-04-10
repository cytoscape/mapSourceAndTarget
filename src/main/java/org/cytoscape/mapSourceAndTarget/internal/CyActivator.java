package org.cytoscape.mapSourceAndTarget.internal;

import static org.cytoscape.work.ServiceProperties.*;

import java.util.Properties;

import org.cytoscape.mapSourceAndTarget.internal.model.MapSandTManager;
import org.cytoscape.mapSourceAndTarget.internal.tasks.MapTaskFactory;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.TableTaskFactory;
import org.cytoscape.util.swing.IconManager;
import org.cytoscape.util.swing.TextIcon;
import org.osgi.framework.BundleContext;


public class CyActivator extends AbstractCyActivator {

	public void start(BundleContext bc) {
		final CyServiceRegistrar registrar = getService(bc, CyServiceRegistrar.class);
		MapSandTManager manager = new MapSandTManager(registrar);
		registerService(bc, manager, NetworkAddedListener.class, new Properties());

		{
			var iconManager = registrar.getService(IconManager.class);
			var iconFont = iconManager.getIconFont("cytoscape-3", 18.0f);
			var icon = new TextIcon("#", iconFont, 24, 24); // "#" is the node table icon in the cytoscape-3 font
			var iconId = "mapSourceAndTarget_tableTaskFactory";
			iconManager.addIcon(iconId, icon);
			
			MapTaskFactory expExp = new MapTaskFactory(registrar);
			Properties props = new Properties();
			
			props.setProperty(LARGE_ICON_ID, iconId);
			props.setProperty(TOOLTIP, "Map node source/target columns...");
			props.setProperty("inNodeTableToolBar", "false");
			props.setProperty("inEdgeTableToolBar", "true");
			props.setProperty("inNetworkTableToolBar", "false");
			props.setProperty("inUnassignedTableToolBar", "false");
			props.setProperty(TOOL_BAR_GRAVITY, "0.0045");
			
			props.setProperty(COMMAND_NAMESPACE, "mapSourceAndTarget");
			props.setProperty(COMMAND, "map");
			props.setProperty(COMMAND_DESCRIPTION, "Map node columns into the edge table");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
			
			registerService(bc, expExp, TableTaskFactory.class, props);
		}

	}
}
