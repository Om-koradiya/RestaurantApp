# Restaurant Discovery & Analytics System

A Scala-based CLI application using MongoDB's `sample_restaurants` dataset for restaurant discovery, CRUD operations, search/filtering, indexing, and analytics.

## Prerequisites

- **Java JDK 8+** (JDK 11 recommended)
- **sbt** (Scala Build Tool)
- **MongoDB** running locally on `localhost:27017`
- **MongoDB sample_restaurants dataset** loaded (via MongoDB Atlas sample data or manual import)

## Setup & Execution

1. **Ensure MongoDB is running** with the `sample_restaurants` database loaded:
   ```bash
   mongosh
   use sample_restaurants
   db.restaurants.countDocuments()  # Should return ~25,000+
   ```

2. **Navigate to project directory:**
   ```bash
   cd assignment
   ```

3. **Run the application:**
   ```bash
   sbt run
   ```

4. **Follow the interactive menu** to perform CRUD operations, search, and analytics.

## Project Structure

```
assignment/
├── build.sbt                         # SBT build config with MongoDB driver
├── project/
│   └── build.properties              # SBT version
├── src/main/scala/restaurant/
│   ├── MongoConnection.scala          # MongoDB connection singleton
│   ├── Models.scala                   # Case classes, traits (Printable, Searchable)
│   ├── RestaurantCRUD.scala           # Create, Read, Update, Delete operations
│   ├── SearchFilter.scala             # Search by name, cuisine, borough, ZIP, score
│   ├── IndexManager.scala             # Index creation and listing
│   ├── Analytics.scala                # Aggregation queries
│   └── RestaurantApp.scala            # Main CLI menu-driven application
└── README.md
```

## Features Demonstrated

### Scala Concepts
- **Variables & data types**: String, Int, Double, Boolean, List, Option
- **Conditions & functions**: Pattern matching (`match/case`), if/else
- **Collections & operations**: `map`, `filter`, `foreach`, `headOption`
- **Option & pattern matching**: `Option[T]`, `Try/Success/Failure`
- **Exception handling**: `Try/recover` throughout all DB operations
- **Case classes & objects**: `Address`, `Grade`, `Restaurant`, singleton objects
- **Encapsulation**: Private fields with getter methods in `RestaurantView`
- **Traits & inheritance**: `Printable` and `Searchable` traits, `RestaurantView extends Printable with Searchable`

### MongoDB CRUD
- **Create**: Add new restaurant records with full address and grade data
- **Read**: View restaurants, find by name
- **Update**: Modify cuisine or borough of existing restaurants
- **Delete**: Remove restaurants by name with confirmation

### Search & Filtering (6 operations)
1. Search by restaurant name (partial, case-insensitive)
2. Search by cuisine type
3. Search by borough
4. Search by ZIP code
5. Search by minimum score
6. Combined filter: cuisine + borough

### Indexing
- `idx_cuisine` — single index on cuisine field
- `idx_borough` — single index on borough field
- `idx_cuisine_borough_score` — compound index on cuisine + borough + grades.score

### Aggregation Queries (5 operations)
1. Restaurant count by cuisine (top 10)
2. Restaurant count by borough
3. Average score by cuisine (top 10)
4. Grade distribution (A, B, C, etc.)
5. Top-rated restaurants by average score

## Author
- **Name**: Om Koradiya
- **Enrollment**: 23162121012
- **Branch - Batch**: BDA - 74
