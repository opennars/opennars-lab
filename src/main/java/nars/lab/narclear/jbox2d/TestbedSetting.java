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

/**
 * Defines a setting used in the testbed.
 * @author Daniel Murphy
 */
public class TestbedSetting {
  
  /**
   * Whether the setting effects the engine's behavior or
   * modifies drawing.
   *
   */
  public static enum SettingType {
    DRAWING, ENGINE
  }
  
  /**
   * The type of value this setting pertains to
   */
  public static enum ConstraintType {
    BOOLEAN, RANGE
  }
  
  public final String name;
  public final SettingType settingsType;
  public final ConstraintType constraintType;
  public boolean enabled;
  public int value;
  public final int min;
  public final int max;
  
  public TestbedSetting(String argName, SettingType argType, boolean argValue){
    name = argName;
    settingsType = argType;
    enabled = argValue;
    constraintType = ConstraintType.BOOLEAN;
    min = max = value = 0;
  }
  
  public TestbedSetting(String argName, SettingType argType, int argValue, int argMinimum, int argMaximum){
    name = argName;
    settingsType = argType;
    value = argValue;
    min = argMinimum;
    max = argMaximum;
    constraintType = ConstraintType.RANGE;
    enabled = false;
  }
}
