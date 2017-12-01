package io.trailermaker.core

import better.files._
import io.trailermaker.core.impl.AvConvConcat
import io.trailermaker.core.impl.AvConvCutter
import io.trailermaker.core.impl.AvConvInfo
import io.trailermaker.core.impl.AvConvInfos
import org.scalatest._

import scala.concurrent.Future
import scala.concurrent.duration._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class VideoCutterTest extends AsyncFlatSpec with Matchers with VideoCutterBehaviors {
  private val avConvInfoImpl = AvConvInfos()
  private val avConvCutterImpl = AvConvCutter()

  "AvConvCutter" should behave like videoCutter(avConvInfoImpl, avConvCutterImpl)

}
