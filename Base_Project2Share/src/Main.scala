import javafx.application.Application
import javafx.geometry.{Bounds, Insets, Pos}
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape._
import javafx.scene.transform.{Rotate, Translate}
import javafx.scene.{Group, Node}
import javafx.stage.Stage
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.{PerspectiveCamera, Scene, SceneAntialiasing, SubScene}
import scala.collection.JavaConverters._
import javafx.collections.transformation.FilteredList

import scala.io.Source
import scala.jdk.FunctionWrappers.AsJavaPredicate



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
    val whiteMaterial = new PhongMaterial()
    whiteMaterial.setDiffuseColor(Color.rgb(255,255,255))

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

    def checkIntersectingObjects(o:Object,index:Int=0):Boolean= {
      if (worldRoot.getChildren.get(index).getBoundsInParent.contains(o.asInstanceOf[Shape3D].getBoundsInParent)) false
      else if (index==worldRoot.getChildren.size()) true
      else checkIntersectingObjects(o,index+1)
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

    //def getElementFromWorldRoot(f:Node=>Boolean):FilteredList[Node] = {
      //worldRoot.getChildren.filtered(AsJavaPredicate(f))
    //}


    def makeOctree() = {
      def aux(outerPlacement:Placement):List[Placement] = {
        val dim = outerPlacement._2/2
        val part1 = (outerPlacement._1,dim):Placement
        val part2 = ((outerPlacement._1._1+dim,outerPlacement._1._2,outerPlacement._1._3),dim):Placement
        val part3 = ((outerPlacement._1._1,outerPlacement._1._2+dim,outerPlacement._1._3),dim):Placement
        val part4 = ((outerPlacement._1._1+dim,outerPlacement._1._2+dim,outerPlacement._1._3),dim):Placement
        val part5 = ((outerPlacement._1._1,outerPlacement._1._2,outerPlacement._1._3+dim),dim):Placement
        val part6 = ((outerPlacement._1._1+dim,outerPlacement._1._2,outerPlacement._1._3+dim),dim):Placement
        val part7 = ((outerPlacement._1._1,outerPlacement._1._2+dim,outerPlacement._1._3+dim),dim):Placement
        val part8 = ((outerPlacement._1._1+dim,outerPlacement._1._2+dim,outerPlacement._1._3+dim),dim):Placement
        part1::part2::part3::part4::part5::part6::part7::part8::Nil
      }
      val boxes = worldRoot.getChildren.asScala.toList.filter(x=> x.isInstanceOf[Box] && x.asInstanceOf[Box].getDrawMode!=DrawMode.LINE)
      val cylinders = worldRoot.getChildren.asScala.toList.filter(x=> x.isInstanceOf[Cylinder] && x.asInstanceOf[Cylinder].getDrawMode!=DrawMode.LINE)
      val objects = boxes:::cylinders
      val placement1: Placement = ((0, 0, 0), 32.0)
      aux(placement1)
      println(objects)
    }

    readFromFile(s"${System.getProperty("user.home")}/IdeaProjects/ProjetoPPM/Base_Project2Share/configs.txt")
    makeOctree()

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



    //setup and start the Stage
    stage.setTitle("PPM Project 21/22")
    stage.setScene(scene)
    stage.show


    //oct1 - example of an Octree[Placement] that contains only one Node (i.e. cylinder1)
    //In case of difficulties to implement task T2 this octree can be used as input for tasks T3, T4 and T5

    val placement1: Placement = ((0, 0, 0), 8.0)
    val sec1: Section = (((0.0,0.0,0.0), 2.0), List(cylinder1.asInstanceOf[Node]))
    val ocLeaf1 = OcLeaf(sec1)
    val oct1:Octree[Placement] = OcNode[Placement](placement1, ocLeaf1, OcLeaf(((2.0,0.0,0.0),2.0), Nil), OcEmpty, OcEmpty, OcEmpty, OcEmpty, OcEmpty, OcEmpty)

    def changePartitionsColor[A](tree:Octree[A]):Any = {
      tree match {
        case OcNode(coords, up_00, up_01, up_10, up_11, down_00, down_01, down_10, down_11) => {
          changePartitionsColor[A](up_00)
          changePartitionsColor[A](up_01)
          changePartitionsColor[A](up_10)
          changePartitionsColor[A](up_11)
          changePartitionsColor[A](down_00)
          changePartitionsColor[A](down_01)
          changePartitionsColor[A](down_10)
          changePartitionsColor[A](down_11)
        }
        case OcLeaf(section) => {
          println("ola")
          val box = new Box(section.asInstanceOf[Section]._1._2,section.asInstanceOf[Section]._1._2,section.asInstanceOf[Section]._1._2)
          box.setDrawMode(DrawMode.LINE)
          box.setTranslateX(section.asInstanceOf[Section]._1._1._1+section.asInstanceOf[Section]._1._2/2)
          box.setTranslateY(section.asInstanceOf[Section]._1._1._2+section.asInstanceOf[Section]._1._2/2)
          box.setTranslateZ(section.asInstanceOf[Section]._1._1._3+section.asInstanceOf[Section]._1._2/2)
          worldRoot.getChildren.add(box)
          if (camVolume.getBoundsInParent.intersects(box.asInstanceOf[Shape3D].getBoundsInParent)) {
            worldRoot.getChildren.get(worldRoot.getChildren.size()-1).asInstanceOf[Shape3D].setMaterial(whiteMaterial)
          }
          else worldRoot.getChildren.get(worldRoot.getChildren.size()-1).asInstanceOf[Shape3D].setMaterial(blueMaterial)
        }
        case OcEmpty =>
      }
    }

    //Mouse left click interaction
    scene.setOnMouseClicked((event) => {
      camVolume.setTranslateX(camVolume.getTranslateX + 2)
      changePartitionsColor(oct1)
    })

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

