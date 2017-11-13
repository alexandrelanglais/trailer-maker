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
  private val vFile1       = File.resource("duration-2.00.webm")
  private val vFile2       = File.resource("duration-6.84.webm")
  private val aviFile1     = File.resource("duration-2.00.avi")
  private val badVideoInfo = VideoInfo("", 0, 0, 0)

  "AvConv" should "be able to retrieve duration of files" in {
    for {
      file1 <- AvConvInfo.readFileInfo(vFile1)
      _ = assert(file1.duration === 2.seconds)

      file2 <- AvConvInfo.readFileInfo(vFile2)
      _ = assert(file2.duration === 6.84.seconds)
    } yield Succeeded

  }

  it should "be able to retrieve the file name" in {
    for {
      file1 <- AvConvInfo.readFileInfo(vFile1)
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
    AvConvInfo.readFileInfo(vFile1).flatMap(vi => assert(vi.videoInfo.map(x => x.codec) === Some("vp8")))
    AvConvInfo.readFileInfo(vFile2).flatMap(vi => assert(vi.videoInfo.map(x => x.codec) === Some("vp8")))
  }

  it should "be able to retrieve the audio codec used" in {
    pending
  }

  it should "be able to retrieve the video resolution" in {
    AvConvInfo
      .readFileInfo(vFile1)
      .flatMap(vi => {
        assert(vi.videoInfo.map(x => x.height) === Some(314))
        assert(vi.videoInfo.map(x => x.width) === Some(480))
      })
  }

  it should "be able to retrieve the video FPS" in {
    AvConvInfo
      .readFileInfo(vFile1)
      .flatMap(vi => {
        assert(vi.videoInfo.map(x => x.fps) === Some(29.97))
      })
  }

  it should "be able to retrieve duration of avi files" in {
    for {
      file1 <- AvConvInfo.readFileInfo(aviFile1)
      _ = assert(file1.duration === 1.97.seconds)
    } yield Succeeded

  }

}
