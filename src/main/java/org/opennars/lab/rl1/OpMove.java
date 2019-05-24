package org.opennars.lab.rl1;

import org.opennars.entity.Task;
import org.opennars.interfaces.Timable;
import org.opennars.language.Term;
import org.opennars.operator.Operation;
import org.opennars.operator.Operator;
import org.opennars.storage.Memory;

import java.util.List;

public class OpMove  extends Operator {
    private final boolean isPositive;

    public OpMove(Rl1 app, boolean isPositive) {
        super("^opMove" + (isPositive ? "p" : "n"));
        this.app = app;
        this.isPositive = isPositive;
    }

    @Override
    protected List<Task> execute(Operation operation, Term[] args, Memory memory, Timable time) {
        if (!isPositive && app.distanceFromCenter <= 0.0) {
            return null; // do ignore because we cant move past the center
        }

        float directionWithStepsize = isPositive ? 2 : -2;
        app.cursorPosX += (directionWithStepsize * app.cursorDirX);
        app.cursorPosY += (directionWithStepsize * app.cursorDirY);

        app.differenceToBorder += directionWithStepsize;
        app.distanceFromCenter += directionWithStepsize;

        return null;
    }

    private final Rl1 app;
}
