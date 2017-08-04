import monix.eval.Task

case class TargetTask(name: String)(parents: Seq[TargetTask])(job: () => Unit) {
  def tasks(): Task[Any] = {
    Task.gatherUnordered(parents.map(p => p.tasks()) :+ Task.now()).flatMap(pResults => Task(job()))
  }
}