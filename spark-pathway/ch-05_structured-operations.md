# spark guide ch 05

## cool lets go
```scala
$> cd ~/repo/spark-definitive-guide/spark-book
$> spark-shell

scala> val s = "hello world"
s: String = hello world
println(s)
hello world

:help
// lots of help
:quit

val df = spark.read.format("json")
    .load("data/flight-data/json/2015-summary.json")
df.createOrReplaceTempView("dfTable")
df.printSchema
    root
     |-- DEST_COUNTRY_NAME: string (nullable = true)
     |-- ORIGIN_COUNTRY_NAME: string (nullable = true)
     |-- count: long (nullable = true)

df.select("DEST_COUNTRY_NAME").show(2)
    +-----------------+
    |DEST_COUNTRY_NAME|
    +-----------------+
    |    United States|
    |    United States|
    +-----------------+

// many ways to refer to columns
import org.apache.spark.sql.functions.{expr, col, column}
df.select(
    df.col("DEST_COUNTRY_NAME"),
    col("DEST_COUNTRY_NAME"),
    column("DEST_COUNTRY_NAME"),
    'DEST_COUNTRY_NAME,
    $"DEST_COUNTRY_NAME",
    expr("DEST_COUNTRY_NAME")
).show(2)
+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+
|DEST_COUNTRY_NAME|DEST_COUNTRY_NAME|DEST_COUNTRY_NAME|DEST_COUNTRY_NAME|DEST_COUNTRY_NAME|DEST_COUNTRY_NAME|
+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+
|    United States|    United States|    United States|    United States|    United States|    United States|
|    United States|    United States|    United States|    United States|    United States|    United States|
+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+

df.select(
    'dest_country_name,
    expr("count + 99000000 as count_plus")
    ).show(2)
+-----------------+----------+
|dest_country_name|count_plus|
+-----------------+----------+
|    United States|  99000015|
|    United States|  99000001|
+-----------------+----------+

// selectExpr lets you bring that together
df.selectExpr("dest_country_name as newName",
    "count + 99000",
    "dest_country_name")
    .show(2)
+-------------+---------------+-----------------+
|      newName|(count + 99000)|dest_country_name|
+-------------+---------------+-----------------+
|United States|          99015|    United States|
|United States|          99001|    United States|
+-------------+---------------+-----------------+

df.select(
    'dest_country_name,
    'count
    )
    .groupBy("dest_country_name")
    .agg(
        sum("count")
    ).show(2)
+-----------------+----------+
|dest_country_name|sum(count)|
+-----------------+----------+
|         Anguilla|        41|
|           Russia|       176|
+-----------------+----------+

// addition to null yields null!!
df.select(
    'dest_country_name,
    'count
    )
    .groupBy("dest_country_name")
    .agg(
        sum("count"),
        sum(expr("count + null")).alias("plus_null")
    ).show(2)
+-----------------+----------+---------+
|dest_country_name|sum(count)|plus_null|
+-----------------+----------+---------+
|         Anguilla|        41|     null|
|           Russia|       176|     null|
+-----------------+----------+---------+

// selectExpr is very cool
df.selectExpr(
    "*",
    "dest_country_name = origin_country_name as withinCountry"
    ).show(2)
+-----------------+-------------------+-----+-------------+
|DEST_COUNTRY_NAME|ORIGIN_COUNTRY_NAME|count|withinCountry|
+-----------------+-------------------+-----+-------------+
|    United States|            Romania|   15|        false|
|    United States|            Croatia|    1|        false|
+-----------------+-------------------+-----+-------------+

```

## filtering
```scala
// 'filter' and 'where' are identical
df.filter('count > 40).show(2)

df.where('count > 40).show(2)

// combining filters
df.filter('count > 40)
    .filter('origin_country_name =!= "Ireland")
    .show(2)
```

## sorting
```scala
// deduplicate rows
.distinct()

// count rows
.count()

// sorting
import org.apache.spark.sql.functions.{desc, asc}
df.sort("count").show(3)
df.orderBy("count").show(3)
df.sort("count","dest_country_name").show(3)
df.orderBy(expr("count desc")).show(3)  // didn't work?
df.orderBy(desc("count")).show(3)

desc_nulls_last

// sort within partitions before other operations, as optimization
val df_sorted = spark.read.format("json").load("data/flight-data/json/2015-summary.json")
    .sortWithinPartitions("count")

```

## more
```scala
// sample rows
.sample(#withReplacement,#fraction,#seed)

// random 
dataFrames = df.randomSplit(Array(0.25, 0.75), #seed)
// dataFrames[0] and dataFrames[1]

// how many partitions in data frame?
df.rdd.getNumPartitions

```

## create manual dataframe 
```scala
import org.apache.spark.sql.Row
val row1 = Row("hello",null,1,false)
val row2 = Row("hello",3.5,1,false)
// next?
```

## merlin_test dataframe
nulls, functions, etc
```scala
val dfTest = spark.read
    .option("inferSchema","true")
    .option("header","true")
    .csv("data/merlin_test_nulls.csv")
dfTest.printSchema
dfTest.createOrReplaceTempView ("merlin_test_nulls") 
// root
//  |-- asin: string (nullable = true)
//  |-- gl: integer (nullable = true)
//  |-- ops: double (nullable = true)
//  |-- qty: integer (nullable = true)

dfTest.show()
 +----------+---+------+----+
 |      asin| gl|   ops| qty|
 +----------+---+------+----+
 |B00000XXX1| 21|  null|null|
 |B00000XXX2| 21| 50.45|   5|
 |B00000XXX3| 79| 23.99|   1|
 |B00000XXX4| 21|101.05|   3|
 +----------+---+------+----+

val nullRepl = dfTest.select(
    $"asin"
    ,coalesce($"ops",lit(0)).alias("ops_0")
    ,coalesce($"qty",lit(0)).alias("qty_0")
    )
nullRepl.show()
   +----------+------+-----+
   |      asin| ops_0|qty_0|
   +----------+------+-----+
   |B00000XXX1|   0.0|    0|
   |B00000XXX2| 50.45|    5|
   |B00000XXX3| 23.99|    1|
   |B00000XXX4|101.05|    3|
   +----------+------+-----+

val nullRepl = dfTest.select(
    $"asin"
    ,expr("nvl($ops),0)").alias("ops_0")
    )
```
