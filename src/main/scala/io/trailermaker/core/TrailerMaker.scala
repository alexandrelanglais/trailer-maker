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

object TrailerMaker {
  private val sdf = new SimpleDateFormat("HH:mm:ss")
  sdf.setTimeZone(TimeZone.getTimeZone("UTC"))

  def makeTrailer(file: File): Future[File] = {
    val interval = 5000
    val length   = 6000
    for {
      fileInfo: AvConvInfo <- AvConvInfo.readFileInfo(file)
      ivals = (0L until fileInfo.duration.length by interval).toList
      cutFiles <- ivals.flatMap(ival => List(AvConvCutter.cut(file, sdf.format(new Date(ival)), sdf.format(new Date(length)))))
      cut <- cutFiles
      res <- AvConvConcat.concat(List(cut))
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
