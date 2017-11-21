package io.trailermaker.core

import better.files.File
import io.trailermaker.core.AvConvCutter.EXE_NAME
import io.trailermaker.core.AvConvCutter.logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._

object AvConvConcat extends TrailerMakerBase {

  def concat(files: List[File], outputDir: File, fileName: String): Future[File] =
    Future {
      val tmpFile         = File.newTemporaryFile(suffix = ".txt")
      val ext             = files.headOption.fold(".avi")(f => f.extension.fold(".avi")(e => e))
      val cleanedFileName = fileName.replaceAll("\\s", "_").trim
      val output          = File(s"${outputDir.pathAsString}/${cleanedFileName}${ext}").createIfNotExists()

      files.map(f => tmpFile.append(s"file '${f.pathAsString}'\n"))

      val out = new StringBuilder
      val err = new StringBuilder

      outputDir.createDirectories()

      val ioLogger =
        ProcessLogger((o: String) => out.append(o), (e: String) => err.append(e))

//      val cmd = s"$EXE_NAME -y -safe 0 -f concat -i ${tmpFile.pathAsString} ${output.pathAsString}"
      val cmd = s"$EXE_NAME -y -safe 0 -f concat -i ${tmpFile.pathAsString} -c copy ${output.pathAsString}"
      logger.debug(cmd)
      val s = cmd ! (ioLogger)

      //    parseCutFileString(err.toString)
      println(err.toString)
      output
    }
}
