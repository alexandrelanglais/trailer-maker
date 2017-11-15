package io.trailermaker.core

import better.files._
import org.scalatest._

import scala.concurrent.duration._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class TrailerMakerTest extends FlatSpec with Matchers {
  "TrailerMaker" should "be able to parse all arguments passed" in {
    val args = "-f input.webm -l 2000 -i 1000 -o output".split(" ").toList
    val a: Arguments = TrailerMaker.parseArgs(args, Arguments(None, None))
    assert(a.filePath.nonEmpty)
    assert(a.opts.nonEmpty)
    for {
      o <- a.opts
      length <- o.length
      _ = assert(length === 2000)
      interval <- o.interval
      _ = assert(interval === 1000)
      out <- o.outputFile
      _ = assert(out.name === "output")
    } yield Succeeded
  }
}
