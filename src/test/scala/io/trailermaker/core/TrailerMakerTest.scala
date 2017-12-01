package io.trailermaker.core

import better.files._
import io.trailermaker.core.impl.AvConvConcat
import io.trailermaker.core.impl.AvConvCutter
import io.trailermaker.core.impl.AvConvInfos
import io.trailermaker.core.impl.Media4sConcat
import io.trailermaker.core.impl.Media4sCutter
import io.trailermaker.core.impl.Media4sInfo
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class TrailerMakerTest extends AsyncFlatSpec with Matchers with TrailerMakerBehaviors {
  "TrailerMaker with AvConv" should behave like makeTrailer(AvConvInfos(), AvConvCutter(), AvConvConcat())
  "TrailerMaker with Media4s" should behave like makeTrailer(Media4sInfo(), Media4sCutter(), Media4sConcat())
}
