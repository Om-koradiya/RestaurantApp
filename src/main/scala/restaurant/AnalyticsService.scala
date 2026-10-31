package restaurant

import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Accumulators._
import org.mongodb.scala.model.Sorts
import scala.util.Try

object AnalyticsService {
  private val col = DB.collection

  def restaurantsByCuisine(): Unit = {
    Try {
      val results = DB.await(col.aggregate(Seq(
        group("$cuisine", sum("count", 1)),
        sort(Sorts.descending("count")),
        limit(10)
      )))
      println("\n  Top 10 Cuisines by Restaurant Count:\n")
      println(f"  ${"Cuisine"}%-30s ${"Count"}%s")
      println("  " + "-" * 45)
      results.foreach { doc =>
        val cuisine = doc.getString("_id")
        val count = doc.getInteger("count")
        println(f"  $cuisine%-30s $count%s")
      }
    }.recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }

  def restaurantsByBorough(): Unit = {
    Try {
      val results = DB.await(col.aggregate(Seq(
        group("$borough", sum("count", 1)),
        sort(Sorts.descending("count"))
      )))
      println("\n  Restaurants by Borough:\n")
      println(f"  ${"Borough"}%-20s ${"Count"}%s")
      println("  " + "-" * 35)
      results.foreach { doc =>
        val borough = doc.getString("_id")
        val count = doc.getInteger("count")
        println(f"  $borough%-20s $count%s")
      }
    }.recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }

  def avgScoreByCuisine(): Unit = {
    Try {
      val results = DB.await(col.aggregate(Seq(
        unwind("$grades"),
        group("$cuisine", avg("avgScore", "$grades.score")),
        sort(Sorts.descending("avgScore")),
        limit(10)
      )))
      println("\n  Top 10 Cuisines by Average Score:\n")
      println(f"  ${"Cuisine"}%-30s ${"Avg Score"}%s")
      println("  " + "-" * 45)
      results.foreach { doc =>
        val cuisine = doc.getString("_id")
        val avgScore: Double = Try(doc.getDouble("avgScore").doubleValue()).getOrElse(0.0)
        println(f"  $cuisine%-30s $avgScore%.2f")
      }
    }.recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }

  def gradeDistribution(): Unit = {
    Try {
      val results = DB.await(col.aggregate(Seq(
        unwind("$grades"),
        group("$grades.grade", sum("count", 1)),
        sort(Sorts.descending("count"))
      )))
      println("\n  Grade Distribution:\n")
      println(f"  ${"Grade"}%-10s ${"Count"}%s")
      println("  " + "-" * 25)
      results.foreach { doc =>
        val grade = doc.getString("_id")
        val count = doc.getInteger("count")
        println(f"  $grade%-10s $count%s")
      }
    }.recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }

  def topRatedRestaurants(n: Int = 10): Unit = {
    Try {
      val results = DB.await(col.aggregate(Seq(
        unwind("$grades"),
        group("$name", avg("avgScore", "$grades.score")),
        sort(Sorts.descending("avgScore")),
        limit(n)
      )))
      println(s"\n  Top $n Rated Restaurants (by Avg Score):\n")
      println(f"  ${"Restaurant"}%-40s ${"Avg Score"}%s")
      println("  " + "-" * 55)
      results.foreach { doc =>
        val name = doc.getString("_id")
        val avgScore: Double = Try(doc.getDouble("avgScore").doubleValue()).getOrElse(0.0)
        println(f"  $name%-40s $avgScore%.2f")
      }
    }.recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }
}
