package io.trailermaker.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

import better.files._
import io.trailermaker.core.AvConvInfo.sdf

import scala.concurrent.Await
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

final case class TMOptions(interval: Int, length: Int)

object TrailerMaker extends TrailerMakerBase {
  private val sdf = new SimpleDateFormat("HH:mm:ss")
  sdf.setTimeZone(TimeZone.getTimeZone("UTC"))

  def makeTrailer(file: File, options: Option[TMOptions] = None): Future[File] = {
    val defaultOptions = TMOptions(35000, 1000)
    val interval       = options.getOrElse(defaultOptions).interval
    val length         = options.getOrElse(defaultOptions).length
    for {
      fileInfo: AvConvInfo <- AvConvInfo.readFileInfo(file)
      _     = logger.debug(s"file duration=${fileInfo.duration.length}")
      ivals = (0L until fileInfo.duration.length by interval).toList
      _     = logger.debug(s"splits=${ivals.mkString(",")}")
      futs <- Future.sequence(for {
               ival <- ivals
             } yield AvConvCutter.cut(file, sdf.format(new Date(ival)), sdf.format(new Date(length))))

      res <- AvConvConcat.concat(futs)
      _ = futs.map(toRm => toRm.delete(true))
    } yield res
  }

  def main(args: Array[String]): Unit = {
    val r = AvConvInfo
      .readFileInfo(file"/tmp/1502289537524872459.webm")
      .map(
        a => println(s"Video duration : ${a.duration} ms")
      )

    Await.result(r, 5.seconds)
  }

}
