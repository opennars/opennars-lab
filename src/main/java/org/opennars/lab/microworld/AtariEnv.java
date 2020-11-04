package org.opennars.lab.microworld;

import org.opennars.entity.Concept;
import org.opennars.entity.Task;
import org.opennars.gui.NARSwing;
import org.opennars.interfaces.Timable;
import org.opennars.interfaces.pub.Reasoner;
import org.opennars.io.Narsese;
import org.opennars.io.Parser;
import org.opennars.lab.metric.MetricReporter;
import org.opennars.language.Term;
import org.opennars.main.Nar;
import org.opennars.operator.Operation;
import org.opennars.operator.Operator;
import org.opennars.storage.Memory;

import org.xml.sax.SAXException;
import processing.core.PApplet;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO< positive reinforcement when it has hit the alien >
// TODO< add goal to hit alien >

public class AtariEnv {
    public MetricReporter metricReporter;

    public static class MyFrame extends PApplet {
        public AtariEnv atariEnv;

        @Override
        public void setup() {
            size(800, 600);
            frameRate(30);
        }

        @Override
        public void draw() {
            update();
            drawInternal();
        }

        private void drawInternal() {
            background(127);
            fill(0);

            for (Entity iEntity : atariEnv.entities) {
                fill(255);
                ellipse((int)iEntity.posX, (int)iEntity.posY, 10, 10);
            }
        }

        void update() {
            atariEnv.update();
        }
    }

    int actionLast = 0;

    public float alpha=0.1f; // how random is the action selection

    public Random rng = new Random();

    long timeCounter = 0;

    String oldInput = "";

    long entityIdCounter = 0;

