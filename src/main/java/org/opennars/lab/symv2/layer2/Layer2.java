package org.opennars.lab.symv2.layer2;

import org.opennars.lab.common.Image2d;
import org.opennars.lab.symv2.imageactorlayer.ImageActor;
import org.opennars.lab.symv2.imageactorlayer.RetinaFovea;

import java.util.List;


// TODO< use of patricks prototype based mechanism >
public class Layer2 {
    public List<ImageActor> imageActors;

    private Image2d canvas;

    // TODO< grouping of close enough points >
    // TODO< acceleration for grouping of close points >

    public void drawTuples(RetinaFovea fovea, List<Tuple> tuples) {
        for(Tuple iTuple : tuples) {
            final int ax = (int)(iTuple.a.positionAndOrientation.position[0] - (float)fovea.position.getEntry(0, 0));
            final int ay = (int)(iTuple.a.positionAndOrientation.position[1] - (float)fovea.position.getEntry(1, 0));

            final int bx = (int)(iTuple.b.positionAndOrientation.position[0] - (float)fovea.position.getEntry(0, 0));
            final int by = (int)(iTuple.b.positionAndOrientation.position[1] - (float)fovea.position.getEntry(1, 0));

            Renderer.drawLine(canvas, ax, ay, bx, by);
        }
    }

    public static class Tuple {
        public ImageActor a;
        public ImageActor b;

        public Tuple(ImageActor a, ImageActor b) {
            this.a = a;
            this.b = b;
        }
    }
}
