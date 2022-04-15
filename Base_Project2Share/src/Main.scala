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
import javafx.scene.input._

import java.io._
import scala.annotation.tailrec
import scala.collection.JavaConverters._
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


    // 3D objects (group of nodes - javafx.scene.Node) that will be provide to the subScene
    val worldRoot:Group = new Group(wiredBox, camVolume, lineX, lineY, lineZ)

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


    def checkIntersects(obj:Node,objs:List[Node]):Boolean = {
      objs match{
        case Nil => false
        case y::ys =>{
          if(obj.getBoundsInParent.intersects(y.asInstanceOf[Shape3D].getBoundsInParent)) true
          else checkIntersects(obj,ys)
        }
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
          if(wiredBox.getBoundsInParent.contains(cylinder2.asInstanceOf[Shape3D].getBoundsInParent) && !checkIntersects(cylinder2,getObjects(false))) {
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
          if(wiredBox.getBoundsInParent.contains(cube2.asInstanceOf[Shape3D].getBoundsInParent) && !checkIntersects(cube2,getObjects(false))) {
            worldRoot.getChildren.add(cube2)
          }
        }
        else println("Objeto desconhecido: " + linha(0))
      }
    }

    readFromFile(s"${System.getProperty("user.home")}/IdeaProjects/ProjetoPPM/Base_Project2Share/configs.txt")



    def getObjects(parts:Boolean):List[Node] = {
      val boxes = if(parts){
        worldRoot.getChildren.asScala.toList.filter(x=>x.isInstanceOf[Box] && (x.asInstanceOf[Box].getDrawMode==DrawMode.FILL || (x.asInstanceOf[Box].getMaterial!=redMaterial && x.asInstanceOf[Box].getDrawMode==DrawMode.LINE)))
      } else worldRoot.getChildren.asScala.toList.filter(x=> x.isInstanceOf[Box] && x.asInstanceOf[Box].getDrawMode!=DrawMode.LINE)
      val cylinders = worldRoot.getChildren.asScala.toList.filter(x=> x.isInstanceOf[Cylinder] && x.asInstanceOf[Cylinder].getDrawMode!=DrawMode.LINE)
      val objects = boxes:::cylinders
      objects
    }

    def makeBox(plac:Placement):Box = {
      val box = new Box(plac._2, plac._2, plac._2)
      box.setTranslateX(plac._2 / 2 + plac._1._1)
      box.setTranslateY(plac._2 / 2 + plac._1._2)
      box.setTranslateZ(plac._2 / 2 + plac._1._3)
      box.setDrawMode(DrawMode.LINE)
      box.setMaterial(whiteMaterial)
      box
    }

    def caixinhasMagicas(outerPlacement:Placement):List[Box] = {
      val dim = outerPlacement._2/2
      val part1 = makeBox(outerPlacement._1,dim)
      val part2 = makeBox((outerPlacement._1._1+dim,outerPlacement._1._2,outerPlacement._1._3),dim)
      val part3 = makeBox((outerPlacement._1._1,outerPlacement._1._2+dim,outerPlacement._1._3),dim)
      val part4 = makeBox((outerPlacement._1._1+dim,outerPlacement._1._2+dim,outerPlacement._1._3),dim)
      val part5 = makeBox((outerPlacement._1._1,outerPlacement._1._2,outerPlacement._1._3+dim),dim)
      val part6 = makeBox((outerPlacement._1._1+dim,outerPlacement._1._2,outerPlacement._1._3+dim),dim)
      val part7 = makeBox((outerPlacement._1._1,outerPlacement._1._2+dim,outerPlacement._1._3+dim),dim)
      val part8 = makeBox((outerPlacement._1._1+dim,outerPlacement._1._2+dim,outerPlacement._1._3+dim),dim)
      part1::part2::part3::part4::part5::part6::part7::part8::Nil

    }

    def depth(node:Node,i:Int,caixas:List[Box]):Int = {
      caixas match{
        case Nil => 0
        case x::xs => {
          if(x.getBoundsInParent.contains(node.getBoundsInParent)) {
            val placement = ((x.getTranslateX-x.getWidth/2,x.getTranslateY-x.getWidth/2,x.getTranslateZ-x.getWidth/2),x.getWidth)
            depth(node,i+1,caixinhasMagicas(placement))
          }
          else if(x.getBoundsInParent.intersects(node.getBoundsInParent)){
            i
          }
          else depth(node,i,xs)
        }
      }
    }

    def calculateDepth(placement:Placement,objs:List[Node]):List[Int] = {
      objs match{
        case Nil => Nil
        case x::xs => depth(x,0,caixinhasMagicas(placement))::calculateDepth(placement,xs)
      }
    }

    def minimumDepth(x:Option[Placement]=None):Int = {
      if(x.nonEmpty){
        if(calculateDepth(x.get,getContainedObjects(makeBox(x.get),getObjects(false),Nil)).isEmpty) -1
        else calculateDepth(x.get,getContainedObjects(makeBox(x.get),getObjects(false),Nil)).min
      }
      else calculateDepth(((0.0,0.0,0.0),32),getObjects(false)).min
    }


    def maximumDepth(x:Option[Placement]=None):Int = {
      if(x.nonEmpty){
        if(calculateDepth(x.get,getContainedObjects(makeBox(x.get),getObjects(false),Nil)).isEmpty) -1
        else calculateDepth(x.get,getContainedObjects(makeBox(x.get),getObjects(false),Nil)).max
      }
      else calculateDepth(((0.0,0.0,0.0),32),getObjects(false)).max
    }



    def checkContains(obj:Node, objs:List[Node]):Boolean = {
      objs match{
        case Nil => false
        case y::ys => {
          if(obj.getBoundsInParent.contains(y.asInstanceOf[Shape3D].getBoundsInParent)) {
            true
          }
          else checkContains(obj,ys)
        }
      }
    }

    @tailrec
    def getContainedObjects(obj:Node, objs:List[Node], objs2:List[Node]):List[Node] = {
      objs match{
        case Nil => objs2
        case y::ys => {
          if(obj.getBoundsInParent.contains(y.asInstanceOf[Shape3D].getBoundsInParent)) getContainedObjects(obj,ys,y::objs2)
          else getContainedObjects(obj,ys,objs2)
        }
      }
    }


    def createOctree(maxLevel:Int=minimumDepth()):Octree[Placement] = {
      val placement1: Placement = ((0, 0, 0), 32)
      makeNode(placement1,minimumDepth(),maxLevel)
    }

    def makeNode(placement:Placement, depth:Int, maxLevel:Int):Octree[Placement] = {
      val box = makeBox(placement)
      if(checkContains(box,getObjects(false))){
        if(depth==0 || maxLevel == 0){
          if(!checkIntersects(camVolume,List(box))) box.setMaterial(blueMaterial)
          worldRoot.getChildren.add(box)
          OcLeaf(placement,getContainedObjects(box,getObjects(false),Nil):List[Node])
        }
        else{
          OcNode(placement,
            makeNode((placement._1,placement._2/2),minimumDepth(Option(placement._1,placement._2/2)),maxLevel-1),
            makeNode(((placement._1._1+placement._2/2,placement._1._2,placement._1._3),placement._2/2),minimumDepth(Option((placement._1._1+placement._2/2,placement._1._2,placement._1._3),placement._2/2)),maxLevel-1),
            makeNode(((placement._1._1,placement._1._2+placement._2/2,placement._1._3),placement._2/2),minimumDepth(Option((placement._1._1,placement._1._2+placement._2/2,placement._1._3),placement._2/2)),maxLevel-1),
            makeNode(((placement._1._1+placement._2/2,placement._1._2+placement._2/2,placement._1._3),placement._2/2),minimumDepth(Option((placement._1._1+placement._2/2,placement._1._2+placement._2/2,placement._1._3),placement._2/2)),maxLevel-1),
            makeNode(((placement._1._1,placement._1._2,placement._1._3+placement._2/2),placement._2/2),minimumDepth(Option((placement._1._1,placement._1._2,placement._1._3+placement._2/2),placement._2/2)),maxLevel-1),
            makeNode(((placement._1._1+placement._2/2,placement._1._2,placement._1._3+placement._2/2),placement._2/2),minimumDepth(Option((placement._1._1+placement._2/2,placement._1._2,placement._1._3+placement._2/2),placement._2/2)),maxLevel-1),
            makeNode(((placement._1._1,placement._1._2+placement._2/2,placement._1._3+placement._2/2),placement._2/2),minimumDepth(Option((placement._1._1,placement._1._2+placement._2/2,placement._1._3+placement._2/2),placement._2/2)),maxLevel-1),
            makeNode(((placement._1._1+placement._2/2,placement._1._2+placement._2/2,placement._1._3+placement._2/2),placement._2/2),minimumDepth(Option((placement._1._1+placement._2/2,placement._1._2+placement._2/2,placement._1._3+placement._2/2),placement._2/2)),maxLevel-1))
        }
      }
      else OcEmpty
    }


    //é aqui que se poe o limite de profundidade da octree
    var oct1 = createOctree(2)


    def getPartition(sec:Section):Node = {
      val translations = (sec._1._1._1+sec._1._2/2, sec._1._1._2+sec._1._2/2, sec._1._1._3+sec._1._2/2)
      val list = worldRoot.getChildren.asScala.toList.filter(x=>x.isInstanceOf[Box] && x.asInstanceOf[Box].getDrawMode==DrawMode.LINE && x.asInstanceOf[Box].getMaterial!=redMaterial)
      val box = list.filter(x=>x.asInstanceOf[Box].getTranslateX==translations._1 && x.asInstanceOf[Box].getTranslateY==translations._2 && x.asInstanceOf[Box].getTranslateZ==translations._3).head
      box
    }


    def changePartitionsColor[A](tree:Octree[A]):Any = {
      tree match {
        case OcNode(_, up_00, up_01, up_10, up_11, down_00, down_01, down_10, down_11) => {
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
          val box = getPartition(section.asInstanceOf[Section])
          if (camVolume.getBoundsInParent.intersects(box.asInstanceOf[Box].getBoundsInParent)) {
            box.asInstanceOf[Box].setMaterial(whiteMaterial)
          }
          else {
            box.asInstanceOf[Box].setMaterial(blueMaterial)
          }
        }
        case OcEmpty =>
      }
    }


    def scale(factor:Double) = {

      def scaleObjects(x:List[Node]):Any = {
        x match{
          case Nil => Nil
          case y::ys => {
            val newCoords = (y.getTranslateX*factor,y.getTranslateY*factor,y.getTranslateZ*factor)
            y.setTranslateX(newCoords._1)
            y.setTranslateY(newCoords._2)
            y.setTranslateZ(newCoords._3)
            y.setScaleX(y.getScaleX*factor)
            y.setScaleY(y.getScaleY*factor)
            y.setScaleZ(y.getScaleZ*factor)
          }
            scaleObjects(ys)
        }
      }
      def scaleOcTree[A](tree:Octree[Placement]=oct1):Octree[Placement] = {
        def scalePlacement(plac: => Placement):Placement = {
          ((plac._1._1*factor, plac._1._2*factor, plac._1._3*factor), plac._2*factor)
        }
        tree match {
          case OcNode(placement, up_00, up_01, up_10, up_11, down_00, down_01, down_10, down_11) => {
            val placement2 = scalePlacement(placement)
            OcNode(placement2, scaleOcTree(up_00), scaleOcTree(up_01), scaleOcTree(up_10), scaleOcTree(up_11), scaleOcTree(down_00), scaleOcTree(down_01), scaleOcTree(down_10), scaleOcTree(down_11))
          }
          case OcLeaf(section) => {
            val sec2 = scalePlacement(section.asInstanceOf[Section]._1)
            OcLeaf((sec2,section.asInstanceOf[Section]._2))
          }
          case OcEmpty => OcEmpty
        }
      }
      scaleObjects(getObjects(true))
      oct1 = scaleOcTree()
      changePartitionsColor(oct1)
    }

    def writeToFile(file: String) = {
      val pw = new PrintWriter(new File(file))
      pw.write(oct1.toString)
      pw.close
    }


    //Mouse left click interaction
    scene.setOnMouseClicked((event) => {
      camVolume.setTranslateX(camVolume.getTranslateX + 2)
      changePartitionsColor(oct1)
      writeToFile("output.txt")
    })

    scene.setOnKeyPressed(e => {
      if(e.getCode == KeyCode.UP)
        scale(2)
      else if(e.getCode() == KeyCode.DOWN)
        scale(0.5)
    })


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