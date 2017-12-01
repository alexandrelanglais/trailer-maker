package io.trailermaker.core

import better.files.File
import io.trailermaker.core.impl.AvConvInfo
import org.scalatest.AsyncFlatSpec
import org.scalatest.Succeeded

import scala.concurrent.duration._

trait VideoConcatBehaviors { this: AsyncFlatSpec =>

  protected def videoConcat(infoImpl: => VideoInfos[AvConvInfo], concatImpl: => VideoConcat) {
    it should "be able to concat parts from a video file" in {
      for {
        file <- concatImpl.concat(List(File.resource("concat-1.webm"), File.resource("concat-2.webm")), File("/tmp"), File.newTemporaryFile().name)
        _ = assert(file.exists)

        infos <- infoImpl.readFileInfo(file)
        _ = assert(infos.duration > 0.seconds)
      } yield Succeeded
    }
    it should "work with avi files" in {
      for {
        file <- concatImpl.concat(List(File.resource("concat-1.avi"), File.resource("concat-2.avi")), File("/tmp"), File.newTemporaryFile().name)
        _ = assert(file.exists)

        infos <- infoImpl.readFileInfo(file)
        _ = assert(infos.duration > 0.seconds)
      } yield Succeeded

    }
  }
}
