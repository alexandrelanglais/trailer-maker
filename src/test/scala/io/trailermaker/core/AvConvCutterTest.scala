package io.trailermaker.core

import better.files._
import org.scalatest._

import scala.concurrent.Future
import scala.concurrent.duration._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class AvConvCutterTest extends AsyncFlatSpec with Matchers {
  "AvConvCutter" should "be able cut a part from a video file" in {
    for {
      file <- AvConvCutter.cut(File.resource("duration-6.84.webm"), "00:00:01", "00:00:02")
      _ = assert(file.exists)

      infos <- AvConvInfo.readFileInfo(file)
      _ = assert(infos.duration > 0.seconds)

      file <- AvConvCutter.cut(File.resource("duration-6.84.webm"), "00:00:03", "00:00:03")
      _ = assert(file.exists)

      infos <- AvConvInfo.readFileInfo(file)
      _ = assert(infos.duration > 0.seconds)
    } yield Succeeded
  }
  it should "work with various files" in {
    for {
      file <- AvConvCutter.cut(File.resource("duration-2.00.webm"), "00:00:00", "00:00:01")
      _ = assert(file.exists)

      infos <- AvConvInfo.readFileInfo(file)
      _ = assert(infos.duration > 0.seconds)
    } yield Succeeded

  }

  it should "work with avi files" in {
    for {
      file <- AvConvCutter.cut(File.resource("duration-6.84.avi"), "00:00:01", "00:00:02")
      _ = assert(file.exists)

      infos <- AvConvInfo.readFileInfo(file)
      _ = assert(infos.duration > 0.seconds)
    } yield Succeeded
  }
}
