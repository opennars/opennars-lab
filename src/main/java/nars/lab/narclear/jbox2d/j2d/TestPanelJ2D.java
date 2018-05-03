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
package nars.lab.narclear.jbox2d.j2d;

import java.awt.AWTError;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;
import nars.lab.narclear.jbox2d.PhysicsController;
import nars.lab.narclear.jbox2d.TestbedPanel;
import nars.lab.narclear.jbox2d.TestbedState;


/**
 * @author Daniel Murphy
 */
@SuppressWarnings("serial")
public class TestPanelJ2D extends JPanel implements TestbedPanel {

  public static final int SCREEN_DRAG_BUTTON = 3;

  public static final int INIT_WIDTH = 600;
  public static final int INIT_HEIGHT = 600;

  private Graphics2D dbg = null;
  private Image dbImage = null;

  private int panelWidth;
  private int panelHeight;

  private final PhysicsController controller;

  public TestPanelJ2D(final TestbedState model, final PhysicsController controller) {
    this.controller = controller;
    setBackground(Color.black);
    setPreferredSize(new Dimension(INIT_WIDTH, INIT_HEIGHT));
    updateSize(INIT_WIDTH, INIT_HEIGHT);

    AWTPanelHelper.addHelpAndPanelListeners(this, model, controller, SCREEN_DRAG_BUTTON);
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        updateSize(getWidth(), getHeight());
        dbImage = null;
      }
    });
  }

  public final Graphics2D getDBGraphics() {
    return dbg;
  }

  private void updateSize(int width, int height) {
    panelWidth = width;
    panelHeight = height;
    controller.updateExtents(width / 2, height / 2);
  }

  public boolean render() {
    if (dbImage == null) {
      //System.out.println("dbImage is null, creating a new one");
      if (panelWidth <= 0 || panelHeight <= 0) {
        return false;
      }
      dbImage = createImage(panelWidth, panelHeight);
      if (dbImage == null) {
        System.err.println("dbImage is still null, ignoring render call");
        return false;
      }
      dbg = (Graphics2D) dbImage.getGraphics();
      dbg.setFont(new Font("Courier New", Font.PLAIN, 12));
    }
    dbg.setColor(Color.black);
    dbg.fillRect(0, 0, panelWidth, panelHeight);
    return true;
  }

  public void paintScreen() {
    try {
      Graphics g = this.getGraphics();
      if ((g != null) && dbImage != null) {
        g.drawImage(dbImage, 0, 0, null);
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
      }
    } catch (AWTError e) {
      System.err.println("Graphics context error" + e);
    }
  }
}
