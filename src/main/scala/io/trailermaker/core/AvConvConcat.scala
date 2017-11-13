package io.trailermaker.core

import better.files.File
import io.trailermaker.core.AvConvCutter.EXE_NAME
import io.trailermaker.core.AvConvCutter.logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._

object AvConvConcat extends TrailerMakerBase {
  def concat(files: List[File]): Future[File] = {
    Future {
      val tmpFile = File.newTemporaryFile()
      val output = File.newTemporaryFile(suffix = ".avi", prefix = "concat-")
      files.map(f => tmpFile.append(s"file '${f.pathAsString}'\n"))

      val out = new StringBuilder
      val err = new StringBuilder

      val ioLogger =
        ProcessLogger((o: String) => out.append(o), (e: String) => err.append(e))

      val cmd = s"$EXE_NAME -y -f concat -safe 0 -i ${tmpFile.pathAsString} -c copy ${output.pathAsString}"
      logger.debug(cmd)
      val s = cmd ! (ioLogger)

      //    parseCutFileString(err.toString)
      println(err.toString)
      output
    }
  }
}
