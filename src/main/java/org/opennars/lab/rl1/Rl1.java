package org.opennars.lab.rl1;

import org.opennars.gui.NARSwing;
import org.opennars.interfaces.pub.Reasoner;
import org.opennars.main.Nar;
import org.xml.sax.SAXException;
import processing.core.PApplet;
import processing.event.MouseEvent;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Random;

/**
 *
 *
 */
public class Rl1 extends PApplet {
    private Random rng = new Random();

    public Reasoner reasoner;

    public double currentCellCorePosX = 80.0;
    public double currentCellCorePosY = 35.0;

    public double cursorPosX = 80.0;
    public double cursorPosY = 35.0;
    public double cursorDirX = 1.0;
    public double cursorDirY = 0.0;
    public double distanceFromCenter = 0.0;

    public double differenceToBorder = Double.POSITIVE_INFINITY;

    public int stepsSinceLastBorder = 0; // steps since the last contact with the border was made (for training)
    private String lastClassificationNarsese;


    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "org.opennars.lab.rl1.Rl1" };
        PApplet.main(appletArgs);
    }

    public void setup() {
        size(500, 500);

        try {
            reasoner = new Nar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ((Nar)reasoner).narParameters.DECISION_THRESHOLD = 0.4f;

        {
            OpMove op = new OpMove(this, true);
            reasoner.addPlugin(op);
            ((Nar)reasoner).memory.addOperator(op);


            OpMove opNeg = new OpMove(this, false);
            reasoner.addPlugin(opNeg);
            ((Nar)reasoner).memory.addOperator(opNeg);
        }

        new NARSwing((Nar)reasoner);
    }

    public long iterationCounter = 0;

    public void step() {
        stepsSinceLastBorder++;


        {
            boolean isOutOfCell = false;

            //if (differenceToBorder == Double.POSITIVE_INFINITY) {
            isOutOfCell = distanceFromCenter > 10;
            //}

            if (isOutOfCell) {
                if (differenceToBorder == Double.POSITIVE_INFINITY) {
                    // is first time out of cell
                    differenceToBorder = 0.0f;
                }
            }
        }



        long classOfPatch_ = (int)((cursorPosX - 80.0) / 2);

        //System.out.println("CLASS "+classOfPatch_);

        String narseseOfClassification = "<class-->[c"+classOfPatch_ + "]>.:|:";
        reasoner.addInput(narseseOfClassification); // send classification to NARS
        lastClassificationNarsese = narseseOfClassification;

        if (differenceToBorder != Double.POSITIVE_INFINITY) {
            //System.out.println(differenceToBorder);

            if( Math.abs(differenceToBorder) <= 4.0) {
                stepsSinceLastBorder = 0;

                // reward positive because it is close to the border
                reasoner.addInput("<be-->goal1>.:|:");
                System.out.println("REWARD: good");
            }
            else {
                // weak negative reward
                if ((iterationCounter %2) == 0) {
                    ///commented for experimentation reasoner.addInput("<be-->good>.:|: %0.1;0.1%");
                    //System.out.println("REWARD: weak bad");
                }
            }
        }

        if (cursorPosX > 400) {
            cursorPosX = 60;
            cursorPosY += 2;
        }

        //if (iterationCounter > 150*5) {
        //    return;
        //}

        if ((iterationCounter %2) == 0) {
            reasoner.addInput("<be-->goal1>!"); // remind of goal
            reasoner.addInput("<dontReset-->goal2>!"); // remind of goal
        }

        { // motor babble

            if (differenceToBorder == Double.POSITIVE_INFINITY) { // if it hasn't yet found the boundary
                if (rng.nextDouble() < 0.7) {
                    if (rng.nextInt(2) == 0) {
                        reasoner.addInput("(^opMovep,{SELF})! :|:");
                    }
                    //else {
                    //    reasoner.addInput("(^opMoven,{SELF})! :|:");
                    //}
                }
            }
            else {
                if (rng.nextDouble() < 0.1) {
                    if (rng.nextInt(2) == 0) {
                        reasoner.addInput("(^opMovep,{SELF})! :|:");
                    }
                    else {
                        reasoner.addInput("(^opMoven,{SELF})! :|:");
                    }
                }
            }


        }

        reasoner.cycles(50);

        if (/*iterationCounter > 200 ||*/ stepsSinceLastBorder > 200 || (distanceFromCenter != Double.POSITIVE_INFINITY && distanceFromCenter > 60.0)) {
            System.out.println("force reset !!!");
            System.out.println("");
            System.out.println("");
            System.out.println("");


            //reasoner.addInput("(--,<dontReset-->goal2>).:|:"); // case with two goals, doesn't work
            reasoner.addInput("(--,<be-->goal1>).:|:");
            System.out.println("REWARD: bad");

            //cursorDirX = rng.nextDouble() * 2.0 - 1.0;
            //cursorDirY = rng.nextDouble() * 2.0 - 1.0;
            //double len = Math.sqrt(cursorDirX*cursorDirX + cursorDirY*cursorDirY);
            //cursorDirX /= len;
            //cursorDirY /= len;

            resetCursorState(currentCellCorePosX, currentCellCorePosY);
        }

        iterationCounter++;
    }

    public void draw(){


        step();

        background(0);

        scale(3.0f);

        ellipse((int)cursorPosX, (int)cursorPosY, 5, 5);

        text(lastClassificationNarsese, 0, 12);

        scale(1.0f/3.0f);

        fill(255);
    }




    public void mouseWheel(MouseEvent event) {
        float e = event.getCount();
    }

    // resets the complete tracking state of the cursor (for training)
    private void resetCursorState(double posX, double posY) {
        iterationCounter = 0;
        differenceToBorder = Double.POSITIVE_INFINITY;
        distanceFromCenter = 0.0;

        stepsSinceLastBorder = 0; // because we are reseted

        cursorPosX = posX;
        cursorPosY = posY;
    }
}
