import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape._
import javafx.scene.transform.{Rotate, Translate}
import javafx.scene.{Group, Node}
import javafx.stage.Stage
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.{PerspectiveCamera, Scene, SceneAntialiasing, SubScene}

import scala.io.Source


class Main extends Application {

  //Auxiliary types
  type Point = (Double, Double, Double)
  type Size = Double
  type Placement = (Point, Size) //1st point: origin, 2nd point: size

  //Shape3D is an abstract class that extends javafx.scene.Node
  //Box and Cylinder are subclasses of Shape3D
  type Section = (Placement, List[Node])  //example: ( ((0.0,0.0,0.0), 2.0), List(new Cylinder(0.5, 1, 10)))


  /*
    Additional information about JavaFX basic concepts (e.g. Stage, Scene) will be provided in week7
   */
  override def start(stage: Stage): Unit = {

    //Get and print program arguments (args: Array[String])
    val params = getParameters
    println("Program arguments:" + params.getRaw)

    //Materials to be applied to the 3D objects
    val redMaterial = new PhongMaterial()
    redMaterial.setDiffuseColor(Color.rgb(150,0,0))

    val greenMaterial = new PhongMaterial()
    greenMaterial.setDiffuseColor(Color.rgb(0,255,0))

    val blueMaterial = new PhongMaterial()
    blueMaterial.setDiffuseColor(Color.rgb(0,0,150))

    //3D objects
    val lineX = new Line(0, 0, 200, 0)
    lineX.setStroke(Color.GREEN)

    val lineY = new Line(0, 0, 0, 200)
    lineY.setStroke(Color.YELLOW)

    val lineZ = new Line(0, 0, 200, 0)
    lineZ.setStroke(Color.LIGHTSALMON)
    lineZ.getTransforms().add(new Rotate(-90, 0, 0, 0, Rotate.Y_AXIS))

    val camVolume = new Cylinder(10, 50, 10)
    camVolume.setTranslateX(1)
    camVolume.getTransforms().add(new Rotate(45, 0, 0, 0, Rotate.X_AXIS))
    camVolume.setMaterial(blueMaterial)
    camVolume.setDrawMode(DrawMode.LINE)

    val wiredBox = new Box(32, 32, 32)
    wiredBox.setTranslateX(16)
    wiredBox.setTranslateY(16)
    wiredBox.setTranslateZ(16)
    wiredBox.setMaterial(redMaterial)
    wiredBox.setDrawMode(DrawMode.LINE)

    val cylinder2 = new Cylinder(0.5, 1, 10)
    cylinder2.setTranslateX(5)
    cylinder2.setTranslateY(2)
    cylinder2.setTranslateZ(2)
    cylinder2.setScaleX(5)
    cylinder2.setScaleY(5)
    cylinder2.setScaleZ(5)
    cylinder2.setMaterial(redMaterial)

    val cylinder1 = new Cylinder(0.5, 1, 10)
    cylinder1.setTranslateX(2)
    cylinder1.setTranslateY(2)
    cylinder1.setTranslateZ(2)
    cylinder1.setScaleX(2)
    cylinder1.setScaleY(2)
    cylinder1.setScaleZ(2)
    cylinder1.setMaterial(greenMaterial)

    val box1 = new Box(1, 1, 1)  //
    box1.setTranslateX(5)
    box1.setTranslateY(5)
    box1.setTranslateZ(5)
    box1.setMaterial(greenMaterial)

    // 3D objects (group of nodes - javafx.scene.Node) that will be provide to the subScene
    val worldRoot:Group = new Group(wiredBox, camVolume, lineX, lineY, lineZ, cylinder1, box1)

    def checkIntersectingObjects(o:Object):Boolean= {
      worldRoot.getChildren match{
        case null => true
        case x =>
      }
    }


    def readFromFile(file: String) = {
      val bufferedSource = Source.fromFile(file)
      for (line <- bufferedSource.getLines) {
        val linha = line.split(" ")
        if (linha(0) == "Cylinder") {
          val cylinder2 = new Cylinder(0.5, 1, 10)
          val translacoes = (linha(2),linha(3),linha(4))
          cylinder2.setTranslateX(translacoes._1.toDouble)
          cylinder2.setTranslateY(translacoes._2.toDouble)
          cylinder2.setTranslateZ(translacoes._3.toDouble)
          val scale = (linha(5),linha(6),linha(7))
          cylinder2.setScaleX(scale._1.toDouble)
          cylinder2.setScaleY(scale._2.toDouble)
          cylinder2.setScaleZ(scale._3.toDouble)
          val color = linha(1).substring(1,linha(1).length-1).split(",")
          val color2 = new PhongMaterial()
          color2.setDiffuseColor(Color.rgb(color(0).toInt,color(1).toInt,color(2).toInt))
          cylinder2.setMaterial(color2)
          if(wiredBox.getBoundsInParent.contains(cylinder2.asInstanceOf[Shape3D].getBoundsInParent)) {
            worldRoot.getChildren.add(cylinder2)
          }
        }
        else if(linha(0)=="Cube"){
          val cube2 = new Box(1, 1, 1)
          val translacoes = (linha(2),linha(3),linha(4))
          cube2.setTranslateX(translacoes._1.toDouble)
          cube2.setTranslateY(translacoes._2.toDouble)
          cube2.setTranslateZ(translacoes._3.toDouble)
          val scale = (linha(5),linha(6),linha(7))
          cube2.setScaleX(scale._1.toDouble)
          cube2.setScaleY(scale._2.toDouble)
          cube2.setScaleZ(scale._3.toDouble)
          val color = linha(1).substring(1,linha(1).length-1).split(",")
          val color2 = new PhongMaterial()
          color2.setDiffuseColor(Color.rgb(color(0).toInt,color(1).toInt,color(2).toInt))
          cube2.setMaterial(color2)
          if(wiredBox.getBoundsInParent.contains(cube2.asInstanceOf[Shape3D].getBoundsInParent)) {
            worldRoot.getChildren.add(cube2)
          }
        }
        else println("Objeto desconhecido: " + linha(0))
      }
    }
      readFromFile(s"${System.getProperty("user.home")}/IdeaProjects/ProjetoPPM/Base_Project2Share/configs.txt")

    // Camera
    val camera = new PerspectiveCamera(true)

    val cameraTransform = new CameraTransformer
    cameraTransform.setTranslate(0, 0, 0)
    cameraTransform.getChildren.add(camera)
    camera.setNearClip(0.1)
    camera.setFarClip(10000.0)

    camera.setTranslateZ(-500)
    camera.setFieldOfView(20)
    cameraTransform.ry.setAngle(-45.0)
    cameraTransform.rx.setAngle(-45.0)
    worldRoot.getChildren.add(cameraTransform)

    // SubScene - composed by the nodes present in the worldRoot
    val subScene = new SubScene(worldRoot, 800, 600, true, SceneAntialiasing.BALANCED)
    subScene.setFill(Color.DARKSLATEGRAY)
    subScene.setCamera(camera)

    // CameraView - an additional perspective of the environment
    val cameraView = new CameraView(subScene)
    cameraView.setFirstPersonNavigationEabled(true)
    cameraView.setFitWidth(350)
    cameraView.setFitHeight(225)
    cameraView.getRx.setAngle(-45)
    cameraView.getT.setZ(-100)
    cameraView.getT.setY(-500)
    cameraView.getCamera.setTranslateZ(-50)
    cameraView.startViewing

      // Position of the CameraView: Right-bottom corner
      StackPane.setAlignment(cameraView, Pos.BOTTOM_RIGHT)
      StackPane.setMargin(cameraView, new Insets(5))

    // Scene - defines what is rendered (in this case the subScene and the cameraView)
    val root = new StackPane(subScene, cameraView)
    subScene.widthProperty.bind(root.widthProperty)
    subScene.heightProperty.bind(root.heightProperty)

    val scene = new Scene(root, 810, 610, true, SceneAntialiasing.BALANCED)

    //Mouse left click interaction
    scene.setOnMouseClicked((event) => {
      camVolume.setTranslateX(camVolume.getTranslateX + 2)
      worldRoot.getChildren.removeAll()
    })

    //setup and start the Stage
    stage.setTitle("PPM Project 21/22")
    stage.setScene(scene)
    stage.show

/*
    //oct1 - example of an Octree[Placement] that contains only one Node (i.e. cylinder1)
    //In case of difficulties to implement task T2 this octree can be used as input for tasks T3, T4 and T5

    val placement1: Placement = ((0, 0, 0), 8.0)
    val sec1: Section = (((0.0,0.0,0.0), 4.0), List(cylinder1.asInstanceOf[Node]))
    val ocLeaf1 = OcLeaf(sec1)
    val oct1:Octree[Placement] = OcNode[Placement](placement1, ocLeaf1, OcEmpty, OcEmpty, OcEmpty, OcEmpty, OcEmpty, OcEmpty, OcEmpty)

    //example of bounding boxes (corresponding to the octree oct1) added manually to the world
    val b2 = new Box(8,8,8)
    //translate because it is added by defaut to the coords (0,0,0)
    b2.setTranslateX(8/2)
    b2.setTranslateY(8/2)
    b2.setTranslateZ(8/2)
    b2.setMaterial(redMaterial)
    b2.setDrawMode(DrawMode.LINE)

    val b3 = new Box(4,4,4)
    //translate because it is added by defaut to the coords (0,0,0)
    b3.setTranslateX(4/2)
    b3.setTranslateY(4/2)
    b3.setTranslateZ(4/2)
    b3.setMaterial(redMaterial)
    b3.setDrawMode(DrawMode.LINE)

    //adding boxes b2 and b3 to the world
    worldRoot.getChildren.add(b2)
    worldRoot.getChildren.add(b3)
*/
  }

  override def init(): Unit = {
    println("init")
  }

  override def stop(): Unit = {
    println("stopped")
  }



}

object FxApp {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[Main], args: _*)
  }
}

