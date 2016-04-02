package net.fmoraes.eclipseforces.java;

import java.util.Collections;

import net.fmoraes.eclipseforces.util.AbstractLauncher;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

/**
 * A class for launching JUnit tests specifying a java class.
 */
class JUnitLauncher extends AbstractLauncher {

  // internal constant:
  // org.eclipse.jdt.internal.junit.launcher.JUnitLaunchConfiguration.ID_JUNIT_APPLICATION:
  public static final String ID_JUNIT_APPLICATION = "org.eclipse.jdt.junit.launchconfig";

  private static final String JUNIT_TEST_KIND_ATTRIBUTE = "org.eclipse.jdt.junit.TEST_KIND";

  // internal constant
  // org.eclipse.jdt.internal.junit.launcher.TestKindRegistry.JUNIT4_TEST_KIND_ID:
  private static final String JUNIT4_TEST_KIND_ID = "org.eclipse.jdt.junit.loader.junit4";

  private ICompilationUnit unit;
  
  private String memoryLimit;
  
  private boolean shouldRun;

  /**
   * Create a new launcher for a new java class file (ICompilationUnit).
   * 
   * @param unit
   *            The .java file to run
   */
  public JUnitLauncher(ICompilationUnit unit, String memory, boolean run) {
    this.unit = unit;
    memoryLimit = memory;
    shouldRun = run;
  }

  private String getClassName() {
    String className = null;
    try {
      IType type = this.unit.getTypes()[0];
      className = type.getFullyQualifiedName();
    } catch (JavaModelException e) {
      throw new RuntimeException(e);
    }
    return className;
  }

  @Override
  protected String getLauncherName() {
    return getClassName();
  }

  @Override
  protected String getLauncherTypeId() {
    return ID_JUNIT_APPLICATION;
  }

  @Override
  protected void setUpConfiguration(ILaunchConfigurationWorkingCopy config) throws Exception {
    String projectName = unit.getJavaProject().getElementName();

    config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
    config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, getClassName());
    config.setAttribute(JUNIT_TEST_KIND_ATTRIBUTE, JUNIT4_TEST_KIND_ID);
    String vmArgs = "-ea";
    if(memoryLimit != null)
      vmArgs += " -Xmx" + memoryLimit + "M";
    config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs);
    // Setting preferred_launchers avoids being prompted for specific launcher, which may happen for JUnit when
    // Android Tools are installed (choice between "Eclipse JUnit Launcher" and "Android JUnit Test Launcher"):
    config.setAttribute("org.eclipse.debug.core.preferred_launchers",
        Collections.singletonMap("[run]", "org.eclipse.jdt.junit.launchconfig"));
  }

  @Override
  protected boolean shouldRun()
  {
    return shouldRun;
  }
}
