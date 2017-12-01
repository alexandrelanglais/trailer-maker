package io.trailermaker.core

import better.files.File

import scala.concurrent.Future

trait VideoInfos[T] {
  def readFileInfo(file: File): Future[T]
}

trait VideoCutter {
  def cut(file: File, start: String, duration: String, processFile: Option[File] = None, part: Option[String] = None): Future[File]
}

trait VideoConcat {
  def concat(files: List[File], outputDir: File, fileName: String): Future[File]
}