    private void update() {

        { // change direction of aliens
            for(Entity iEntity : entities) {
                if (iEntity.tag.equals("alien")) {
                    if (iEntity.posX > 200) {
                        iEntity.velX = -Math.abs(iEntity.velX);
                    }
                    if (iEntity.posX < 0) {
                        iEntity.velX = Math.abs(iEntity.velX);
                    }
                }
            }
        }

        { // move
            for(Entity iEntity : entities) {
                iEntity.posX += iEntity.velX;
                iEntity.posY += iEntity.velY;
            }
        }

        boolean agentDestroyedAlien = false;
        { // check hit of alien
            for(int idx=entities.size()-1;idx>=0;idx--) {
                // remove hit aliens which moved off screen
                if (entities.get(idx).tag.equals("alien")) {
                    boolean isHit = false;

                    for(Entity iOther : entities) {
                        if (iOther.tag.equals("bullet")) {
                            double diffX = iOther.posX - entities.get(idx).posX;
                            double diffY = iOther.posY - entities.get(idx).posY;
                            double dist = Math.sqrt(diffX*diffX+diffY*diffY);
                            if (dist < 30) {
                                iOther.remove = true;
                                isHit = true;
                            }
                        }
                    }

                    if (isHit) {
                        agentDestroyedAlien = true;
                        entities.get(idx).remove = true;
                    }
                }

                // remove bullets which moved off screen
                entities.get(idx).remove |= entities.get(idx).posY < 0.0;
            }

            for(int idx=entities.size()-1;idx>=0;idx--) {
                if (entities.get(idx).remove) {
                    entities.remove(idx);
                }
            }
        }

        { // remove entities
            for(int idx=entities.size()-1;idx>=0;idx--) {
                if (entities.get(idx).posY < 0.0) {
                    entities.remove(idx);
                }
            }
        }



        { // respawn alien
            long alienCount = 0;

            // count aliens
            for(Entity iEntity : entities) {
                if (iEntity.tag.equals("alien")) {
                    alienCount++;
                }
            }

            if (alienCount < 2) {
                {
                    Entity e = new Entity();
                    e.id = entityIdCounter++;
                    e.posX = rng.nextDouble() * 200.0;
                    e.posY = rng.nextDouble() * 40.0;
                    e.velX = 2.0;
                    e.tag = "alien";
                    entities.add(e);
                }
            }
        }


        Entity controlledEntity = null;

        for(Entity iEntity : entities) {// search controlled entity
            if (iEntity.tag.equals("controlled")) {
                controlledEntity = iEntity;
                break;
            }
        }

        {


            String newInput = "";

            List<String> nrseses = new ArrayList<>();

            long perceptionIdCounter = 0;

            for(Entity iEntity : entities) {
                // ignore bullet entites for now
                //if (iEntity.tag.equals("bullet")) {
                //    continue;
                //}

                // ignore the controlled entity because we compute relative positions anyways
                if (iEntity.tag.equals("controlled")) {
                    continue;
                }

                int quantizedRelPosX = (int)((iEntity.posX-controlledEntity.posX) / 15.0);
                int quantizedRelPosY = (int)((iEntity.posY-controlledEntity.posY) / 15.0);

                String quantizedRelX = "c";
                if (quantizedRelPosX < 0.0) {
                    quantizedRelX = "neg";
                }
                else if (quantizedRelPosX > 0.0) {
                    quantizedRelX = "pos";
                }
                String nrse = "<{(*,{entityId"+perceptionIdCounter+"},{"+iEntity.tag+"},{"+quantizedRelX+"})} --> [perceptEntity]>. :|: %1.0;0.9%";
                newInput += nrse;
                nrseses.add(nrse);

                perceptionIdCounter++;
            }

            if (!newInput.equals(oldInput)) {
                oldInput = newInput;

                for(String iNrsese: nrseses) {
                    reasoner.addInput(iNrsese);
                }
            }
        }

        {
            if (agentDestroyedAlien) {
                reasoner.addInput("<{SELF} --> [satisfied]>. :|:");
            }
        }

        timeCounter++;
        if(timeCounter%2==0) { //TODO add motivation module that handles current goals of "animals"
            if(timeCounter%10 == 0) { //les priority than eating ^^
                //reasoner.addInput("<{SELF} --> [healthy]>! :|:");
            } else {
                reasoner.addInput("<{SELF} --> [satisfied]>! :|:");
            }
            //System.out.println("food urge input");
        }

        actionLast = 0;

        reasoner.cycles(10);

        // limit position
        for(Entity iEntity : entities) {
            if (iEntity.tag.equals("controlled")) {
                iEntity.posX = Math.min(iEntity.posX, 200);
                iEntity.posX = Math.max(iEntity.posX, 0);
            }
        }

        {
            if (actionLast == 1) { // right
                for(Entity iEntity : entities) {
                    if (iEntity.tag.equals("controlled")) {
                        iEntity.velX = 4.0;
                        iEntity.posX = Math.min(iEntity.posX, 200);
                    }
                }
            }
            if (actionLast == 2) { // left
                for(Entity iEntity : entities) {
                    if (iEntity.tag.equals("controlled")) {
                        iEntity.velX = -4.0;
                        iEntity.posX = Math.max(iEntity.posX, 0);
                    }
                }
            }

            if (actionLast == 3) { // fire bullet
                Entity bulletEntity = new Entity();
                bulletEntity.tag = "bullet";
                bulletEntity.id = entityIdCounter++;
                bulletEntity.posX = controlledEntity.posX;
                bulletEntity.posY = controlledEntity.posY;
                bulletEntity.velY = -30; // bullets move upward
                entities.add(bulletEntity);
            }
        }

        {
            if(actionLast==0 && rng.nextFloat()<alpha) { //if Nar hasn't decided chose a random action
                actionLast = rng.nextInt(actionsN+1);
                Concept actionLeft = null;
                Concept actionRight = null;
                Concept actionFire = null;
                try {
                    actionLeft = ((Nar)reasoner).memory.concept(new Narsese(((Nar)reasoner)).parseTerm("Left({SELF})")); //refine API in the future
                    actionRight = ((Nar)reasoner).memory.concept(new Narsese(((Nar)reasoner)).parseTerm("Right({SELF})")); //innate motivation plugin
                    actionFire = ((Nar)reasoner).memory.concept(new Narsese(((Nar)reasoner)).parseTerm("ActionFire({SELF})")); //innate motivation plugin
                } catch (Parser.InvalidInputException ex) {              //with for instance battery level etc. and their state?
                    Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(actionLast == 1) {
                    if(actionRight != null && !actionRight.allowBabbling) {
                        actionLast = 0;
                    }
                    reasoner.addInput("Right({SELF})! :|:");
                }
                if(actionLast == 2) {
                    if(actionLeft != null && !actionLeft.allowBabbling) {
                        actionLast = 0;
                    }
                    reasoner.addInput("Left({SELF})! :|:");
                }
                if(actionLast == 3) {
                    if(actionFire != null && !actionFire.allowBabbling) {
                        actionLast = 0;
                    }
                    reasoner.addInput("ActionFire({SELF})! :|:");
                }
            }
        }
    }


    public final int actionsN = 3; //for actions since we allow the same randomization phase

    public Reasoner reasoner;


    public AtariEnv() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException {

        metricReporter = new MetricReporter();
        metricReporter.connect("127.0.0.1", 8125);

        reasoner = new Nar();
        ((Nar)reasoner).memory.addOperator(new ActionFire("^ActionFire"));
        ((Nar)reasoner).memory.addOperator(new Left("^Left"));
        ((Nar)reasoner).memory.addOperator(new Right("^Right"));
        ((Nar)reasoner).narParameters.VOLUME = 0;
        new NARSwing(((Nar)reasoner));

        // add entities
        {
            Entity e = new Entity();
            e.id = entityIdCounter++;
            e.posX = 100;
            e.posY = 200;
            e.velX = 4;
            e.tag = "controlled";
            entities.add(e);
        }
    }

    public class ActionFire extends Operator {
        public ActionFire(String name) {
            super(name);
        }

        @Override
        public java.util.List<Task> execute(Operation operation, Term[] args, Memory memory, Timable time) {
            actionLast = 3;
            //memory.allowExecution = false;
            System.out.println("Reasoner decide fire");
            return null;
        }
    }
    public class Right extends Operator {
        public Right(String name) {
            super(name);
        }

        @Override
        public java.util.List<Task> execute(Operation operation, Term[] args, Memory memory, Timable time) {
            actionLast = 1;
            //memory.allowExecution = false;
            System.out.println("Reasoner decide right");
            return null;
        }
    }
    public class Left extends Operator {
        public Left(String name) {
            super(name);
        }

        @Override
        public List<Task> execute(Operation operation, Term[] args, Memory memory, Timable time) {
            actionLast = 2;
            //memory.allowExecution = false;
            System.out.println("Reasoner decide left");
            return null;
        }
    }


    public static class Entity {
        public double posX = 0, posY = 0;
        public double velX = 0, velY = 0;
        public long id;
        public boolean remove = false; // flag to indicate removal of entity

        public String tag = "";
    }

    public List<Entity> entities = new ArrayList<>();


    public static void main(String[] args) throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException {
        String[] args2 = {"AtariEnv"};
        AtariEnv env = new AtariEnv();
        MyFrame applet = new MyFrame();
        applet.atariEnv = env;

        PApplet.runSketch(args2, applet);
    }
}
