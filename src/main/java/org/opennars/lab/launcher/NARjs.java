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
//package org.opennars.lab.launcher;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;
//import javax.script.ScriptException;
//import org.opennars.Nar;
//
///**
// * Javascript Nar Runner
// * @author me
// */
//public class NARjs {
//    final static ScriptEngineManager factory = new ScriptEngineManager();
//    
//    final ScriptEngine js = factory.getEngineByName("JavaScript");
//
//    public NARjs() throws Exception {
//        super();
//        js.eval("load('nashorn:mozilla_compat.js')");
//        
//        js.eval("importPackage('java.lang')");
//        js.eval("importPackage('java.util')");
//        js.eval("importPackage('java.io')");
//
//        js.eval("importPackage('org.opennars.core')");
//        js.eval("importPackage('org.opennars.core.build')");
//        js.eval("importPackage('org.opennars.io')");
//        js.eval("importPackage('org.opennars.gui')");
//        
//        js.eval("function newDefaultNAR() { var x = new Nar(new Default()); new TextOutput(x, System.out); return x; }");
//    }
//
//    public Object eval(String s) throws ScriptException {
//        return js.eval(s);
//    }
//    
//    public static void printHelp() {
//        System.out.println("Help coming soon.");
//    }
//    
//    public static void main(String[] args) throws Exception {
//        NARjs j = new NARjs();
//        
//        System.out.println(Nar.VERSION +  " Javascript Console - :h for help, :q to exit");
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        System.out.print("> ");
//
//        String s;
//        while ((s = br.readLine())!=null) {
//            
//            
//            try {
//                if (s.equals(":q"))
//                    break;
//                else if (s.startsWith(":h")) {
//                    printHelp();
//                    continue;
//                }
//                    
//                Object ret = j.eval(s);
//                
//                if (ret != null) {
//                    System.out.println(ret);
//                }
//            } catch (Exception e) {
//                System.out.println(e.getClass().getName() + " in parsing: " + e.getMessage());
//            } finally {
//
//                
//                System.out.print("> ");
//                
//            }
//        }
//    
//        br.close();
//        System.exit(0);
//    }
//}
