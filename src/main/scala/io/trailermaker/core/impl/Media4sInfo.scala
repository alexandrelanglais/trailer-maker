package io.trailermaker.core.impl

import better.files.File
import io.trailermaker.core.TrailerMakerBase
import io.trailermaker.core.VideoInfos
import org.matthicks.media4s.video.VideoUtil
import org.matthicks.media4s.video.info.MediaInfo

import scala.concurrent.Future
import scala.concurrent.duration
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

final case class Media4sInfo() extends TrailerMakerBase with VideoInfos[AvConvInfo] {
  override def readFileInfo(file: File) =
    Future {
      val info = VideoUtil.info(file.toJava)
      AvConvInfo(
        info.videoInfo.map(_.codec),
        Some(file.name),
        FiniteDuration(info.duration.longValue() * 1000, duration.MILLISECONDS),
        Some(info.bitRate.intValue()),
        Some(
          VideoInfo(
            info.videoInfo.map(_.codec).getOrElse(""),
            info.videoInfo.map(_.width).getOrElse(0),
            info.videoInfo.map(_.height).getOrElse(0),
            info.videoInfo.map(_.fps / 100).getOrElse(0)
          )),
        Some(info.meta.map.mkString(":"))
      )
    }

//  final case class VideoInfo(codec: String, width: Int, height: Int, fps: Double)
//
//  final case class AvConvInfo(format:    Option[String] = None,
//                              fileName:  Option[String],
//                              duration:  FiniteDuration,
//                              bitRate:   Option[Int] = None,
//                              videoInfo: Option[VideoInfo],
//                              metadatas: Option[String] = None)

}
