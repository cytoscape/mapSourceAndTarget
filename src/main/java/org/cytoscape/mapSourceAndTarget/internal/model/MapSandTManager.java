package org.cytoscape.mapSourceAndTarget.internal.model;

import java.util.Collection;
import java.util.Properties;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.property.AbstractConfigDirPropsReader;
import org.cytoscape.property.CyProperty;
import org.cytoscape.property.CyProperty.SavePolicy;
import org.cytoscape.service.util.CyServiceRegistrar;

import org.cytoscape.mapSourceAndTarget.internal.tasks.MapTask;

/**
 * This class manages the automatic mapping of source and target node
 * names to the edge table.
 */
public class MapSandTManager implements NetworkAddedListener {
  final CyServiceRegistrar registrar;
  final Properties configProperties;
  String defaultColumn;

  public MapSandTManager(final CyServiceRegistrar registrar) {
    this.registrar = registrar;

    // Check preference
    configProperties = getPropertyService();

    // If we're supposed to, register ourselves as a listener for network creation
    if (!configProperties.containsKey("autoMap")) {
      configProperties.setProperty("autoMap", "FALSE");
    }
  }

  public void handleEvent(NetworkAddedEvent e) {
    // Check to see if we're supposed to do the mapping
    if (!getBoolean(configProperties, "autoMap")) {
      return;
    }

    // Get the default column
    defaultColumn = configProperties.getProperty("defaultColumn", "name");

    // See if we already have mapped columns
    CyNetwork net = e.getNetwork();
    Collection<CyColumn> columns = net.getDefaultEdgeTable().getColumns("node");
    if (columns != null && columns.size() > 0) { return; }

    // Nope, do the mapping
    MapTask task = new MapTask(registrar);
    CyTable edgeTable = net.getDefaultEdgeTable();
    CyTable nodeTable = net.getDefaultNodeTable();
    task.createColumn(edgeTable, nodeTable.getColumn(defaultColumn).getType(), "node::Source_", defaultColumn, MapTask.SOURCE_EQ);
    task.createColumn(edgeTable, nodeTable.getColumn(defaultColumn).getType(), "node::Target_", defaultColumn, MapTask.TARGET_EQ);
  }

  private boolean getBoolean(Properties p, String prop) {
    String v = p.getProperty(prop, "FALSE");
    return Boolean.valueOf(v);
  }

  private Properties getPropertyService() {
    CyProperty<Properties> service = new CyPropertyReader();
    Properties serviceProps = new Properties();
    serviceProps.setProperty("cyPropertyName", service.getName());
    registrar.registerAllServices(service, serviceProps);

    return service.getProperties();
  }

  private class CyPropertyReader extends AbstractConfigDirPropsReader {
    public CyPropertyReader() {
      super("mapSourceAndTarget", "mapSourceAndTarget.props", SavePolicy.CONFIG_DIR);
    }
  }
}
