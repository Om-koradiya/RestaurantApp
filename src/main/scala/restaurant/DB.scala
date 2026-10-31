package restaurant

import org.mongodb.scala._
import com.mongodb.{ConnectionString, MongoClientSettings}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import scala.concurrent.Await
import scala.concurrent.duration._

object DB {
  // Set MONGO_URI environment variable with your MongoDB connection string
  private val connectionString = sys.env.getOrElse("MONGO_URI",
    "mongodb+srv://<username>:<password>@<cluster>.mongodb.net/"
  )

  private val codecRegistry = fromRegistries(DEFAULT_CODEC_REGISTRY)

  private val settings = MongoClientSettings.builder()
    .applyConnectionString(new ConnectionString(connectionString))
    .codecRegistry(codecRegistry)
    .build()

  private val client: MongoClient = MongoClient(settings)
  val database: MongoDatabase = client.getDatabase("sample_restaurants").withCodecRegistry(codecRegistry)
  val collection: MongoCollection[Document] = database.getCollection("restaurants")

  def await[T](obs: Observable[T]): Seq[T] =
    Await.result(obs.toFuture(), 15.seconds)

  def close(): Unit = client.close()
}
