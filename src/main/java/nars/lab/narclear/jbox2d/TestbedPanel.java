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
 * Created at 3:13:48 AM Jul 17, 2010
 */
package nars.lab.narclear.jbox2d;

/**
 * A TestbedPanel encapsulates the graphical panel displayed to the user. Also it is responsible for
 * populating panel-specific data in the model (like panel width).
 * 
 * @author Daniel Murphy
 */
public interface TestbedPanel {

  public void grabFocus();

  /**
   * Renders the world
   * @return if the renderer is ready for drawing
   */
  public boolean render();

  /**
   * Paints the rendered world to the screen
   */
  public void paintScreen();
}
