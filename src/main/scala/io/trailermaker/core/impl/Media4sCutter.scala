package io.trailermaker.core.impl

import java.text.SimpleDateFormat
import java.util.TimeZone

import better.files.File
import io.trailermaker.core.TrailerMakerBase
import io.trailermaker.core.VideoCutter
import org.matthicks.media4s.video.Preset
import org.matthicks.media4s.video.VideoUtil
import org.matthicks.media4s.video.codec.AudioCodec
import org.matthicks.media4s.video.codec.VideoCodec
import org.matthicks.media4s.video.transcode.FFMPEGTime
import org.matthicks.media4s.video.transcode.FFMPEGTranscoder
import org.matthicks.media4s.video.transcode.TranscodeListener

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._

final case class Media4sCutter() extends TrailerMakerBase with VideoCutter {

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  override def cut(file: File, start: String, duration: String, processFile: Option[File] = None, part: Option[String] = None): Future[File] =
    Future {
      logger.debug(s"Cutting file ${file.name} from $start and duration $duration")

      val ext         = file.extension.fold("")(_.toString)
      val tmpFilePath = File.newTemporaryFile(suffix = ".webm")

      var previous: Double = 0.0
      val listener = new TranscodeListener {
        override def log(message: String): Unit = {}

        override def progress(percentage: Double,
                              frame:      Int,
                              fps:        Double,
                              q:          Double,
                              size:       Long,
                              time:       Double,
                              bitRate:    Long,
                              elapsed:    Double,
                              finished:   Boolean): Unit =
          previous = percentage
      }
      val sdf = new SimpleDateFormat("HH:mm:ss")
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"))

      val dDur = sdf.parse(duration).getTime / 1000.0
      val t = FFMPEGTranscoder()
        .withArgs("-y", "-ss", start)
        .input(file.toJava)
        .duration(dDur)
        .withArgs("-c:v", "vp8", "-c:a", "libvorbis", "-quality", "good", "-b:v", "600k", "-qmin", "10", "-qmax", "42", "-maxrate", "500k", "-bufsize", "1000k")
        .output(tmpFilePath.toJava)

      logger.debug(t.command.mkString(" "))
      t.execute(Some(listener), Some(NICE_VALUE))

      logger.debug(s"Cutting part ${part.fold("")(_.toString)}")
      processFile.map(_.writeText(s"Cutting part ${part.fold("")(_.toString)}"))

      tmpFilePath
    }

}
