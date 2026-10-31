package restaurant

import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates
import org.bson.{BsonArray, BsonDocument, BsonDouble, BsonInt32, BsonInt64, BsonString}
import scala.util.{Try, Success, Failure}
import scala.jdk.CollectionConverters._

object RestaurantService {
  private val col = DB.collection

  def addRestaurant(name: String, cuisine: String, borough: String,
                    building: String, street: String, zipcode: String,
                    restaurantId: String): Unit = {
    Try {
      val coordArray = new BsonArray()
      coordArray.add(new BsonDouble(-73.9))
      coordArray.add(new BsonDouble(40.7))

      val gradeDoc = new BsonDocument()
      gradeDoc.put("date", new BsonInt64(System.currentTimeMillis()))
      gradeDoc.put("grade", new BsonString("A"))
      gradeDoc.put("score", new BsonInt32(10))
      val gradesArray = new BsonArray()
      gradesArray.add(gradeDoc)

      val addressDoc = new BsonDocument()
      addressDoc.put("building", new BsonString(building))
      addressDoc.put("street", new BsonString(street))
      addressDoc.put("zipcode", new BsonString(zipcode))
      addressDoc.put("coord", coordArray)

      val mainDoc = new BsonDocument()
      mainDoc.put("name", new BsonString(name))
      mainDoc.put("cuisine", new BsonString(cuisine))
      mainDoc.put("borough", new BsonString(borough))
      mainDoc.put("restaurant_id", new BsonString(restaurantId))
      mainDoc.put("address", addressDoc)
      mainDoc.put("grades", gradesArray)

      DB.await(col.insertOne(Document(mainDoc)))
      println(s"\n  Restaurant '$name' added successfully!")
    } match {
      case Success(_) => ()
      case Failure(e) => println(s"\n  Error adding restaurant: ${e.getMessage}")
    }
  }

  def viewRestaurants(limit: Int = 10): Unit = {
    Try {
      val docs = DB.await(col.find().limit(limit))
      if (docs.isEmpty) {
        println("  No restaurants found.")
      } else {
        docs.foreach { doc =>
          try {
            val view = new RestaurantView(doc)
            println(view.prettyPrint())
            println("  " + "-" * 40)
          } catch { case _: Exception => () }
        }
        println(s"  Showing ${docs.size} restaurants.")
      }
    }.recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }

  def findByName(name: String): Option[RestaurantView] = {
    Try {
      val docs = DB.await(col.find(regex("name", name, "i")).limit(1))
      docs.headOption.map(new RestaurantView(_))
    } match {
      case Success(v) => v
      case Failure(e) =>
        println(s"  Error: ${e.getMessage}")
        None
    }
  }

  def updateCuisine(name: String, newCuisine: String): Unit = {
    Try {
      val result = DB.await(col.updateOne(equal("name", name), Updates.set("cuisine", newCuisine)))
      if (result.head.getModifiedCount > 0) println(s"\n  Updated cuisine of '$name' to '$newCuisine'.")
      else println(s"\n  Restaurant '$name' not found or no change.")
    }.recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }

  def updateBorough(name: String, newBorough: String): Unit = {
    Try {
      val result = DB.await(col.updateOne(equal("name", name), Updates.set("borough", newBorough)))
      if (result.head.getModifiedCount > 0) println(s"\n  Updated borough of '$name' to '$newBorough'.")
      else println(s"\n  Restaurant '$name' not found.")
    }.recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }

  def deleteRestaurant(name: String): Unit = {
    Try {
      val result = DB.await(col.deleteOne(equal("name", name)))
      if (result.head.getDeletedCount > 0) println(s"\n  Restaurant '$name' deleted successfully.")
      else println(s"\n  Restaurant '$name' not found.")
    }.recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }
}
