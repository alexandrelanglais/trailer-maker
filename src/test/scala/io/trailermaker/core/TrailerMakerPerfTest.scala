package io.trailermaker.core

import io.trailermaker.core.impl._
import org.scalatest._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class TrailerMakerPerfTest extends AsyncFlatSpec with Matchers with TrailerMakerPerfBehaviors {
  //"TrailerMaker Perfs with AvConv" should behave like makeTrailer(AvConvInfos(), AvConvCutter(), AvConvConcat())

  "TrailerMaker Perfs with Media4s" should behave like makeTrailer(Media4sInfo(), Media4sCutter(), Media4sConcat())
}
