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

final case class VideoInfo(codec: String, width: Int, height: Int, fps: Double)

final case class AvConvInfo(format:    Option[String] = None,
                            fileName:  Option[String],
                            duration:  FiniteDuration,
                            bitRate:   Option[Int] = None,
                            videoInfo: Option[VideoInfo])

object AvConvInfo extends LazyLogging {
  private val EXE_NAME = "avconv"
  private val sdf      = new SimpleDateFormat("HH:mm:ss.S")

  sdf.setTimeZone(TimeZone.getTimeZone("UTC"))

  def readFileInfo(file: File): Future[AvConvInfo] = {
    val out = new StringBuilder
    val err = new StringBuilder

    val ioLogger =
      ProcessLogger((o: String) => out.append(o), (e: String) => err.append(e))

    val cmd = s"$EXE_NAME -i ${file.pathAsString}"
    logger.debug(cmd)
    val s = cmd ! ioLogger

    parseFileInfoString(err.toString)
  }

  private def parseFileInfoString(string: String): Future[AvConvInfo] = {
    logger.debug(string)
    @tailrec
    def go(str: String, map: Map[String, String]): Map[String, String] =
      str match {
        case x if x startsWith "Duration: " =>
          go(str.substring(12), map + ("duration" -> (x.substring(x.indexOf(' ') + 1, x.indexOf(' ') + 12) + "0")))
        case x if x startsWith "from '" => {
          val inQuote = x.substring(x.indexOf('\'') + 1)
          val path    = inQuote.substring(0, inQuote.indexOf('\''))
          go(str.substring(12), map + ("filePath" -> path))
        }
        case x if x startsWith "Stream #0.0" => {
          val video = x.substring(x.indexOf("Video:") + 7)
          val split = video.split(",")

          val (vcodec: String, res: String, fps: String) =
            if (split.size == 4) (split(0).trim, split(2).trim, "0")
            else (split(0).trim, split(2).trim, split(4).trim.substring(0, split(4).trim.indexOf(' ')))

          val splitRes = res.split("x")
          val (w, h)   = (splitRes(0).trim, splitRes(1).trim)

          map +
            ("vcodec" -> vcodec, "vWidth" -> w, "vHeight" -> h, "fps" -> fps)
        }
        case x if x isEmpty => map
        case _              => go(str.substring(1), map)
      }
    parseInfoMap(go(string, Map.empty[String, String]))
  }

  private def parseInfoMap(infos: Map[String, String]): Future[AvConvInfo] = {
    logger.debug(infos.toString)
    val dur = infos
      .get("duration")
      .map(o => Duration.create(sdf.parse(o).getTime, TimeUnit.MILLISECONDS))
    val fn = infos.get("filePath").map(File(_).name)

    val vInfo = for {
      vCodec <- infos.get("vcodec")
      vWidth <- infos.get("vWidth").map(_.toInt)
      vHeight <- infos.get("vHeight").map(_.toInt)
      fps <- infos.get("fps").map(_.toDouble)
    } yield
      VideoInfo(
        vCodec,
        vWidth,
        vHeight,
        fps
      )

    if (dur.isEmpty) Future.failed(new Exception("Duration could not be retrieved from file"))
    else Future(AvConvInfo(duration = dur.fold(0.seconds)(x => x), fileName = fn, videoInfo = vInfo))

  }

}
