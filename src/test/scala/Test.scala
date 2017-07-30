import org.scalatest.FunSuite

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

case class Target(name: String, job: () => Unit, parents: Seq[Target]) {
  def run(): Task[Any] = {
    if (parents.isEmpty) {
      Task.gatherUnordered(Seq(Task(job())))
    } else {
      Task.gatherUnordered(parents.map(x => x.run())).flatMap(x => Task(job()))
    }
  }
}

class Test extends FunSuite {
  test("test") {
    val grand = Target("grand", () => println("grand"), Seq())
    val parent = Target("parent", () => println("parent"), Seq(grand))
    val child = Target("child", () => println("child"), Seq(parent))

    for (x <- child.run()) x
  }
}
