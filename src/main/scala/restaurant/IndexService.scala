package restaurant

import org.mongodb.scala.model.{Indexes, IndexOptions}
import scala.util.Try

object IndexService {
  private val col = DB.collection

  def createIndexes(): Unit = {
    Try {
      DB.await(col.createIndex(Indexes.ascending("cuisine"), IndexOptions().name("idx_cuisine")))
      println("  Created index: idx_cuisine (cuisine ascending)")

      DB.await(col.createIndex(Indexes.ascending("address.zipcode"), IndexOptions().name("idx_zipcode")))
      println("  Created index: idx_zipcode (address.zipcode ascending)")

      DB.await(col.createIndex(
        Indexes.compoundIndex(Indexes.ascending("borough"), Indexes.ascending("cuisine")),
        IndexOptions().name("idx_borough_cuisine")
      ))
      println("  Created compound index: idx_borough_cuisine")
    }.recover { case e => println(s"  Index note: ${e.getMessage}") }
  }

  def listIndexes(): Unit = {
    Try {
      val indexes = DB.await(col.listIndexes())
      println("\n  Current Indexes on 'restaurants' collection:\n")
      indexes.foreach { idx =>
        val name = idx.getString("name")
        val key = idx.get("key").map(_.toString).getOrElse("N/A")
        println(s"    Name: $name")
        println(s"    Key:  $key")
        println("    " + "-" * 30)
      }
    }.recover { case e => println(s"\n  Error: ${e.getMessage}") }
  }
}
