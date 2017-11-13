package io.trailermaker.core

import better.files._

import scala.concurrent.Await
import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object TrailerMaker {

  def main(args: Array[String]): Unit = {
    val r = AvConvInfo
      .readFileInfo(file"/tmp/1502289537524872459.webm")
      .map(
        a => println(s"Video duration : ${a.duration} ms")
      )

    Await.result(r, 5.seconds)
  }

}
