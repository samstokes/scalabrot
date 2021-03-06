package uk.co.samstokes.scalabrot

import java.io._
import javax.imageio._
import javax.imageio.stream._
import java.awt._
import java.awt.image._

case class Complex(re : Double, im : Double) {
  def +(other : Complex) = Complex(re + other.re, im + other.im)
  def *(other : Complex) =
      Complex(re * other.re - im * other.im,
              re * other.im + im * other.re)
  def sq() = *(this)
  def sqmag() = re * re + im * im
  def mag() = Math.sqrt(sqmag)
}

object app {

  val bailout = 2.0
  val iterations = 1000

  implicit def double2Complex(d : Double) = Complex(d, 0.0)

  def colourists(name : String) = name.toLowerCase match {
    case "escapebw" => escapeBW _
    case "escapevalue" => escapeValue _
    case "escapetime" => escapeTime _
    case _ => throw new IllegalArgumentException("no such colourist")
  }

  case class MandelInfo(escaped : Boolean, value : Complex, iterations : Int)

  def escapeBW(i : MandelInfo) = if (i.escaped) 0xffffff else 0x000000

  def grayscale(shade : Int) = (shade << 16) | (shade << 8) | shade

  def escapeValue(i : MandelInfo) = {
    if (i.escaped)
      grayscale(((i.value.mag - bailout) * 128).toInt)
    else
      0x000000
  }

  def escapeTime(i : MandelInfo) = {
    if (i.escaped)
      grayscale(i.iterations)
    else
      0x000000
  }

  def mandel(c : Complex, bailout : Double, maxiter : Int) = {
    val bailoutsq = bailout * bailout

    var z = 0.0 : Complex
    var iter = 0
    while (z.sqmag < bailoutsq && iter < maxiter) {
      z = z.sq + c
      iter += 1
    }

    MandelInfo(z.sqmag >= bailoutsq, z, iter)
  }

  def drawMandel(width : Int, height : Int, colourist : (MandelInfo) => Int) = {
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    // translate the complex plane and scale it to the image dimensions
    val minRe = -2.0; val maxRe = 1.0
    val scale = (maxRe - minRe) / width.toDouble
    val xoff = - 2 * width / 3; val yoff = - height / 2

    for (y <- Iterator.range(0, height); x <- Iterator.range(0, width)) {
      val c = Complex((x + xoff) * scale, (y + yoff) * scale)
      val escape = mandel(c, bailout, iterations)
      image.setRGB(x, y, colourist(escape))
    }

    image
  }

  def imageWriter(suffix : String) = {
    val writers = ImageIO.getImageWritersBySuffix(suffix)
    // TODO this might throw, or be rubbish
    writers.next
  }

  def main(args : Array[String]) : Unit = {
    val image = drawMandel(args(0).toInt, args(1).toInt, colourists(args(2)))
    val output = new File(args(3))

    val suffix = output.getName.replaceFirst(".*\\.", "")
    val writer = imageWriter(suffix)
    writer.setOutput(new FileImageOutputStream(output))
    writer.write(image)
  }

}
