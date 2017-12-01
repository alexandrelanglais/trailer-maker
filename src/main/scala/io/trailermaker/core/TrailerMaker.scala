package io.trailermaker.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.UUID

import better.files._
import io.trailermaker.core.impl.AvConvConcat
import io.trailermaker.core.impl.AvConvCutter
import io.trailermaker.core.impl.AvConvInfo
import io.trailermaker.core.impl.AvConvInfos
import io.trailermaker.core.impl.Media4sConcat
import io.trailermaker.core.impl.Media4sCutter
import io.trailermaker.core.impl.Media4sInfo

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

final case class TMOptions(interval:      Option[Long] = None,
                           length:        Option[Long] = None,
                           duration:      Option[Long] = None,
                           outputDir:     Option[File] = None,
                           progressFile:  Option[File] = None,
                           start:         Option[Long] = None,
                           preserve:      Boolean      = false,
                           prependLength: Boolean      = false)

final case class Arguments(filePath: Option[File], opts: Option[TMOptions])

final case class TrailerMaker(infoImpl: VideoInfos[AvConvInfo], cutterImpl: VideoCutter, concatImpl: VideoConcat) extends TrailerMakerBase {
  private val sdf = new SimpleDateFormat("HH:mm:ss.S")
  sdf.setTimeZone(TimeZone.getTimeZone("UTC"))

  def makeTrailer(file: File, options: Option[TMOptions] = None): Future[File] = {
    val defaultOptions = TMOptions(interval = Some(75000L), length = Some(1000L))
    val cutLengths     = options.getOrElse(defaultOptions).length.getOrElse(1000L)
    val outputDir      = options.getOrElse(defaultOptions).outputDir.getOrElse(File.newTemporaryDirectory(prefix = "concat-"))
    val preserve       = options.getOrElse(defaultOptions).preserve
    val prependLength  = options.getOrElse(defaultOptions).preserve
    val fileNameTmp    = if (preserve) file.nameWithoutExtension(false) else UUID.randomUUID().toString
    val processFile    = options.getOrElse(defaultOptions).progressFile.getOrElse(File.newTemporaryFile(prefix = "process-", suffix = ".txt"))

    processFile.createIfNotExists()

    val duration = options match {
      case Some(opt) =>
        opt.duration match {
          case Some(d) => d
          case None    => 0L
        }
      case None => 0L
    }

    val fileName = if (prependLength && duration > 0) s"${duration / 1000}_seconds_-_$fileNameTmp" else fileNameTmp

    val start = options match {
      case Some(opt) =>
        opt.start match {
          case Some(d) => d
          case None    => 0L
        }
      case None => 0L
    }

    for {
      fileInfo: AvConvInfo <- infoImpl.readFileInfo(file)
      _ = logger.debug(s"file duration=${fileInfo.duration.length}")
      _ = logger.debug(s"wanted duration=$duration")
      _ = logger.debug(s"cut length=$cutLengths")
      interval = if (duration == 0L) options.getOrElse(defaultOptions).interval.getOrElse(0L)
      else (fileInfo.duration.length.doubleValue()/ duration.doubleValue() * cutLengths).longValue()
      _     = logger.debug(s"interval=$interval")
      ivals = (start until fileInfo.duration.length by interval).toList
      _     = logger.debug(s"splits=${ivals.mkString(",")}")
      futs <- Future.sequence(for {
               ival <- ivals
               part = s"${(ival / interval) + 1}/${ivals.length}"
             } yield cutterImpl.cut(file, sdf.format(new Date(ival)), sdf.format(new Date(cutLengths)), Some(processFile), Some(part)))

      _ = processFile.writeText(s"Concatenating parts")
      res <- concatImpl.concat(futs, outputDir, fileName)
      _ = processFile.writeText(s"Complete:${res.name}")
      _ = futs.map(toRm => toRm.delete(true))
    } yield res
  }

  def usage(): Unit = {
    println("Usage: trailer-maker -f <original-file-path> [options]")
    println("where options can be:")
    println("\t-o <path>: output path for the trailer created")
    println("\t-i <value>: interval in ms between cuts")
    println("\t-l <value>: length in ms of each cut")
    println("\t-d <value>: final duration in ms (overrides interval)")
    println("\t-p <value>: path to a text process file (for logging)")
    println("\t-s <value>: time to start in ms in the original video")
    println("\t--preserve: preserves the original file name")
    println("\t--prepend-length: add length at the start of the trailer file name")
  }

  @tailrec
  def parseArgs(args: List[String], a: Arguments): Arguments = args match {
    case x :: f :: xs if x.startsWith("-f")          => parseArgs(xs, a.copy(filePath = Some(File(f))))
    case x :: f :: xs if x.startsWith("-i")          => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(interval = Some(f.toInt)))))
    case x :: f :: xs if x.startsWith("-l")          => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(length = Some(f.toInt)))))
    case x :: f :: xs if x.startsWith("-d")          => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(duration = Some(f.toInt)))))
    case x :: f :: xs if x.startsWith("-o")          => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(outputDir = Some(File(f))))))
    case x :: f :: xs if x.startsWith("-p")          => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(progressFile = Some(File(f))))))
    case x :: f :: xs if x.startsWith("-s")          => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(start = Some(f.toLong)))))
    case x :: xs if x.startsWith("--preserve")       => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(preserve = true))))
    case x :: xs if x.startsWith("--prepend-length") => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(prependLength = true))))
    case Nil                                         => a
  }

}

object TrailerMaker {
  def main(args: Array[String]): Unit = {
    val tm = TrailerMaker(Media4sInfo(), Media4sCutter(), Media4sConcat())
    if (args.length == 0) {
      tm.usage()
    } else {
      val ar = tm.parseArgs(args.toList, Arguments(None, None))
      ar.filePath match {
        case None => println("Attribute -f is mandatory")
        case Some(file) => {
          val r = tm.makeTrailer(file, ar.opts).map(v => println(s"Trailer generated at ${v.pathAsString}"))
          Await.result(r, 15.hours)
        }
      }
    }
  }

}