package org.cytoscape.mapSourceAndTarget.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class MapTaskFactory extends AbstractTaskFactory {
  final CyServiceRegistrar registrar;

  public MapTaskFactory(final CyServiceRegistrar reg) {
    super();
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
}
