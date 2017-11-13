package io.trailermaker.core

import better.files._
import org.scalatest._

import scala.concurrent.duration._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class TrailerMakerTest extends AsyncFlatSpec with Matchers {
  "TrailerMaker" should "be able to make a trailer from a video file" in {
    for {
      file <- TrailerMaker.makeTrailer(File("/tmp/futfut.avi"))
      _ = assert(file.exists)

//      infos <- AvConvInfo.readFileInfo(file)
//      _ = assert(infos.duration === 7.54.seconds)
    } yield Succeeded
  }
}
