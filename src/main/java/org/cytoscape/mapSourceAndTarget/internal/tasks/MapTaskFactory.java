package org.cytoscape.mapSourceAndTarget.internal.tasks;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.TableTaskFactory;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class MapTaskFactory extends AbstractTaskFactory implements TableTaskFactory {
  final CyServiceRegistrar registrar;

  public MapTaskFactory(final CyServiceRegistrar reg) {
    this.registrar = reg;
  }

  @Override
  public TaskIterator createTaskIterator() {
    return new TaskIterator(new MapTask(registrar));
  }

  @Override
  public boolean isReady() {
    return true;
  }
  
  public boolean isApplicable(CyTable table) {
    var networkTableManager = registrar.getService(CyNetworkTableManager.class);
    var type = networkTableManager.getTableType(table);
    return CyEdge.class.equals(type);
  }

  @Override
  public TaskIterator createTaskIterator(CyTable table) {
    return new TaskIterator(new MapTask(registrar));
  }

  @Override
  public boolean isReady(CyTable table) {
    return true;
  }
}
