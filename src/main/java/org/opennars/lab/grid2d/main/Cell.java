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
package org.opennars.lab.grid2d.main;

public class Cell {
    
    public String name="";
    public float light=0.0f;
    public float charge = 0;
    public float value=0;
    public float value2=0;
    public float conductivity = 0.98f;
    public boolean chargeFront = false;
    public float height = 0;
    public Material material;
    public Logic logic;
    public final CellState state;
    public boolean is_solid=false;
    
    public boolean isSolid() {
        return is_solid;
    }

    public enum Material {
        DirtFloor,
        GrassFloor,
        StoneWall,
        Corridor,
        Door, 
        Empty, DirtWall,
        Machine,
        Water,
        Pizza
        //case Tile.Upstairs:
        //case Tile.Downstairs:
        //case Tile.Chest:        
    }
    
    public enum Logic {
        NotALogicBlock,
        AND,
        OR,
        XOR,
        NOT,
        BRIDGE,
        SWITCH,
        OFFSWITCH,
        WIRE, 
        Load,
        UNCERTAINBRIDGE,
    }
    
    public enum Machine {
        Light,
        Turret
    }
    
    public Machine machine;
    
    public Cell() {
        this(new CellState(0, 0));
    }
    
    public Cell(CellState state) {
        this.state = state;
        
        height = 64;
        material = Material.Empty;
        logic=Logic.NotALogicBlock;
        machine = null;
        
        charge=-0.5f; //undefined charge by default
    }
    

    public void setHeightInfinity() {
        height = Float.MAX_VALUE;
        material = Material.StoneWall;
    }
    
    public void drawtext(Grid2DSpace s, String str) {
        s.pushMatrix();
        //
        s.translate(0.2f,0.9f);
        s.text(str,0,0);
        s.popMatrix();
    }
    
