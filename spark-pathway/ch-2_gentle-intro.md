# starting spark locally

```bash
brew cask install java      # openjdk 12
brew install scala
brew install apache-spark
# shit it says must be java 8
brew cask install homebrew/cask-versions/adoptopenjdk8
brew install apache-spark

spark-shell

scala>  # we are in budiness

```

# spark-shell goods
```scala
:help
:h?             // search history
:quit
:save           // save a replayable session to file
:sh             // *shell command*
:paste          // paste a whole chunk of code in, ctrl-d to finish

```

# begin doing the gentle intro
```scala
// bunch of stuff about the SparkSession

// first command!
val helloRange = spark.range(1000).toDF("number")

helloRange.take(5)
    Array([0], [1], [2], [3], [4])

helloRange.show()
+------+
|number|
+------+
|     0|
|     1|
|     2|

// there we go

val divisBy2 = helloRange.where("number % 2 = 0")
divisBy2.show()

divisBy2.count()
    Long = 500

```

# methods so far
```scala
where
show
take
toDF
range
count
```

# Spark UI
http://localhost:4040

*nice! see your jobs there*

# end-to-end example: flight data
```scala
$> head data/flight-data/csv/2015-summary.csv
DEST_COUNTRY_NAME,ORIGIN_COUNTRY_NAME,count
United States,Romania,15


:paste  // gotta do this
val flightData2015 = spark
    .read
    .option("inferschema", "true")
    .option("header","true")
    .csv("./data/flight-data/csv/2015-summary.csv")

flightData2015.show(2)
+-----------------+-------------------+-----+
|DEST_COUNTRY_NAME|ORIGIN_COUNTRY_NAME|count|
+-----------------+-------------------+-----+
|    United States|            Romania|   15|
|    United States|            Croatia|    1|
+-----------------+-------------------+-----+

// a transformation: sorting the data, look at the physical plan

flightData2015.sort("count").explain


// before acting on plan, set the shuffle partitions
spark.conf.set("spark.sql.shuffle.partitions","5")

flightData2015.sort("count").take(2)
flightData2015.sort("count").show(2)
+-----------------+-------------------+-----+
|DEST_COUNTRY_NAME|ORIGIN_COUNTRY_NAME|count|
+-----------------+-------------------+-----+
|          Moldova|      United States|    1|
|    United States|            Croatia|    1|
+-----------------+-------------------+-----+
```

# 