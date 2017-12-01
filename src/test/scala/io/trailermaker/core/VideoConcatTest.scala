package io.trailermaker.core

import better.files._
import io.trailermaker.core.impl.AvConvConcat
import io.trailermaker.core.impl.AvConvInfo
import io.trailermaker.core.impl.AvConvInfos
import io.trailermaker.core.impl.Media4sConcat
import io.trailermaker.core.impl.Media4sCutter
import io.trailermaker.core.impl.Media4sInfo
import org.scalatest._

import scala.concurrent.duration._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class VideoConcatTest extends AsyncFlatSpec with Matchers with VideoConcatBehaviors {
  private val avConvInfos = AvConvInfos()
  private val avConvConcat = AvConvConcat()

  private val m4sInfoImpl = Media4sInfo()
  private val m4sConcatImpl = Media4sConcat()


  "AvConvConcat" should behave like videoConcat(avConvInfos, avConvConcat)

  "Media4sConcat" should behave like videoConcat(m4sInfoImpl, m4sConcatImpl)

}
