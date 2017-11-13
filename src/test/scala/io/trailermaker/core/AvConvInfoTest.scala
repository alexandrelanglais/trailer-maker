package io.trailermaker.core

import collection.mutable.Stack
import org.scalatest._

import scala.concurrent.duration._
import better.files._
import org.scalatest

import scala.io.BufferedSource
import scala.io.Source
import scala.concurrent.Future
import scala.util.Success

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class AvConvInfoTest extends AsyncFlatSpec with Matchers {
  private val vFile1       = AvConvInfo.readFileInfo(File.resource("duration-2.00.webm"))
  private val vFile2       = AvConvInfo.readFileInfo(File.resource("duration-6.84.webm"))
  private val badVideoInfo = VideoInfo("", 0, 0, 0)

  "AvConv" should "be able to retrieve duration of files" in {
    for {
      file1 <- vFile1
      _ = assert(file1.duration === 2.seconds)

      file2 <- vFile2
      _ = assert(file2.duration === 6.84.seconds)
    } yield Succeeded

  }

  it should "be able to retrieve the file name" in {
    for {
      file1 <- vFile1
      _ = assert(file1.fileName === Some("duration-2.00.webm"))
    } yield Succeeded
  }

  it should "be able to retrieve the metadatas if present" in {
    pending
  }

  it should "be able to retrieve the number of streams" in {
    pending
  }

  it should "be able to retrieve the video codec used" in {
    vFile1.flatMap(vi => assert(vi.videoInfo.map(x => x.codec) === Some("vp8")))
    vFile2.flatMap(vi => assert(vi.videoInfo.map(x => x.codec) === Some("vp8")))
  }

  it should "be able to retrieve the audio codec used" in {
    pending
  }

  it should "be able to retrieve the video resolution" in {
    vFile1.flatMap(vi => {
      assert(vi.videoInfo.map(x => x.height) === Some(314))
      assert(vi.videoInfo.map(x => x.width) === Some(480))
    })
  }

  it should "be able to retrieve the video FPS" in {
    vFile1.flatMap(vi => {
      assert(vi.videoInfo.map(x => x.fps) === Some(29.97))
    })
  }

}
