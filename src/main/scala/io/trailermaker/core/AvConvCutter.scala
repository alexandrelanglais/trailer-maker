package io.trailermaker.core

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

import better.files.File
import com.typesafe.scalalogging.LazyLogging
import io.trailermaker.core.AvConvInfo.EXE_NAME
import java.io

import scala.annotation.tailrec
import scala.collection.immutable.Map
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import scala.sys.process._
import scala.util.Success
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object AvConvCutter extends TrailerMakerBase {

  def cut(file: File, start: String, duration: String): Future[File] =
    Future {
      logger.debug(s"Cutting file ${file.name} from $start and duration $duration")
      val out = new StringBuilder
      val err = new StringBuilder

      val ioLogger =
        ProcessLogger((o: String) => out.append(o), (e: String) => err.append(e))

      val ext         = file.extension.fold("")(_.toString)
      val tmpFilePath = File.newTemporaryFile(suffix = ".webm")

//      val cmd = s"$EXE_NAME -y -ss $start -i ${file.pathAsString} -t $duration -vcodec copy -acodec copy $tmpFilePath"
      val cmd = s"$EXE_NAME -y -ss $start -i ${file.pathAsString} -t $duration -c:v vp8 -c:a libvorbis $tmpFilePath"
      logger.debug(cmd)
      val s = cmd ! (ioLogger)

      //    parseCutFileString(err.toString)
      println(err.toString)
      tmpFilePath
    }

//  def parseCutFileString(toString: String): Future[File] = {
//  }
}
