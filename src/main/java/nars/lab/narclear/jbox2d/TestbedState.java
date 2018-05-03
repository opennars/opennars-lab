/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nars.lab.narclear.jbox2d;

import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import nars.lab.narclear.PhysicsModel;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.IViewportTransform;

/**
 * Model for the testbed
 * 
 * @author Daniel
 */
public class TestbedState {
  private final DefaultComboBoxModel tests = new DefaultComboBoxModel();
  private final TestbedSettings settings = new TestbedSettings();
  private DebugDraw draw;
  private PhysicsModel test;
  private final Vector<TestChangedListener> listeners = new Vector<>();
  private final boolean[] keys = new boolean[512];
  private final boolean[] codedKeys = new boolean[512];
  private float calculatedFps;
  private int currTestIndex = -1;
  private PhysicsModel runningTest;
  private List<String> implSpecificHelp;
  private TestbedPanel panel;
  private WorldCreator worldCreator = new DefaultWorldCreator();

  public TestbedState() {}

  public WorldCreator getWorldCreator() {
    return worldCreator;
  }

  public void setWorldCreator(WorldCreator worldCreator) {
    this.worldCreator = worldCreator;
  }

  public void setPanel(TestbedPanel panel) {
    this.panel = panel;
  }

  public TestbedPanel getPanel() {
    return panel;
  }

  public void setImplSpecificHelp(List<String> implSpecificHelp) {
    this.implSpecificHelp = implSpecificHelp;
  }

  public List<String> getImplSpecificHelp() {
    return implSpecificHelp;
  }

  public void setCalculatedFps(float calculatedFps) {
    this.calculatedFps = calculatedFps;
  }

  public float getCalculatedFps() {
    return calculatedFps;
  }

  public void setViewportTransform(IViewportTransform transform) {
    draw.setViewportTransform(transform);
  }

  public void setDebugDraw(DebugDraw argDraw) {
    draw = argDraw;
  }

  public DebugDraw getDebugDraw() {
    return draw;
  }

  public PhysicsModel getCurrTest() {
    return test;
  }

  /**
   * Gets the array of keys, index corresponding to the char value.
   * 
   * @return
   */
  public boolean[] getKeys() {
    return keys;
  }

  /**
   * Gets the array of coded keys, index corresponding to the coded key value.
   * 
   * @return
   */
  public boolean[] getCodedKeys() {
    return codedKeys;
  }

  public void setCurrTestIndex(int argCurrTestIndex) {
    if (argCurrTestIndex < 0 || argCurrTestIndex >= tests.getSize()) {
      throw new IllegalArgumentException("Invalid test index");
    }
    if (currTestIndex == argCurrTestIndex) {
      return;
    }

    if (!isTestAt(argCurrTestIndex)) {
      throw new IllegalArgumentException("No test at " + argCurrTestIndex);
    }
    currTestIndex = argCurrTestIndex;
    ListItem item = (ListItem) tests.getElementAt(argCurrTestIndex);
    test = item.test;
    for (TestChangedListener listener : listeners) {
      listener.testChanged(test, currTestIndex);
    }
  }

  public int getCurrTestIndex() {
    return currTestIndex;
  }

  public void setRunningTest(PhysicsModel runningTest) {
    this.runningTest = runningTest;
  }

  public PhysicsModel getRunningTest() {
    return runningTest;
  }

  public void addTestChangeListener(TestChangedListener argListener) {
    listeners.add(argListener);
  }

  public void removeTestChangeListener(TestChangedListener argListener) {
    listeners.remove(argListener);
  }

  public void addTest(PhysicsModel argTest) {
    tests.addElement(new ListItem(argTest));
  }

  public void addCategory(String argName) {
    tests.addElement(new ListItem(argName));
  }

  public PhysicsModel getTestAt(int argIndex) {
    ListItem item = (ListItem) tests.getElementAt(argIndex);
    if (item.isCategory()) {
      return null;
    }
    return item.test;
  }

  public boolean isTestAt(int argIndex) {
    ListItem item = (ListItem) tests.getElementAt(argIndex);
    return !item.isCategory();
  }

  public void clearTestList() {
    tests.removeAllElements();
  }

  public int getTestsSize() {
    return tests.getSize();
  }

  public DefaultComboBoxModel getComboModel() {
    return tests;
  }

  public TestbedSettings getSettings() {
    return settings;
  }

  public class ListItem {
    public String category;
    public PhysicsModel test;

    public ListItem(String argCategory) {
      category = argCategory;
    }

    public ListItem(PhysicsModel argTest) {
      test = argTest;
    }

    public boolean isCategory() {
      return category != null;
    }

    @Override
    public String toString() {
      return isCategory() ? category : test.getTestName();
    }
  }

  public static interface TestChangedListener {
    public void testChanged(PhysicsModel test, int index);
  }
}
