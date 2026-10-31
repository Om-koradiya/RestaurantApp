package restaurant

import scala.io.StdIn
import scala.util.Try

object Main {
  def main(args: Array[String]): Unit = {
    println("\n" + "=" * 55)
    println("  Restaurant Discovery & Analytics System")
    println("  Using MongoDB sample_restaurants dataset")
    println("=" * 55)

    println("\n  Initializing indexes...")
    IndexService.createIndexes()

    var running = true
    while (running) {
      println("\n" + "-" * 40)
      println("  MAIN MENU")
      println("-" * 40)
      println("  1. Add Restaurant")
      println("  2. Search/View Restaurants")
      println("  3. Update Restaurant")
      println("  4. Delete Restaurant")
      println("  5. Restaurant Analytics")
      println("  6. Index Information")
      println("  7. Exit")
      println("-" * 40)

      try {
        val choice = Option(StdIn.readLine("  Enter choice: ")).map(_.trim).getOrElse("")
        println()

        choice match {
          case "1" =>
            println("  --- Add New Restaurant ---")
            val name = StdIn.readLine("  Restaurant name: ").trim
            val cuisine = StdIn.readLine("  Cuisine type: ").trim
            val borough = StdIn.readLine("  Borough: ").trim
            val building = StdIn.readLine("  Building: ").trim
            val street = StdIn.readLine("  Street: ").trim
            val zipcode = StdIn.readLine("  Zipcode: ").trim
            val id = StdIn.readLine("  Restaurant ID: ").trim
            RestaurantService.addRestaurant(name, cuisine, borough, building, street, zipcode, id)

          case "2" =>
            println("  --- Search/View Restaurants ---")
            println("  1. View all (first 10)")
            println("  2. Search by name")
            println("  3. Search by cuisine")
            println("  4. Search by borough")
            println("  5. Search by ZIP code")
            println("  6. Search by minimum score")
            println("  7. Search by cuisine + borough")
            val c = StdIn.readLine("  Choice: ").trim
            c match {
              case "1" => RestaurantService.viewRestaurants()
              case "2" => SearchService.searchByName(StdIn.readLine("  Enter name: ").trim)
              case "3" => SearchService.searchByCuisine(StdIn.readLine("  Enter cuisine: ").trim)
              case "4" => SearchService.searchByBorough(StdIn.readLine("  Enter borough: ").trim)
              case "5" => SearchService.searchByZipcode(StdIn.readLine("  Enter ZIP code: ").trim)
              case "6" => SearchService.searchByMinScore(Try(StdIn.readLine("  Enter minimum score: ").trim.toInt).getOrElse(0))
              case "7" =>
                val cu = StdIn.readLine("  Enter cuisine: ").trim
                val bo = StdIn.readLine("  Enter borough: ").trim
                SearchService.searchByCuisineAndBorough(cu, bo)
              case _ => println("  Invalid choice.")
            }

          case "3" =>
            println("  --- Update Restaurant ---")
            println("  1. Update cuisine")
            println("  2. Update borough")
            val c = StdIn.readLine("  Choice: ").trim
            val name = StdIn.readLine("  Restaurant name to update: ").trim
            c match {
              case "1" => RestaurantService.updateCuisine(name, StdIn.readLine("  New cuisine: ").trim)
              case "2" => RestaurantService.updateBorough(name, StdIn.readLine("  New borough: ").trim)
              case _ => println("  Invalid choice.")
            }

          case "4" =>
            println("  --- Delete Restaurant ---")
            val name = StdIn.readLine("  Restaurant name to delete: ").trim
            val confirm = StdIn.readLine(s"  Are you sure you want to delete '$name'? (y/n): ").trim
            if (confirm.toLowerCase == "y") RestaurantService.deleteRestaurant(name)
            else println("  Deletion cancelled.")

          case "5" =>
            println("  --- Restaurant Analytics ---")
            println("  1. Restaurants by cuisine (top 10)")
            println("  2. Restaurants by borough")
            println("  3. Average score by cuisine (top 10)")
            println("  4. Grade distribution")
            println("  5. Top-rated restaurants")
            println("  6. Run all analytics")
            val c = StdIn.readLine("  Choice: ").trim
            c match {
              case "1" => AnalyticsService.restaurantsByCuisine()
              case "2" => AnalyticsService.restaurantsByBorough()
              case "3" => AnalyticsService.avgScoreByCuisine()
              case "4" => AnalyticsService.gradeDistribution()
              case "5" => AnalyticsService.topRatedRestaurants(
                Try(StdIn.readLine("  How many? (default 10): ").trim.toInt).getOrElse(10)
              )
              case "6" =>
                AnalyticsService.restaurantsByCuisine()
                AnalyticsService.restaurantsByBorough()
                AnalyticsService.avgScoreByCuisine()
                AnalyticsService.gradeDistribution()
                AnalyticsService.topRatedRestaurants()
              case _ => println("  Invalid choice.")
            }

          case "6" => IndexService.listIndexes()

          case "7" =>
            println("  Goodbye!")
            DB.close()
            running = false

          case _ => println("  Invalid choice. Please try again.")
        }
      } catch {
        case e: Exception => println(s"  Error: ${e.getMessage}")
      }
    }
  }
}
