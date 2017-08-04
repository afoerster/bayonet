import net.jcazevedo.moultingyaml._
import sys.process._

case class TargetConfig(name: String, dependencies: Option[List[String]] = None, command: String)

object Parser {

  def readMakefile = scala.io.Source.fromFile("Makefile.yml")
    .mkString
    .parseYaml

  def convert: Seq[TargetConfig] = {
    object TargetConfigProtocol extends DefaultYamlProtocol {
      implicit val configFormat = yamlFormat3(TargetConfig)
    }

    import TargetConfigProtocol._

    readMakefile.convertTo[Seq[TargetConfig]]
  }

  def buildTargets(configTargets: Seq[TargetConfig]): Map[String, TargetTask] = {
    configTargets.flatMap(c =>
      buildTarget(c, configTargets)).toMap
  }

  def buildTarget(allConfigTargets: TargetConfig, config: Seq[TargetConfig]): Seq[(String, TargetTask)] = {
    val deps = allConfigTargets.dependencies.map { coll =>
      coll.flatMap { depName =>
        val parentTarget = config.filter(_.name == depName)
        parentTarget.headOption match {
          case Some(v) => buildTarget(v, config)
          case None => throw new Exception(s"Could not resolve dependency '$depName' required by '${allConfigTargets.name}")
        }
      }
    }.toSeq.flatten

    val target = new TargetTask(allConfigTargets.name)(deps.map(_._2))(() => (allConfigTargets.command !))
    Seq((allConfigTargets.name, target)) ++ deps
  }

  def runTargets(name: String) = {
    buildTargets(convert)(name).tasks()
  }
}
