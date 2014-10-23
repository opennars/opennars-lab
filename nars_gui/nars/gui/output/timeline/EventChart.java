/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package nars.gui.output.timeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import nars.core.Events;
import nars.entity.Item;
import nars.gui.NARSwing;
import nars.io.Output;
import nars.util.NARTrace;
import processing.core.PConstants;

/**
 *
 * @author me
 */
public class EventChart extends Chart {
    final NARTrace trace;
    //stores the previous "representative event" for an object as the visualization is updated each time step
    public Map<Object, EventPoint> lastSubjectEvent = new HashMap();
    //all events mapped to their visualized feature
    public Map<Object, EventPoint> events = new HashMap();
    float timeScale;
    float yScale;
    Timeline2DCanvas l;
    boolean includeTaskEvents = true;
    boolean includeOutputEvents = true;
    boolean includeOtherEvents = true;
    private final TreeMap<Long, List<Events.InferenceEvent>> timepoints;

    public static class EventPoint<X> {

        public float x;
        public float y;
        public float z;
        public final X value;
        public final List<EventPoint<X>> incoming = new ArrayList<>();
        public final Object subject;

        public EventPoint(X value, Object subject, float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.subject = subject;
            this.value = value;
        }

        private void set(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public EventChart(NARTrace trace, boolean includeTaskEvents, boolean includeOutputEvents, boolean includeOtherEvents) {
        super();
        this.trace = trace;
        this.includeTaskEvents = includeTaskEvents;
        this.includeOutputEvents = includeOutputEvents;
        this.includeOtherEvents = includeOtherEvents;
        timepoints = new TreeMap();
    }

    @Override
    public void update(Timeline2DCanvas l, float timeScale, float yScale) {
        TreeMap<Long, List<Events.InferenceEvent>> time = trace.time;
        synchronized (timepoints) {
            timepoints.putAll(time.subMap(l.cycleStart, l.cycleEnd));
        }
    }

    @Override
    public void draw(Timeline2DCanvas l, float y, float timeScale, float yScale) {
        this.timeScale = timeScale;
        this.yScale = yScale * height;
        this.l = l;
        if ((timepoints == null) || (timepoints.isEmpty())) {
            return;
        }
        lastSubjectEvent.clear();
        events.clear();
        l.noStroke();
        l.textSize(l.drawnTextScale);
        synchronized (timepoints) {
            //something not quite right about this
            long maxItemsPerCycle = timepoints.values().stream().map((x) -> x.stream().filter((e) -> include(e)).count()).max(Long::compare).get();
            for (Map.Entry<Long, List<Events.InferenceEvent>> e : timepoints.entrySet()) {
                long t = e.getKey();
                List<Events.InferenceEvent> v = e.getValue();
                drawEvent(t, v, y, (int) maxItemsPerCycle);
            }
        }
        l.strokeCap(PConstants.SQUARE);
        l.strokeWeight(2f);
        for (EventPoint<Object> to : events.values()) {
            for (EventPoint<Object> from : to.incoming) {
                l.stroke(256f * NARSwing.hashFloat(to.subject.hashCode()), 100f, 200f, 68f);
                l.line(timeScale * from.x, from.y, timeScale * to.x, to.y);
            }
        }
    }

    public boolean include(Events.InferenceEvent i) {
        if (i instanceof NARTrace.TaskEvent) {
            return includeTaskEvents;
        }
        if (i instanceof NARTrace.OutputEvent) {
            return includeOutputEvents;
        }
        return includeOtherEvents;
    }

    private void drawEvent(long t, List<Events.InferenceEvent> v, float y, int maxItemsPerCycle) {
        if (v.isEmpty()) {
            return;
        }
        float itemScale = Math.min(timeScale / maxItemsPerCycle, yScale / maxItemsPerCycle);
        float x = t;
        y += yScale / maxItemsPerCycle / 2f;
        for (Events.InferenceEvent i : v) {
            if (!include(i)) {
                continue;
            }
            Class c = i.getType();
            //box(2);
            //quad(-0.5f, -0.5f, 0, 0.5f, -0.5f, 0, 0.5f, 0.5f, 0, -0.5f, 0.5f, 0);
            if (i instanceof NARTrace.TaskEvent) {
                NARTrace.TaskEvent te = (NARTrace.TaskEvent) i;
                float p = te.priority;
                {
                    l.fill(256f * NARSwing.hashFloat(c.hashCode()), 200f, 200f);
                    switch (te.type) {
                        case Added:
                            //forward
                            triangleHorizontal(i, te.task, p * itemScale, x, y, 1.0f);
                            break;
                        case Removed:
                            //backwards
                            triangleHorizontal(i, te.task, p * itemScale, x, y, -1.0f);
                            break;
                    }
                }
            } else if (i instanceof NARTrace.OutputEvent) {
                NARTrace.OutputEvent te = (NARTrace.OutputEvent) i;
                float p = 0.5f;
                if (te.signal.length > 0) {
                    if (te.signal[0] instanceof Item) {
                        Item ii = (Item) te.signal[0];
                        if (ii.budget != null) {
                            p = ii.getPriority();
                        } else {
                            p = 0.5f;
                        }
                    }
                }
                float ph = 0.5f + 0.5f * p; //so that priority 0 will still be visible
                l.fill(256f * NARSwing.hashFloat(te.channel.hashCode()), 100f + 100f * ph, 255f * ph);
                if (te.channel.equals(Output.IN.class)) {
                    /*pushMatrix();
                    translate(x*timeScale, y*yScale);
                    rotate(0.65f); //angled diagonally down and to the right                    */
                    triangleHorizontal(i, te.signal, ph * itemScale, x, y, 1.0f);
                    //popMatrix();
                } else if (te.channel.equals(Output.OUT.class)) {
                    //TODO use faster triangleVertical function instead of push and rotate
                    /*pushMatrix();
                    translate(x*timeScale, y*yScale);
                    rotate(MathUtils.HALF_PI); //angled diagonally down and to the right                   */
                    triangleHorizontal(i, te.signal, ph * itemScale, x, y, 1.0f);
                    //popMatrix();
                } /*else if exe... {
                }*/ else {
                    rect(i, te.signal, ph * itemScale, x, y);
                }
            } else {
                l.fill(256f * NARSwing.hashFloat(c.hashCode()), 200f, 200f);
                rect(i, null, 0.75f * itemScale, x, y);
            }
            x += 1.0 / v.size();
            y += yScale / maxItemsPerCycle;
        }
    }

    protected void rect(Object event, Object subject, float r, float x, float y /*, float z*/ ) {
        float px = x * timeScale;
        float py = y;
        if (r < 2) {
            r = 2;
        }
        l.rect(px + -r / 2f, py + -r / 2f, r, r);
        label(event, subject, r, x, y);
    }

    protected void label(Object event, Object subject, float r, float x, float y) {
        if ((l.showItemLabels) && (r * l.drawnTextScale > l.minLabelScale)) {
            // && (r * timeScale > l.minLabelScale * l.drawnTextScale)) {
            l.fill(255f);
            l.text(event.toString(), timeScale * x - r / 2, y);
        }
        setEventPoint(event, subject, x, y, 0);
    }

    protected void triangleHorizontal(Object event, Object subject, float r, float x, float y, float direction) {
        float px = x * timeScale;
        float py = y;
        if (r < 2) {
            r = 2;
        }
        l.triangle(px + direction * -r / 2, py + direction * -r / 2, px + direction * r / 2, py + 0, px + direction * -r / 2, py + direction * r / 2);
        label(event, subject, r, x, y);
    }

    protected void setEventPoint(Object event, Object subject, float x, float y, float z) {
        EventPoint f = new EventPoint(event, subject, x, y, z);
        events.put(event, f);
        if (subject != null) {
            EventPoint e = lastSubjectEvent.put(subject, f);
            if (e != null) {
                f.incoming.add(e);
            }
        }
    }
    
}
