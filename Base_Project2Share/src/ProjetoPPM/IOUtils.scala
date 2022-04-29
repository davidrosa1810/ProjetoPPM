package ProjetoPPM

import javafx.scene.Group
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, Cylinder}

import java.io.{File, PrintWriter}
import scala.io.Source

import Pure._
import GroupUsers._
import Configs._


object IOUtils {


  def readFromFile(file: String, universe: Box,group:Group):Any = {
    val bufferedSource = Source.fromFile(file)
    for (line <- bufferedSource.getLines) {
      val linha = line.split(" ")

      if (linha(0) == "Cylinder" || linha(0) == "Box") {
        val shape = if (linha(0) == "Cylinder") new Cylinder(0.5, 1, 10) else new Box(1, 1, 1)
        val translacoes = (linha(2), linha(3), linha(4))
        shape.setTranslateX(translacoes._1.toDouble)
        shape.setTranslateY(translacoes._2.toDouble)
        shape.setTranslateZ(translacoes._3.toDouble)
        val scale = (linha(5), linha(6), linha(7))
        shape.setScaleX(scale._1.toDouble)
        shape.setScaleY(scale._2.toDouble)
        shape.setScaleZ(scale._3.toDouble)
        val color = linha(1).substring(1, linha(1).length - 1).split(",")
        val color2 = new PhongMaterial()
        color2.setDiffuseColor(Color.rgb(color(0).toInt, color(1).toInt, color(2).toInt))
        shape.setMaterial(color2)
        if (universe.getBoundsInParent.contains(shape.getBoundsInParent) && !checkIntersects(shape, getObjects(false,group))) {
          group.getChildren.add(shape)
        }
      }
      else println("Objeto desconhecido: " + linha(0))
    }
  }

  def writeToFile(file: String,oct:Octree[Placement]) = {
    val pw = new PrintWriter(new File(file))
    pw.write(oct.toString)
    pw.close
  }


}
