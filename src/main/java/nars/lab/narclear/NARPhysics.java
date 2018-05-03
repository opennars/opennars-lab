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
package nars.lab.narclear;

import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import nars.lab.launcher.NARGame;
import nars.main.NAR;



public class NARPhysics<P extends PhysicsModel> extends NARGame implements Runnable {
    public final P model;
    public final PhysicsRun phy;
    ExecutorService physExe = Executors.newFixedThreadPool(1);
    private Future<?> phyCycle;

    public NARPhysics(NAR nar, float simulationRate, P model) {
        super(nar);
        this.model = model;
        this.phy = new PhysicsRun(nar,simulationRate, model) {

            @Override
            public void keyPressed(KeyEvent e) {
                NARPhysics.this.keyPressed(e);
            }
          
            
        };
        
    }
    public void keyPressed(KeyEvent e) { }

    @Override
    public void start(float fps, int cyclesPerFrame) {
        phy.controller.setFrameRate((int)fps);        
        super.start(fps, cyclesPerFrame);        
    }
    
    public P getModel() { return model; }
    

    @Override
    public void stop() {
        super.stop();
    }

    
    @Override
    public void init() {
    }

    @Override
    public void cycle() {
        if (phy!=null) {
            
            //wait for previous cycle to finish if it hasnt
            if (phyCycle!=null) {
                try {
                    phyCycle.get();
                } catch (Exception ex) {
                    Logger.getLogger(NARPhysics.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            phyCycle = physExe.submit(this);            
        }
    }

    @Override
    public void run() {
        phy.cycle();        
    }
    
    
    
}
