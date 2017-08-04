import org.scalatest.FunSuite
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._


class ParserTest extends FunSuite {
  test("pretty print") {
    print(Parser.readMakefile.prettyPrint)
  }

  test("convert to objects") {
    print(Parser.convert)
  }

  test("run targets") {
    val t = Parser.runTargets("one")
    val r = Await.result(t.runAsync, 4 seconds)
  }


}
