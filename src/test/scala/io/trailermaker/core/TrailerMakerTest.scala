package io.trailermaker.core

import better.files._
import io.trailermaker.core.impl.AvConvConcat
import io.trailermaker.core.impl.AvConvCutter
import io.trailermaker.core.impl.AvConvInfos
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class TrailerMakerTest extends AsyncFlatSpec with Matchers with TrailerMakerBehaviors {
  "TrailerMaker with AvConv" should behave like makeTrailer(AvConvInfos(), AvConvCutter(), AvConvConcat())
}
