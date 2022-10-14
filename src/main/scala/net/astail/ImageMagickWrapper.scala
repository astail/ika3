package net.astail

import org.joda.time.DateTime

import sys.process._

object ImageMagickWrapper {
  val DESTIMAGEDIR = "./image"

  case class ImageIdentify(format: String, width: Int, height: Int)

  sealed trait Image

  case object Width extends Image

  case object Height extends Image


  def imageNameCheck(image: String): String = {
    if (image.contains("/"))
      image.split("/").last
    else
      image
  }

  def sizeCheck(img: String): ImageIdentify = {
    val checkDataW = Process(s"identify -format %w ${img}").!!.stripLineEnd.toInt
    val checkDataH = Process(s"identify -format %h ${img}").!!.stripLineEnd.toInt
    val checkDataFormat = Process(s"identify -format %m ${img}").!!.stripLineEnd
    ImageIdentify(checkDataFormat, checkDataW, checkDataH)
  }

  def resize(img: String, size: Int, wh: Image): String = {
    val destImage = s"${DESTIMAGEDIR}/${imageNameCheck(img)}"
    try {
      val resizeWH = wh match {
        case Width => s"${size}x"
        case Height => s"x${size}"
      }
      Process(s"convert -resize ${resizeWH} ${img} ${destImage}").!!.stripLineEnd
      destImage
    } catch {
      case e: Throwable => "resize error"
    }
  }

  def imageAppend(images: List[String], wh: Image): String = {
    val time: DateTime = org.joda.time.DateTime.now()
    val check: ImageIdentify = sizeCheck(images.head)
    val imagesNameCheck = images.map(imageNameCheck)
    val mergeImage = images.mkString(",").replace(",", " ")
    val destImage = s"${DESTIMAGEDIR}/${time.toString("yyyymmdd")}_${imagesNameCheck.head.take(16)}.${check.format}"
    val whCheck = wh match {
      case Width => "+append"
      case Height => "-append"
    }

    try {
      Process(s"convert ${whCheck} ${mergeImage} ${destImage}").!!.stripLineEnd
      destImage
    } catch {
      case e: Throwable => "imageWidthAppend error"
    }
  }

  def delImage(image: String, dir: Boolean = false) = {

    val deleteData = {
      if(dir) s"${DESTIMAGEDIR}/${image}"
      else image
    }

    try {
      Process(s"rm -f ${deleteData}").!
    } catch {
      case e: Throwable => "delImage error"
    }
  }

}
