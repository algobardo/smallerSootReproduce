import java.io.{FileOutputStream, FileInputStream, File, FilenameFilter}
import java.util
import java.util.Iterator
import java.util.Map
import java.util.zip.{ZipFile, GZIPInputStream}
import soot._
import soot.jimple.AbstractStmtSwitch
import soot.jimple.InvokeExpr
import soot.jimple.InvokeStmt
import soot.jimple.Jimple
import soot.jimple.StringConstant
import soot.options.Options;
import org.rogach.scallop._
import soot.util.PhaseDumper
;
import scala.collection.JavaConversions._

//class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
//  val outDir = opt[String]()
//  val frameworkInstrumentation = toggle("frameworkInstrumentation")
//}


object entry {


  def sootFrameworkInstrumentClasses(pathin: File, pathout: File, additionalClasspath: String): scala.Unit = {
    println("analysing..." + pathin + " output in " + pathout)
    G.reset()
//    Options.v().set_verbose(true);

    Options.v().set_src_prec(Options.src_prec_class)
    Options.v().set_output_format(Options.output_format_class)

    Options.v().set_allow_phantom_refs(true)
        Options.v().set_keep_line_number(true)
    //    Options.v().set_whole_program(true)

    val procDir = new util.ArrayList[String]()
    procDir.add(pathin.getAbsolutePath())

    Options.v().set_process_dir(procDir)
    Options.v().set_output_dir(pathout.getAbsolutePath)

    Options.v().set_soot_classpath(additionalClasspath)

    Options.v().set_include_all(true)
    val excluded = new util.LinkedList[String]()

    excluded.add("android.util") /* Failing jb on MathUtils */

    Options.v().no_bodies_for_excluded()
    Options.v().set_exclude(excluded)



    /* Debugging */
//    val dumpBodies = new util.LinkedList[String]()
//    dumpBodies.add("jb.ls")
//    dumpBodies.add("jtp")
//    dumpBodies.add("bb")
//    dumpBodies.add("bb.lso")
//    dumpBodies.add("bb.pho")
//    dumpBodies.add("bb.ule")
//    dumpBodies.add("bb.lp")
//    Options.v().set_dump_body(dumpBodies)

    /* Workaround for the bug of bb.lp */
    // PhaseOptions.v().setPhaseOption("bb.lp","off")


    Scene.v().loadNecessaryClasses()
    Scene.v().loadBasicClasses()


    val forcedLibrary = new util.LinkedList[String]()

// Disables instrumentation of specific class
    // val allClasses = Scene.v().getClasses.iterator()
    // while(allClasses.hasNext){

    //   val cl = allClasses.next
    //   if(cl.resolvingLevel() == SootClass.BODIES &&
    //     (cl.implementsInterface("java.lang.annotation.Annotation")
    //       || forcedLibrary.contains(cl.getName))
    //   ) {
    //     println("Excluded " + cl)
    //     cl.setLibraryClass()
    //   }

    // }

    PackManager.v().runPacks()

    if(!Options.v().oaat())
      PackManager.v().writeOutput()


  }



  def main(args: Array[String]): scala.Unit = {
    
    if(args.size == 0) {
      sootFrameworkInstrumentClasses(new File("./res/classes"), new File("./out/"), "./classpath/core.jar:./classpath/ext.jar:./classpath/bouncycastle.jar:./classpath/conscrypt.jar:./classpath/corejunit.jar")
    }
    
  }
}