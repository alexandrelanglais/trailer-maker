package io.trailermaker.core

import better.files.File
import io.trailermaker.core.impl.AvConvConcat
import io.trailermaker.core.impl.AvConvCutter
import io.trailermaker.core.impl.AvConvInfo
import io.trailermaker.core.impl.AvConvInfos
import org.scalatest.AsyncFlatSpec
import org.scalatest.Succeeded

import scala.concurrent.Future
import scala.concurrent.duration._

trait TrailerMakerBehaviors { this: AsyncFlatSpec =>

  protected def makeTrailer(infoImpl: => VideoInfos[AvConvInfo], cutterImpl: => VideoCutter, concatImpl: => VideoConcat) {
    val tm = TrailerMaker(infoImpl, cutterImpl, concatImpl)
    it should "be able to parse all arguments passed" in {
      Future {
        val args = "-f input.webm -l 2000 -i 1000 -o /tmp/ --preserve --prepend-length -p process.txt -s 5000".split(" ").toList
        val a: Arguments = tm.parseArgs(args, Arguments(None, None))
        assert(a.filePath.nonEmpty)
        assert(a.opts.nonEmpty)
        for {
          o <- a.opts
          length <- o.length
          _ = assert(length === 2000)
          interval <- o.interval
          _ = assert(interval === 1000)
          out <- o.outputDir
          _ = assert(out.pathAsString === "/tmp")
          pf <- o.progressFile
          _ = assert(pf.name === "process.txt")
          start <- o.start
          _ = assert(start === 5000)
          _ = assert(o.preserve === true)
          _ = assert(o.prependLength === true)
        } yield Succeeded
      }.map(x => assert(x == Some(Succeeded)))
    }

    it should "write progress in a config file if specified" in {
      val args = "-l 2000 -i 1000 -o /tmp/ -p /tmp/process.txt".split(" ").toList
      val a: Arguments = tm.parseArgs(args, Arguments(None, None))

      for {
        _ <- tm.makeTrailer(File.resource("duration-6.84.avi"), a.opts)
        pf = a.opts.flatMap(_.progressFile)
        _  = assert(pf.nonEmpty)
        gg = pf.fold(File.newTemporaryFile())(f => f)
        _  = assert(gg.size > 0)
      } yield Succeeded
    }

    it should "be able to generate the trailer in the specified folder preserving the name" in {
      val args = "-o /tmp/ -p /tmp/process.txt -d 5000 --preserve".split(" ").toList
      val a: Arguments = tm.parseArgs(args, Arguments(None, None))
      for {
        f <- tm.makeTrailer(File.resource("duration-6.84.avi"), a.opts)
        _ = assert(f.pathAsString.startsWith("/tmp/"))
        _ = assert(f.pathAsString.endsWith("duration-6.84.webm"))
      } yield Succeeded
    }

    it should "be able to prepend the length in the trailer file name if a duration is specified" in {
      val args = "-o /tmp/ -p /tmp/process.txt -d 5000 --preserve --prepend-length".split(" ").toList
      val a: Arguments = tm.parseArgs(args, Arguments(None, None))
      for {
        f <- tm.makeTrailer(File.resource("duration-6.84.avi"), a.opts)
        _ = assert(f.pathAsString.startsWith("/tmp/"))
        _ = assert(f.pathAsString.endsWith("duration-6.84.webm"))
        _ = assert(f.name.startsWith("5_seconds_-_"))
      } yield Succeeded
    }

    it should "be able to call main" in {
      val f       = File.resource("duration-6.84.avi")
      val args    = Array("-f", f.pathAsString, "-o", "/tmp/", "-p", "/tmp/process.txt", "-d", "5000", "-l", "1000", "--preserve")
      val resFile = File("/tmp/duration-6.84.webm")
      TrailerMaker.main(args)
      assert(resFile.exists)
    }

    it should "be able to call main with space in path" in {
      val testDir = File("/tmp/trailer maker")
      val f       = File.resource("spaces file name duration 6.84.avi")
      val args    = Array("-f", f.pathAsString, "-o", "/tmp/trailer maker", "-p", "/tmp/trailer maker/process.txt", "-d", "5000", "-l", "1000", "--preserve")
      val resFile = File("/tmp/trailer maker/spaces_file_name_duration_6.84.webm")

      testDir.createDirectories()

      TrailerMaker.main(args)
      assert(resFile.exists)
    }
  }
}
