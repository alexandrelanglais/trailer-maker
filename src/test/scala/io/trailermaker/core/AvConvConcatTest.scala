package io.trailermaker.core

import better.files._
import org.scalatest._

import scala.concurrent.duration._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class AvConvConcatTest extends AsyncFlatSpec with Matchers {
  "AvConvConcat" should "be able to concat parts from a video file" in {
    for {
      file <- AvConvConcat.concat(File.resource("concat-1.webm"), File.resource("concat-2.webm"))
      _ = assert(file.exists)

      infos <- AvConvInfo.readFileInfo(file)
      _ = assert(infos.duration === 5.4.seconds)
    } yield Succeeded
  }
}
