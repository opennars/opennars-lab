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
/**
 * Created at 1:58:18 PM Jul 17, 2010
 */
package nars.lab.narclear.jbox2d;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import nars.lab.narclear.jbox2d.TestbedSetting.SettingType;

/**
 * Stores all the testbed settings.  Automatically populates default settings.
 * 
 * @author Daniel Murphy
 */
public class TestbedSettings {
  public static final String Hz = "Hz";
  public static final String PositionIterations = "Pos Iters";
  public static final String VelocityIterations = "Vel Iters";
  public static final String AllowSleep = "Sleep";
  public static final String WarmStarting = "Warm Starting";
  public static final String SubStepping = "SubStepping";
  public static final String ContinuousCollision = "Continuous Collision";
  public static final String DrawShapes = "Shapes";
  public static final String DrawJoints = "Joints";
  public static final String DrawAABBs = "AABBs";
  public static final String DrawContactPoints = "Contact Points";
  public static final String DrawContactNormals = "Contact Normals";
  public static final String DrawContactImpulses = "Contact Impulses";
  public static final String DrawFrictionImpulses = "Friction Impulses";
  public static final String DrawCOMs = "Center of Mass";
  public static final String DrawStats = "Stats";
  public static final String DrawHelp = "Help";
  public static final String DrawTree = "Dynamic Tree";
  public static final String DrawWireframe = "Wireframe Mode";

  public boolean pause = false;
  public boolean singleStep = false;

  private List<TestbedSetting> settings;
  private final Map<String, TestbedSetting> settingsMap;

  public TestbedSettings() {
    settings = Lists.newArrayList();
    settingsMap = Maps.newHashMap();
    populateDefaultSettings();
  }

  private void populateDefaultSettings() {
    addSetting(new TestbedSetting(Hz, SettingType.ENGINE, 60, 1, 400));
    addSetting(new TestbedSetting(PositionIterations, SettingType.ENGINE, 3, 0, 100));
    addSetting(new TestbedSetting(VelocityIterations, SettingType.ENGINE, 8, 1, 100));
    addSetting(new TestbedSetting(AllowSleep, SettingType.ENGINE, true));
    addSetting(new TestbedSetting(WarmStarting, SettingType.ENGINE, true));
    addSetting(new TestbedSetting(ContinuousCollision, SettingType.ENGINE, true));
    addSetting(new TestbedSetting(SubStepping, SettingType.ENGINE, false));
    addSetting(new TestbedSetting(DrawShapes, SettingType.DRAWING, true));
    addSetting(new TestbedSetting(DrawJoints, SettingType.DRAWING, true));
    addSetting(new TestbedSetting(DrawAABBs, SettingType.DRAWING, false));
    addSetting(new TestbedSetting(DrawContactPoints, SettingType.DRAWING, false));
    addSetting(new TestbedSetting(DrawContactNormals, SettingType.DRAWING, false));
    addSetting(new TestbedSetting(DrawContactImpulses, SettingType.DRAWING, false));
    addSetting(new TestbedSetting(DrawFrictionImpulses, SettingType.DRAWING, false));
    addSetting(new TestbedSetting(DrawCOMs, SettingType.DRAWING, false));
    addSetting(new TestbedSetting(DrawStats, SettingType.DRAWING, false));
    addSetting(new TestbedSetting(DrawHelp, SettingType.DRAWING, false));
    addSetting(new TestbedSetting(DrawTree, SettingType.DRAWING, false));
    addSetting(new TestbedSetting(DrawWireframe, SettingType.DRAWING, true));
  }

  /**
   * Adds a settings to the settings list
   * @param argSetting
   */
  public void addSetting(TestbedSetting argSetting) {
    if (settingsMap.containsKey(argSetting.name)) {
      throw new IllegalArgumentException("Settings already contain a setting with name: "
          + argSetting.name);
    }
    settings.add(argSetting);
    settingsMap.put(argSetting.name, argSetting);
  }

  /**
   * Returns an unmodifiable list of settings
   * @return
   */
  public List<TestbedSetting> getSettings() {
    return Collections.unmodifiableList(settings);
  }

  /**
   * Gets a setting by name.
   * @param argName
   * @return
   */
  public TestbedSetting getSetting(String argName) {
    return settingsMap.get(argName);
  }
}
