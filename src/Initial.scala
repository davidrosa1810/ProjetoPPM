package ProjetoPPM

import Configs.{camVolume, camera, cameraTransform, lineX, lineY, lineZ}

import ProjetoPPM.GroupUsers.menu
import ProjetoPPM.IOUtils.fileLoader
import javafx.fxml.FXMLLoader
import javafx.geometry.{Insets, Pos}
import javafx.scene.{Group, Parent, Scene, SceneAntialiasing, SubScene}
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color

object Initial {


  val worldRoot: Group = new Group(camVolume, lineX, lineY, lineZ)

  val subScene = new SubScene(worldRoot, 800, 600, true, SceneAntialiasing.BALANCED)
  subScene.setFill(Color.DARKSLATEGRAY)
  subScene.setCamera(camera)

  val cameraView = new CameraView(subScene)
  cameraView.setFirstPersonNavigationEabled(true)
  cameraView.setFitWidth(350)
  cameraView.setFitHeight(225)
  cameraView.getRx.setAngle(-45)
  cameraView.getT.setZ(-100)
  cameraView.getT.setY(-500)
  cameraView.getCamera.setTranslateZ(-50)
  cameraView.startViewing

  StackPane.setAlignment(cameraView, Pos.BOTTOM_RIGHT)
  StackPane.setMargin(cameraView, new Insets(5))


  var oct1 = menu(fileLoader(worldRoot), worldRoot)

  worldRoot.getChildren.add(cameraTransform)





  val fxmlLoader = new FXMLLoader(getClass.getResource("Controller.fxml"))
  val mainViewRoot: Parent = fxmlLoader.load()


  val root = new StackPane(subScene, cameraView, mainViewRoot)
  subScene.widthProperty.bind(root.widthProperty)
  subScene.heightProperty.bind(root.heightProperty)
  StackPane.setAlignment(mainViewRoot, Pos.TOP_LEFT)

  val scene = new Scene(root, 810, 610, true, SceneAntialiasing.BALANCED)

}
