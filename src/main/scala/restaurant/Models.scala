package restaurant

import org.mongodb.scala.Document

// Trait for printable entities
trait Printable {
  def prettyPrint(): String
}

// Trait for searchable entities
trait Searchable {
  def matchesQuery(query: String): Boolean
}

// Case class representing restaurant data
case class Restaurant(
  name: String,
  cuisine: String,
  borough: String,
  zipcode: String,
  street: String,
  building: String
)

object Restaurant {
  // Factory method using Option for safe parsing
  def fromDocument(doc: Document): Option[Restaurant] = {
    for {
      name <- Option(doc.getString("name"))
      cuisine <- Option(doc.getString("cuisine"))
      borough <- Option(doc.getString("borough"))
    } yield {
      val address = doc.get[org.bson.BsonValue]("address").flatMap { v =>
        if (v.isDocument) Some(Document(v.asDocument())) else None
      }
      val zipcode = address.flatMap(a => Option(a.getString("zipcode"))).getOrElse("N/A")
      val street = address.flatMap(a => Option(a.getString("street"))).getOrElse("N/A")
      val building = address.flatMap(a => Option(a.getString("building"))).getOrElse("N/A")
      Restaurant(name, cuisine, borough, zipcode, street, building)
    }
  }
}

// Demonstrates encapsulation, traits, inheritance/composition
class RestaurantView(doc: Document) extends Printable with Searchable {
  private val restaurant: Restaurant = Restaurant.fromDocument(doc).getOrElse(
    Restaurant("N/A", "N/A", "N/A", "N/A", "N/A", "N/A")
  )

  def getName: String = restaurant.name
  def getCuisine: String = restaurant.cuisine
  def getBorough: String = restaurant.borough
  def getZipcode: String = restaurant.zipcode

  override def prettyPrint(): String = {
    s"""  Name:     ${restaurant.name}
       |  Cuisine:  ${restaurant.cuisine}
       |  Borough:  ${restaurant.borough}
       |  Address:  ${restaurant.building} ${restaurant.street}, ${restaurant.zipcode}""".stripMargin
  }

  override def matchesQuery(query: String): Boolean = {
    val q = query.toLowerCase
    restaurant.name.toLowerCase.contains(q) ||
      restaurant.cuisine.toLowerCase.contains(q) ||
      restaurant.borough.toLowerCase.contains(q)
  }
}
