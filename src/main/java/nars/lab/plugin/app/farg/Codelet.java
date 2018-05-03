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

package nars.lab.plugin.app.farg;

import nars.storage.Memory;
import nars.entity.BudgetValue;
import nars.entity.Item;
import nars.language.Term;

/**
 *
 * @author patrick.hammer
 */
public class Codelet extends Item<Term> {
    //Codelet is a small amount of code that has a chance to be run.
    //since I believe NAL makes this idea obsolete to a large extent,
    //here we concentrate on parts which are difficult for standard inference like
    //detecting lines in a pixel-matrix:
    //<(*,(*,1,0,0,0),(*,1,0,0,0),(*,1,0,0,0),(*,1,0,0,0)) --> viewjunk>.
    //Linedetector codelets would then inherit from Codelet and its adding/removal 
    //would be regulated by controller
    
    Object args;
    public int timestamp;
    public Object bin=null;
    Term t;
    Memory mem;
    public static int codeletid=0;
    
    public Codelet(BudgetValue budget, Memory mem, Object args) {
        super(budget);
        this.args=args;
        this.mem=mem;
        t=new Term("Codelet"+String.valueOf(codeletid));
        codeletid++;
    }
    
    
    public boolean run(Workspace ws) { return true; }

    @Override
    public Term name() {
        return t; //To change body of generated methods, choose Tools | Templates.
    }
}