    public static float lerp(float current, float next, float speed) {
        return current * (1.0f - speed) + next * (speed);
    }    
    
    
    public void draw(Grid2DSpace s,boolean edge,float wx,float wy,float x,float y, float z) {
        
        int ambientLight = 100;
        
        //draw ground height
        int r=0,g=0,b=0,a=1;
        a = ambientLight;            
         
        if (material == Material.Empty) {
        }
        else if (material == Material.Machine) {
            g = b = 127;
            r = 200;
        }
        else if (material == Material.StoneWall || (material==Material.Door && is_solid)) {
            r = g = b = 255;
        }
        else if (material == Material.DirtFloor || material == Material.GrassFloor || (material==Material.Door && !is_solid)) {
            if (height == Float.MAX_VALUE) {
                r = g = b = 255;
            }
            else { 
                r = g = b = (int)(128 + height);
            }           
        }
        if(material==Material.Door  && is_solid) {
            b=0;
            g=(int) (g/2.0f);
        }
        if(material==Material.Door) {
            r=200;
        }
        if ((charge>0) || (chargeFront)) {
            {
                float freq = 4;
                int chargeBright = (int)((Math.cos(s.getRealtime()*freq)+1) * 25);

                if (charge > 0) {
                    r += chargeBright;
                    g += chargeBright/2;                    
                }
                else {
                    g += chargeBright;
                    r += chargeBright/2;
                }
                if (chargeFront) { 
                    freq = 7;
                    b += 25;
                }
                a += 150;

            }
        }
        if(edge)
        {
            light=255;
        }
        
        a+=light*255;
        //g+=light*128;
        //b+=light*128;
        //r+=light*128;
        
        
        if(material==Material.StoneWall) {
            a=r=g=b=(int) (200+light*255);
            
        }
        if(material==Material.Water) {
            b=64;
            g=32;
        }
        
        r = Math.min(255, r);
        g = Math.min(255, g);
        b = Math.min(255, b);
        a = Math.min(255, a);

        state.cr = lerp(state.cr, r, 0.19f);
        state.cg = lerp(state.cg, g, 0.19f);
        state.cb = lerp(state.cb, b, 0.19f);
        state.ca = lerp(state.ca, a, 0.19f);
        
        if(material==Material.GrassFloor) {
            state.cr+=8;
            state.cg+=16;
        }
        s.fill(state.cr, state.cg, state.cb, state.ca);
        
        boolean full3d=false;
        double v=full3d? 0.5f : 0.0f;
        
        if(logic!=Logic.NotALogicBlock)
        {
            s.fill(state.cr/2.0f);
            s.rect(0,0,1.0f,1.0f);
        }
        else if(material!=Material.Water && material!=Material.StoneWall)
        {
            s.rect(0,0,1.0f,1.0f);
        }
        else
        if(material==Material.Water)
        {
            float verschx=(float) Math.max(-0.5f, Math.min(v,0.05*(x-wx)));
            float verschy=(float) Math.max(-0.5f, Math.min(v,0.05*(y-wy)));
            float add=0.0f; //0.2
            s.rect(add-verschx,add-verschy,1.05f,1.05f);
        }
        else
        if(material==Material.StoneWall || material==Material.Water)
        {
            float verschx=(float) Math.max(-0.3f, Math.min(v,0.05*(x-wx)));
            float verschy=(float) Math.max(-0.3f, Math.min(v,0.05*(y-wy)));
            float add=-0.2f; //0.2
            s.rect(add+verschx,add+verschy,1.1f,1.1f);
            s.rect(add+verschx,add+verschy,1.1f,1.1f);
            
            
            //also try this one, it looks more seamless but less 3d:
            //s.rect(0.2f,0.0f,1.0f,1.0f);
            //s.rect(0.2f,0.0f,1.0f,1.0f);
        }
        
        s.textSize(1);
         if(logic==Logic.SWITCH || logic==Logic.OFFSWITCH)
        {
            s.fill(state.cr+30, state.cg+30, 0, state.ca+30);
            s.ellipse(0.5f, 0.5f, 1.0f, 1.0f);
        }
        else
        if(logic!=Logic.BRIDGE && logic!=Logic.UNCERTAINBRIDGE && logic!=Logic.NotALogicBlock && logic!=Logic.WIRE) {
            //s.fill(state.cr+30, state.cg+30, state.cb+30, state.ca+30);
            s.fill(state.cr+30, state.cg+30, 0, state.ca+30);
            s.triangle(0.25f, 1.0f, 0.5f, 0.5f, 0.75f, 1.0f);
            s.triangle(0.25f, 0.0f, 0.5f, 0.5f, 0.75f, 0.0f);
            s.rect(0, 0.3f, 1, 0.4f);
        }
        else
        if(logic==Logic.WIRE || logic==Logic.BRIDGE || logic==Logic.UNCERTAINBRIDGE) {
            s.fill(state.cr, state.cg, state.cb, state.ca);
            if(logic==Logic.BRIDGE || logic==Logic.UNCERTAINBRIDGE) {
                s.fill(state.cr+30, state.cg+30, 0, state.ca+30);
                s.triangle(0.25f, 0.0f, 0.5f, 0.5f, 0.75f, 0.0f);
                s.rect(0.3f, 0.3f, 0.4f, 0.7f);
            } else { 
                s.rect(0.3f, 0, 0.4f, 1);
            }
            s.rect(0, 0.3f, 1, 0.4f);
        }
         
        s.fill(255,255,255,128);
        if(logic==Logic.AND)
        {
            drawtext(s,"^");
        }
        if(logic==Logic.OR)
        {
            drawtext(s,"v");
        }
        if(logic==Logic.XOR)
        {
            drawtext(s,"x");
        }
        if(logic==Logic.NOT)
        {
            drawtext(s,"~");
        }
        if(logic==Logic.BRIDGE)
        {
            drawtext(s,"H");
        }
        if(logic==Logic.UNCERTAINBRIDGE)
        {
            drawtext(s,"U");
        }
        if(logic==Logic.SWITCH)
        {
            drawtext(s,"1");
        }
        if(logic==Logic.OFFSWITCH)
        {
            drawtext(s,"0");
        }
        
        if (machine!=null) {
            switch (machine) {
                case Light:
                    if (charge > 0)
                        drawtext(s,"+");
                    else
                        drawtext(s,"-");
                    break;
                case Turret:            
                    if (charge > 0)
                        //s.particles.emitParticles(0.5f, 0.3f, s.getTime()/40f, 0.07f, state.x+0.5f, state.y+0.5f, 1);
                    break;
            }
        }
        if(!"".equals(name))
        {
            s.textSize(0.2f);
            s.fill(255,0,0);
            drawtext(s,name);
        }
            
        
    }
    
    static long rseed = System.nanoTime();
    
    public static int nextInt()  {
        final int nbits = 32;
        long x = rseed;
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        rseed = x;
        x &= ((1L << nbits) - 1);
        return (int) x;        
    }

    void setBoundary() {
        setHeightInfinity(); 
    }

    void copyFrom(Cell c) {
        this.material = c.material;
        this.height = c.height;
        this.machine = c.machine;
        this.charge = c.charge;
        this.chargeFront = c.chargeFront;  
        this.light=c.light;
        this.name=c.name+"";
    }

    public void setHeight(int h) {
        this.height = h;
    }
            
    void setLogic(Logic logic, float initialCharge) {
        this.material = Material.Machine;
        this.logic = logic;
        this.charge = initialCharge;
        this.is_solid=false;
    }
}
