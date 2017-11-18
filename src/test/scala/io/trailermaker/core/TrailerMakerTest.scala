package io.trailermaker.core

import better.files._
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class TrailerMakerTest extends AsyncFlatSpec with Matchers {
  "TrailerMaker" should "be able to parse all arguments passed" in {
    Future {
      val args = "-f input.webm -l 2000 -i 1000 -o /tmp/output -p process.txt -s 5000".split(" ").toList
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
        pf <- o.progressFile
        _ = assert(pf.name === "process.txt")
        start <- o.start
        _ = assert(start === 5000)
      } yield Succeeded
    }.map(x => assert(x == Some(Succeeded)))
  }

  it should "write progress in a config file if specified" in {
    val args = "-f input.webm -l 2000 -i 1000 -o /tmp/output -p /tmp/process.txt".split(" ").toList
    val a: Arguments = TrailerMaker.parseArgs(args, Arguments(None, None))

    for {
      _ <- TrailerMaker.makeTrailer(File("input.webm"), a.opts)
      pf = a.opts.flatMap(_.progressFile)
      _  = assert(pf.nonEmpty)
      gg = pf.fold(File.newTemporaryFile())(f => f)
      _  = assert(gg.size > 0)
    } yield Succeeded
  }
}
