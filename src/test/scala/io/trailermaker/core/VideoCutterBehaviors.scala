package io.trailermaker.core

import better.files.File
import io.trailermaker.core.impl.AvConvInfo
import io.trailermaker.core.impl.VideoInfo
import org.scalatest.AsyncFlatSpec
import org.scalatest.Succeeded

import scala.concurrent.duration._

trait VideoCutterBehaviors { this: AsyncFlatSpec =>

  protected def videoCutter(infoImpl: => VideoInfos[AvConvInfo], cutterImpl: => VideoCutter) {
    it should "be able cut a part from a video file" in {
      for {
        file <- cutterImpl.cut(File.resource("duration-6.84.webm"), "00:00:01", "00:00:02")
        _ = assert(file.exists)

        infos <- infoImpl.readFileInfo(file)
        _ = assert(infos.duration > 0.seconds)

        file <- cutterImpl.cut(File.resource("duration-6.84.webm"), "00:00:03", "00:00:03")
        _ = assert(file.exists)

        infos <- infoImpl.readFileInfo(file)
        _ = assert(infos.duration > 0.seconds)
      } yield Succeeded
    }
    it should "work with various files" in {
      for {
        file <- cutterImpl.cut(File.resource("duration-2.00.webm"), "00:00:00", "00:00:01")
        _ = assert(file.exists)

        infos <- infoImpl.readFileInfo(file)
        _ = assert(infos.duration > 0.seconds)
      } yield Succeeded

    }

    it should "work with avi files" in {
      for {
        file <- cutterImpl.cut(File.resource("duration-6.84.avi"), "00:00:01", "00:00:02")
        _ = assert(file.exists)

        infos <- infoImpl.readFileInfo(file)
        _ = assert(infos.duration > 0.seconds)
      } yield Succeeded
    }
  }
}
