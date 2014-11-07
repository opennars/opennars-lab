package nars.perf;

import java.util.Collection;
import nars.core.NAR;
import nars.core.Parameters;
import nars.core.build.Discretinuous;
import static nars.perf.NALStressMeasure.perfNAL;
import nars.test.core.NALTest;

/**
 * Runs NALTestPerf continuously, for profiling
 */
public class NALPerfLoop {
    
    public static void main(String[] args) {
       
        int repeats = 2;
        int warmups = 1;
        int maxConcepts = 2000;
        int extraCycles = 2048;
        int randomExtraCycles = 512;
        Parameters.THREADS = 1;
          
        //NAR n = new NeuromorphicNARBuilder().setConceptBagSize(maxConcepts).build();
        //NAR n = new CurveBagNARBuilder().setConceptBagSize(maxConcepts).build();
        //NAR n = new Default().setConceptBagSize(maxConcepts).build();
        NAR n = new Discretinuous().setConceptBagSize(maxConcepts).build();

        //new NARPrologMirror(n,0.75f, true).temporal(true, true);              
        
        Collection c = NALTest.params();
        while (true) {
            for (Object o : c) {
                String examplePath = (String)((Object[])o)[0];
                Parameters.DEBUG = true;
                
                perfNAL(n, examplePath,extraCycles+ (int)(Math.random()*randomExtraCycles),repeats,warmups,true);
            }
        }        
    }
}
