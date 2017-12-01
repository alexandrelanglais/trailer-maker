package io.trailermaker.core

import better.files._
import io.trailermaker.core.impl.AvConvConcat
import io.trailermaker.core.impl.AvConvInfo
import io.trailermaker.core.impl.AvConvInfos
import org.scalatest._

import scala.concurrent.duration._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class VideoConcatTest extends AsyncFlatSpec with Matchers with VideoConcatBehaviors {
  private val avConvInfos = AvConvInfos()
  private val avConvConcat = AvConvConcat()

  "AvConvConcat" should behave like videoConcat(avConvInfos, avConvConcat)

}
