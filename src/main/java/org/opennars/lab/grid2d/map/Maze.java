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
package org.opennars.lab.grid2d.map;

import org.opennars.lab.grid2d.main.Cell.Material;
import org.opennars.lab.grid2d.main.Hauto;
import org.opennars.lab.grid2d.main.Hauto.SetMaterial;
import static org.opennars.lab.grid2d.main.Hauto.irand;

/**
 * 
 * @author Tyrant
 */
public class Maze {

    
	public static void buildMaze(Hauto m, int x1, int y1, int x2, int y2) {
            m.forEach(x1,y1,x2,y2, new SetMaterial(Material.StoneWall));
            buildInnerMaze(m,x1+1,y1+1,x2-1,y2-1);
            m.copyReadToWrite();
	}
	
	public static void buildInnerMaze(Hauto m, int x1, int y1, int x2, int y2) {
		
                m.forEach(x1,y1,x2,y2, new SetMaterial(Material.StoneWall));
                
		int w=x2-x1+1; int rw=(w+1)/2;
		int h=y2-y1+1; int rh=(h+1)/2;
		
		int sx=x1+2*irand(rw);
		int sy=y1+2*irand(rh);
                m.at(sx, sy, new SetMaterial(Material.DirtFloor));
		
		int finishedCount=0;
		for (int i=1; (i<(rw*rh*1000))&&(finishedCount<(rw*rh)); i++) {
			int x=x1+2*irand(rw);
			int y=y1+2*irand(rh);
			if (m.at(x,y).material!=Material.StoneWall)
                            continue;
                        
			int dx=(irand(2)==1)?(irand(2)*2-1):0;
			int dy=(dx==0)      ?(irand(2)*2-1):0;
			int lx=x+dx*2;
			int ly=y+dy*2;
			if ((lx>=x1)&&(lx<=x2)&&(ly>=y1)&&(ly<=y2)) {
                            if (m.at(lx,ly).material!=Material.StoneWall) {
                                m.at(x, y, new SetMaterial(Material.DirtFloor));
                                m.at(x+dx, y+dy, new SetMaterial(Material.DirtFloor));
                               m.readCells[x][y].setHeight( (int)(Math.random() * 24 + 1));
                                m.writeCells[x][y].setHeight( (int)(Math.random() * 24 + 1));
                                finishedCount++;
                            }
			}
		}
	}
}
