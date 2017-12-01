package io.trailermaker.core

import better.files.File
import io.trailermaker.core.impl.AvConvInfo
import io.trailermaker.core.impl.VideoInfo
import org.scalatest.AsyncFlatSpec
import org.scalatest.FlatSpec
import org.scalatest.Succeeded

import scala.concurrent.duration._

trait VideoInfosBehaviors { this: AsyncFlatSpec =>

  protected def videoInfos(infoImpl: => VideoInfos[AvConvInfo]) {
    val vFile1       = File.resource("duration-2.00.webm")
    val vFile2       = File.resource("duration-6.84.webm")
    val aviFile1     = File.resource("duration-2.00.avi")
    val badVideoInfo = VideoInfo("", 0, 0, 0)

    it should "be able to retrieve duration of files" in {
      for {
        file1 <- infoImpl.readFileInfo(vFile1)
        _ = assert(file1.duration > 0.seconds)

        file2 <- infoImpl.readFileInfo(vFile2)
        _ = assert(file2.duration > 0.seconds)
      } yield Succeeded

    }

    it should "be able to retrieve the file name" in {
      for {
        file1 <- infoImpl.readFileInfo(vFile1)
        _ = assert(file1.fileName === Some("duration-2.00.webm"))
      } yield Succeeded
    }

    it should "be able to retrieve the metadatas if present" in {
      pending
      for {
        file1 <- infoImpl.readFileInfo(vFile2)
        _ = assert(file1.metadatas === Some("title:Knopfler,encoder:Lavf55.36.101,"))
      } yield Succeeded
    }

    it should "be able to retrieve the number of streams" in {
      pending
    }

    it should "be able to retrieve the video codec used" in {
      infoImpl.readFileInfo(vFile1).flatMap(vi => assert(vi.videoInfo.map(x => x.codec) === Some("vp8")))
      infoImpl.readFileInfo(vFile2).flatMap(vi => assert(vi.videoInfo.map(x => x.codec) === Some("vp8")))
    }

    it should "be able to retrieve the audio codec used" in {
      pending
    }

    it should "be able to retrieve the video resolution" in {
      infoImpl
        .readFileInfo(vFile1)
        .flatMap(vi => {
          assert(vi.videoInfo.map(x => x.height) === Some(314))
          assert(vi.videoInfo.map(x => x.width) === Some(480))
        })
    }

    it should "be able to retrieve the video FPS" in {
      infoImpl
        .readFileInfo(vFile1)
        .flatMap(vi => {
          assert(vi.videoInfo.map(x => x.fps) === Some(29.97))
        })
    }

    it should "be able to retrieve duration of avi files" in {
      for {
        file1 <- infoImpl.readFileInfo(aviFile1)
        _ = assert(file1.duration > 0.seconds)
      } yield Succeeded

    }
  }
}
