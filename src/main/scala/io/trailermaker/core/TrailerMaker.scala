package io.trailermaker.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

import better.files._
import io.trailermaker.core.AvConvInfo.sdf

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

final case class TMOptions(interval:     Option[Long] = None,
                           length:       Option[Long] = None,
                           duration:     Option[Long] = None,
                           outputFile:   Option[File] = None,
                           progressFile: Option[File] = None)
final case class Arguments(filePath:     Option[File], opts: Option[TMOptions])

object TrailerMaker extends TrailerMakerBase {
  private val sdf = new SimpleDateFormat("HH:mm:ss.S")
  sdf.setTimeZone(TimeZone.getTimeZone("UTC"))

  def makeTrailer(file: File, options: Option[TMOptions] = None): Future[File] = {

    val defaultOptions = TMOptions(interval = Some(75000L), length = Some(1000L))
    val cutLengths     = options.getOrElse(defaultOptions).length.getOrElse(1000L)
    val outputFile     = options.getOrElse(defaultOptions).outputFile.getOrElse(File.newTemporaryFile(prefix = "concat-"))
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

    for {
      fileInfo: AvConvInfo <- AvConvInfo.readFileInfo(file)
      _ = logger.debug(s"file duration=${fileInfo.duration.length}")
      _ = logger.debug(s"wanted duration=$duration")
      _ = logger.debug(s"cut length=$cutLengths")
      interval = if (duration == 0L) options.getOrElse(defaultOptions).interval.getOrElse(0L)
      else (fileInfo.duration.length.doubleValue() / duration.doubleValue() * cutLengths).longValue()
      _     = logger.debug(s"interval=$interval")
      ivals = (0L until fileInfo.duration.length by interval).toList
      _     = logger.debug(s"splits=${ivals.mkString(",")}")
      futs <- Future.sequence(for {
               ival <- ivals
               _ = processFile.writeText(s"Cutting part ${(ival / fileInfo.duration.length) + 1}/${ivals.length}")
             } yield AvConvCutter.cut(file, sdf.format(new Date(ival)), sdf.format(new Date(cutLengths))))

      _ = processFile.writeText(s"Concatenating parts")
      res <- AvConvConcat.concat(futs, outputFile)
      _ = futs.map(toRm => toRm.delete(true))
    } yield res
  }

  def usage(): Unit = {
    println("Usage: TrailerMaker -f <original-file-path> [options]")
    println("where options can be:")
    println("\t-o <path>: output path for the trailer created")
    println("\t-i <value>: interval in ms between cuts")
    println("\t-l <value>: length in ms of each cut")
    println("\t-d <value>: final duration in ms (overrides interval)")
  }

  @tailrec
  def parseArgs(args: List[String], a: Arguments): Arguments = args match {
    case x :: f :: xs if x.startsWith("-f") => parseArgs(xs, a.copy(filePath = Some(File(f))))
    case x :: f :: xs if x.startsWith("-i") => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(interval = Some(f.toInt)))))
    case x :: f :: xs if x.startsWith("-l") => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(length = Some(f.toInt)))))
    case x :: f :: xs if x.startsWith("-d") => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(duration = Some(f.toInt)))))
    case x :: f :: xs if x.startsWith("-o") => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(outputFile = Some(File(f))))))
    case x :: f :: xs if x.startsWith("-p") => parseArgs(xs, a.copy(opts = Some(a.opts.getOrElse(TMOptions()).copy(progressFile = Some(File(f))))))
    case Nil                                => a
  }

  def main(args: Array[String]): Unit =
    if (args.length == 0) {
      usage()
    } else {
      val ar = parseArgs(args.toList, Arguments(None, None))
      ar.filePath match {
        case None => println("Attribute -f is mandatory")
        case Some(file) => {
          val r = makeTrailer(file, ar.opts).map(v => println(s"Trailer generated at ${v.pathAsString}"))
          Await.result(r, 15.hours)
        }
      }
    }

}
