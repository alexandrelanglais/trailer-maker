package io.trailermaker.core

import better.files.File
import io.trailermaker.core.AvConvCutter.EXE_NAME
import io.trailermaker.core.AvConvCutter.logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._

object AvConvConcat extends TrailerMakerBase {
  def concat(files: File*): Future[File] = {
    Future {
      files.map()
      val out = new StringBuilder
      val err = new StringBuilder

      val ioLogger =
        ProcessLogger((o: String) => out.append(o), (e: String) => err.append(e))

      val tmpFilePath = File.newTemporaryFile(suffix = ".webm")

      val cmd = s"$EXE_NAME -y -ss $start -i ${file.pathAsString} -t $duration -vcodec copy -acodec copy $tmpFilePath"
      logger.debug(cmd)
      val s = cmd ! (ioLogger)

      //    parseCutFileString(err.toString)
      println(err.toString)
      tmpFilePath
    }
  }
}
