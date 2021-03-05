package edu.ucsf.rbvi.mapSourceAndTarget.internal.tasks;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.equations.Equation;
import org.cytoscape.equations.EquationCompiler;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListMultipleSelection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapTask extends AbstractTask {
  final private CyServiceRegistrar registrar;
  final private CyApplicationManager applicationManager;
  final private CyNetwork currentNetwork;
  final private EquationCompiler compiler;
  final static String TARGET_EQ = "=%sTABLECELL(TARGETID($SUID), \"%s\")";
  final static String SOURCE_EQ = "=%sTABLECELL(SOURCEID($SUID), \"%s\")";

  @Tunable(description="Node columns to map")
  public ListMultipleSelection<String> nodeColumns;

  public MapTask(final CyServiceRegistrar registrar) {
    super();
    this.registrar = registrar;
    this.applicationManager = registrar.getService(CyApplicationManager.class);
    this.compiler = registrar.getService(EquationCompiler.class);
    this.currentNetwork = applicationManager.getCurrentNetwork();
    if (currentNetwork == null) return;

    List<String> names = new ArrayList<>(CyTableUtil.getColumnNames(currentNetwork.getDefaultNodeTable()));
    Collections.sort(names);
    nodeColumns = new ListMultipleSelection<>(names);
  }

  @Override
  public void run(TaskMonitor monitor) {
    if (currentNetwork == null) {
      monitor.showMessage(TaskMonitor.Level.ERROR, "No network");
      return;
    }

    List<String> columns = nodeColumns.getSelectedValues();
    if (columns == null || columns.isEmpty()) {
      monitor.showMessage(TaskMonitor.Level.ERROR, "No columns selected");
      return;
    }

    CyTable edgeTable = currentNetwork.getDefaultEdgeTable();
    CyTable nodeTable = currentNetwork.getDefaultNodeTable();
    for (String column: columns) {
      createColumn(edgeTable, nodeTable.getColumn(column).getType(), "node::Source_", column, SOURCE_EQ);
      createColumn(edgeTable, nodeTable.getColumn(column).getType(), "node::Target_", column, TARGET_EQ);
    }
  }

  public void createColumn(CyTable targetTable, Class<?> clazz, String prefix, String column, String equationPattern) {
    final Map<String, Class<?>> variableNameToTypeMap = new HashMap<>();
    variableNameToTypeMap.put("SUID", Long.class);
    variableNameToTypeMap.put(column, clazz == Integer.class ? Long.class : clazz);

    // Create the column
    targetTable.createColumn(prefix+column, clazz, false);
    String formula = String.format(equationPattern, clazz.getSimpleName().toUpperCase(), column);

    // System.out.println("formula = "+formula);
    if (compiler.compile(formula, variableNameToTypeMap)) {
      Equation eq = compiler.getEquation();
      for (CyRow row: targetTable.getAllRows()) {
        row.set(prefix+column, eq);
      }
    } else {
      Equation eq = compiler.getErrorEquation(formula, clazz, compiler.getLastErrorMsg());
      for (CyRow row: targetTable.getAllRows()) {
        row.set(prefix+column, eq);
      }
    }
    return;
  }
}
