import org.scalatest.FunSuite

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

case class Target(name: String)( parents: Seq[Target])(job: () => Unit) {
  def tasks(): Task[Any] = {
    Task.gatherUnordered(parents.map(x => x.tasks()) :+ Task.now()).flatMap((b: scala.List[Any]) => Task(job()))
  }
}

class Test extends FunSuite {
  test("test") {
    val parent = Target("parent")(Seq()) {
      () => println("parent")
    }
    val child = Target("child")(Seq(parent)){
      () => println("child")
    }

    for (x <- child.tasks()) x
  }
}
