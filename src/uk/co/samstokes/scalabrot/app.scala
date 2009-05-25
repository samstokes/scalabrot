package uk.co.samstokes.scalabrot

import java.io._
import javax.imageio._
import javax.imageio.stream._
import java.awt._
import java.awt.image._

object app {

  def drawFunkyThing(width : Int, height : Int) = {
    val maxcol = 0x00ffffff
    val scale = (maxcol / ((width * height) : Double)).toInt

    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    for (y <- Iterator.range(0, height); x <- Iterator.range(0, width)) {
      image.setRGB(x, y, x * y * scale)
    }

    image
  }

  def imageWriter(suffix : String) = {
    val writers = ImageIO.getImageWritersBySuffix(suffix)
    // TODO this might throw, or be rubbish
    writers.next
  }

  def main(args : Array[String]) : Unit = {
    val image = drawFunkyThing(args(0).toInt, args(1).toInt)
    val output = new File(args(2))

    val suffix = output.getName.replaceFirst(".*\\.", "")
    val writer = imageWriter(suffix)
    writer.setOutput(new FileImageOutputStream(output))
    writer.write(image)
  }

}
