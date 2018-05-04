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
/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package org.opennars.lab.tictactoe;

import automenta.vivisect.swing.NPanel;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Array;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.opennars.io.events.EventEmitter.EventObserver;
import org.opennars.io.events.Events.CyclesEnd;
import org.opennars.main.NAR;
import org.opennars.entity.Concept;

/**
 * A grid panel of buttons, each associated with a particular NARS term
 * This allows visualizing concept activity and relationships directly on the button
 * in its appearance (font, color, etc..) and lines drawn between them on an overlay.
 * @author me
 */
abstract public class GridButtonPanel extends NPanel implements EventObserver {
    
    public final NAR nar;

    public final ConceptButton[][] b;

    public class ConceptButton extends JButton implements MouseListener, MouseMotionListener, MouseWheelListener  {
        
        public final Concept concept;
        public final int by;
        public final int bx;
        
        boolean hovering = false;
        boolean pressing = false;

        public ConceptButton(Concept c, int x, int y) {
            super(c.name().toString());
            this.concept = c;
            this.bx = x;
            this.by = y;
            
            addMouseListener(this);
            addMouseMotionListener(null);
            addMouseWheelListener(null);
        }

        @Override public void mouseDragged(MouseEvent e) {        }

        @Override public void mouseMoved(MouseEvent e) {        }

        
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            onMouseClick(this, false, e.getWheelRotation());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            onMouseClick(this, true, 0);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            pressing = true;
            onMouse(this, pressing, hovering, 0);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            pressing = false;
            onMouse(this, pressing, hovering, 0);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            hovering = true;
            onMouse(this, pressing, hovering, 0);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hovering = false;
            onMouse(this, pressing, hovering, 0);
        }
            
        
    }
    
    public GridButtonPanel(NAR n, int w, int h) {
        super(new GridLayout(w, h));
    
        this.nar = n;
        
        b = (ConceptButton[][]) Array.newInstance(ConceptButton.class, w, h);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                b[x][y] = new ConceptButton(initTerm(x, y), x, y);
                add(b[x][y]);
            }
        }
    }

    @Override public void paint(Graphics g) {
        super.paint(g);
        for (int i = 0; i < b.length; i++) 
            for (int j = 0; j < b[i].length; j++)
                repaintButton(b[i][j]);
        
        repaintOverlay(g);
    }
    
    public void repaintOverlay(Graphics g) {   }
    public void repaintButton(ConceptButton b) {     }

    
    
    abstract public Concept initTerm(int x, int y);
    
    public void onMouse(ConceptButton c, boolean press, boolean hover, int wheel) {   }
    
    public void onMouseClick(ConceptButton c, boolean press, int wheelRotation) {   }
    
    public ConceptButton getButton(int x, int y) {
        return b[x][y];        
    }

    @Override
    protected void onShowing(boolean showing) {
        nar.memory.event.set(this, showing, CyclesEnd.class);
    }
    
    @Override
    public void event(Class event, Object[] arguments) {
        if (event == CyclesEnd.class) {
            if (updated) {
                repaintLater();
            }
        }
    }

    boolean updated = true;
    
    protected void repaintLater() {
        
        updated = false;
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override public void run() {
                repaint();
                updated = true;
            }            
        });
    }
    
    
}
