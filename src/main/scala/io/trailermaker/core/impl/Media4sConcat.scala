package io.trailermaker.core.impl

import better.files.File
import io.trailermaker.core.TrailerMakerBase
import io.trailermaker.core.VideoConcat
import org.matthicks.media4s.video.Preset
import org.matthicks.media4s.video.codec.AudioCodec
import org.matthicks.media4s.video.codec.VideoCodec
import org.matthicks.media4s.video.transcode.FFMPEGTranscoder
import org.matthicks.media4s.video.transcode.TranscodeListener

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._

final case class Media4sConcat() extends TrailerMakerBase with VideoConcat {

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  override def concat(files: List[File], outputDir: File, fileName: String): Future[File] =
    Future {
      val tmpFile         = File.newTemporaryFile(suffix = ".txt")
      val ext             = files.headOption.fold(".avi")(f => f.extension.fold(".avi")(e => e))
      val cleanedFileName = fileName.replaceAll("\\s", "_").trim
      val output          = File(s"${outputDir.pathAsString}/$cleanedFileName$ext").createIfNotExists()

      outputDir.createDirectories()

      files.map(f => tmpFile.append(s"file '${f.pathAsString}'\n"))

      var previous: Double = 0.0
      val listener = new TranscodeListener {
        override def log(message: String): Unit = logger.debug(message)

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

      val t = FFMPEGTranscoder()
        .withArgs("-safe", "0", "-f", "concat")
        .input(tmpFile.toJava)
        .audioCodec(AudioCodec.copy)
        .videoCodec(VideoCodec.copy)
        .withArgs("-metadata", """comment="Created by http://trailermaker.io" """.stripMargin)
        .output(output.toJava)
      t.execute(Some(listener), Some(NICE_VALUE))

      output
    }
}
