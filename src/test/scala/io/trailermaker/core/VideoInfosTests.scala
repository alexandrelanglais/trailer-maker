package io.trailermaker.core

import collection.mutable.Stack
import org.scalatest._

import scala.concurrent.duration._
import better.files._
import io.trailermaker.core.impl.AvConvCutter
import io.trailermaker.core.impl.AvConvInfo
import io.trailermaker.core.impl.AvConvInfos
import io.trailermaker.core.impl.Media4sInfo
import io.trailermaker.core.impl.VideoInfo
import org.scalatest

import scala.io.BufferedSource
import scala.io.Source
import scala.concurrent.Future
import scala.util.Success

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class VideoInfosTests extends AsyncFlatSpec with Matchers with VideoInfosBehaviors {
  private def avConfInfoImpl = AvConvInfos()
  private def m4sInfoImpl    = Media4sInfo()

  //"AvConvInfo" should behave like videoInfos(avConfInfoImpl)

  "Media4sInfo" should behave like videoInfos(m4sInfoImpl)
}
