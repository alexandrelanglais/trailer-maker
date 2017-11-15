package io.trailermaker.core

import better.files._
import org.scalatest._

import scala.concurrent.duration._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class AvConvConcatTest extends AsyncFlatSpec with Matchers {
  "AvConvConcat" should "be able to concat parts from a video file" in {
    for {
      file <- AvConvConcat.concat(List(File.resource("concat-1.webm"), File.resource("concat-2.webm")), File.newTemporaryFile())
      _ = assert(file.exists)

      infos <- AvConvInfo.readFileInfo(file)
      _ = assert(infos.duration > 0.seconds)
    } yield Succeeded
  }
  it should "work with avi files" in {
    for {
      file <- AvConvConcat.concat(List(File.resource("concat-1.avi"), File.resource("concat-2.avi")), File.newTemporaryFile())
      _ = assert(file.exists)

      infos <- AvConvInfo.readFileInfo(file)
      _ = assert(infos.duration > 0.seconds)
    } yield Succeeded

  }
}
