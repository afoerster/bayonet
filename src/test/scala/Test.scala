import org.scalatest.FunSuite
import monix.execution.Scheduler.Implicits.global


class Test extends FunSuite {
  test("test") {
    val grand = TargetTask("grand")(Seq()) {
      () => println("grabd")
    }

    val parent = TargetTask("parent")(Seq(grand)) {
      () => println("parent")
    }
    val child = TargetTask("child")(Seq(parent)){
      () => println("child")
    }

    for (x <- child.tasks()) x
  }

}


