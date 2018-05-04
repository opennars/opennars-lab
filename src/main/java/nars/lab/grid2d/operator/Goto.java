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
package nars.lab.grid2d.operator;
 
import java.util.List;
import nars.storage.Memory;
import nars.entity.Task;
import nars.lab.grid2d.main.TestChamber;
import nars.language.Term;
import nars.operator.Operation;
import nars.operator.Operator;

/**
 *  A class used as a template for Operator definition.
 * TODO: memory.registerOperator(new Goto("^goto"));
 */
public class Goto extends Operator {

    TestChamber chamb;
    public Goto(TestChamber chamb, String name) {
        super(name);
        this.chamb=chamb;
    }

    @Override
    protected List<Task> execute(Operation operation, Term[] args, Memory memory) {
        //Operation content = (Operation) task.getContent();
        //Operator op = content.getOperator();
         
        TestChamber.executed=true;
        TestChamber.executed_going=true;
        System.out.println("Executed: " + this);
        for (Term t : args) {
            if(t.equals(Term.SELF))
                continue;
            System.out.println(" --- " + t);
            chamb.operateObj(t.toString(),"go-to");
            break;
        }
        
        
       // if(nars.grid2d.Grid2DSpace.world_used) {
            //ok lets start pathfinding tool
            //nars.grid2d.Grid2DSpace.pathFindAndGoto(arg);
       // }
        
       
        
        return null;
    }

}