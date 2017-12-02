package io.trailermaker.core

import better.files._
import io.trailermaker.core.impl.AvConvConcat
import io.trailermaker.core.impl.AvConvCutter
import io.trailermaker.core.impl.AvConvInfo
import io.trailermaker.core.impl.AvConvInfos
import io.trailermaker.core.impl.Media4sCutter
import io.trailermaker.core.impl.Media4sInfo
import org.scalatest._

import scala.concurrent.Future
import scala.concurrent.duration._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class VideoCutterTest extends AsyncFlatSpec with Matchers with VideoCutterBehaviors {
  private val avConvInfoImpl   = AvConvInfos()
  private val avConvCutterImpl = AvConvCutter()

  private val m4sInfoImpl   = Media4sInfo()
  private val m4sCutterImpl = Media4sCutter()

  "AvConvCutter" should behave like videoCutter(avConvInfoImpl, avConvCutterImpl)

  "Media4sCutter" should behave like videoCutter(m4sInfoImpl, m4sCutterImpl)

}
