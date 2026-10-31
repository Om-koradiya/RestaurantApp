name := "RestaurantApp"
version := "1.0"
scalaVersion := "2.13.14"

libraryDependencies ++= Seq(
  "org.mongodb.scala" %% "mongo-scala-driver" % "5.2.0"
)

fork := true
connectInput := true
