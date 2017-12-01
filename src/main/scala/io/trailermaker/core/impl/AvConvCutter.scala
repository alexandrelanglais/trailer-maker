package io.trailermaker.core.impl

import better.files.File
import io.trailermaker.core.TrailerMakerBase
import io.trailermaker.core.VideoCutter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._

final case class AvConvCutter() extends TrailerMakerBase with VideoCutter {

  override def cut(file: File, start: String, duration: String, processFile: Option[File] = None, part: Option[String] = None): Future[File] =
    Future {
      logger.debug(s"Cutting file ${file.name} from $start and duration $duration")
      val out = new StringBuilder
      val err = new StringBuilder

      val ioLogger =
        ProcessLogger((o: String) => out.append(o), (e: String) => err.append(e))

      val ext         = file.extension.fold("")(_.toString)
      val tmpFilePath = File.newTemporaryFile(suffix = ".webm")

//      val cmd = s"$EXE_NAME -y -ss $start -i ${file.pathAsString} -t $duration -vcodec copy -acodec copy $tmpFilePath"
      val cmd =
        s"$EXE_NAME -y -ss $start -i ${file.pathAsString} -t $duration -c:v vp8 -c:a libvorbis -quality good -b:v 600k -qmin 10 -qmax 42 -maxrate 500k -bufsize 1000k $tmpFilePath"
      logger.debug(cmd)
      val s = cmd ! (ioLogger)

      logger.debug(s"Cutting part ${part.fold("")(_.toString)}")
      processFile.map(_.writeText(s"Cutting part ${part.fold("")(_.toString)}"))

      //    parseCutFileString(err.toString)
//      println(err.toString)
      tmpFilePath
    }

//  def parseCutFileString(toString: String): Future[File] = {
//  }
}
