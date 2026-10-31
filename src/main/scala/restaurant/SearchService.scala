package restaurant

import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import scala.util.Try

object SearchService {
  private val col = DB.collection

  private def displayResults(docs: Seq[Document], label: String): Unit = {
    if (docs.isEmpty) {
      println(s"\n  No restaurants found for $label.")
    } else {
      println(s"\n  Found ${docs.size} restaurant(s) for $label:\n")
      docs.foreach { doc =>
        val view = new RestaurantView(doc)
        println(view.prettyPrint())
        println("  " + "-" * 40)
      }
    }
  }

  def searchByName(name: String): Unit = {
    Try { displayResults(DB.await(col.find(regex("name", name, "i")).limit(10)), s"name='$name'") }
      .recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }

  def searchByCuisine(cuisine: String): Unit = {
    Try { displayResults(DB.await(col.find(regex("cuisine", cuisine, "i")).limit(10)), s"cuisine='$cuisine'") }
      .recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }

  def searchByBorough(borough: String): Unit = {
    Try { displayResults(DB.await(col.find(regex("borough", borough, "i")).limit(10)), s"borough='$borough'") }
      .recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }

  def searchByZipcode(zipcode: String): Unit = {
    Try { displayResults(DB.await(col.find(equal("address.zipcode", zipcode)).limit(10)), s"zipcode='$zipcode'") }
      .recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }

  def searchByMinScore(minScore: Int): Unit = {
    Try { displayResults(DB.await(col.find(gte("grades.score", minScore)).limit(10)), s"score >= $minScore") }
      .recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }

  def searchByCuisineAndBorough(cuisine: String, borough: String): Unit = {
    Try {
      displayResults(
        DB.await(col.find(and(regex("cuisine", cuisine, "i"), regex("borough", borough, "i"))).limit(10)),
        s"cuisine='$cuisine' AND borough='$borough'"
      )
    }.recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }
}
