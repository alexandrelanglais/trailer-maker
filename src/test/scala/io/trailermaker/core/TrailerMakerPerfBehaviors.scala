package io.trailermaker.core

import better.files.File
import io.trailermaker.core.impl.AvConvInfo
import org.scalatest.AsyncFlatSpec
import org.scalatest.Succeeded

import scala.concurrent.Future

trait TrailerMakerPerfBehaviors { this: AsyncFlatSpec =>

  protected def makeTrailer(infoImpl: => VideoInfos[AvConvInfo], cutterImpl: => VideoCutter, concatImpl: => VideoConcat) {
    val tm = TrailerMaker(infoImpl, cutterImpl, concatImpl)
    it should "be fast as shit" ignore {
      val args = "-d 15000 -l 1000 -o /tmp/ --preserve --prepend-length -p process.txt -s 5000".split(" ").toList
      val a: Arguments = tm.parseArgs(args, Arguments(None, None))
      for {
        f <- tm.makeTrailer(File("/tmp/bigfile.mp4"), a.opts)
        _ = assert(f.exists)
      } yield Succeeded
    }

  }
}
