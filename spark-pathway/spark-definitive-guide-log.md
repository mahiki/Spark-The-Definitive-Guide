# Spark Definitive Guide
[github: data and resources](https://github.com/databricks/Spark-The-Definitive-Guide)
[localhost UI](http://localhost:4040)

Chambers, Bill,Zaharia, Matei (2106-02-06T22:28:15). Spark: The Definitive Guide: Big Data Processing Made Simple (Kindle Locations 579-580). O'Reilly Media. Kindle Edition.)
## 2. Gentle Intro to Spark
```bash
brew install scala
brew install apache-spark
brew cask install homebrew/cask-versions/adoptopenjdk8
brew install apache-spark

$> spark-shell
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ / __/  _/
   /___/ .__/\_,_/_/ /_/\_\   version 2.4.3
      /_/
Using Scala version 2.11.12 (OpenJDK 64-Bit Server VM, Java 1.8.0_212)
Type in expressions to have them evaluated.
Type :help for more information.
```
```scala
scala> val s = "hello world"
s: String = hello world

:help
// lots of help
:quit
```

```bash
echo $SPARK_HOME
echo $PYTHONPATH
# nothing
which spark-shell
/usr/local/bin/spark-shell

# if that didn't work
export SPARK_HOME=/usr/local/Cellar/apache-spark/2.4.3/libexec
export PYTHONPATH=/usr/local/Cellar/apache-spark/2.4.3/libexec/python/:$PYTHONP$
```

### begin some testing on REPL
```scala
spark
res0: org.apache.spark.sql.SparkSession = org.apache.spark.sql.SparkSession@1e9b0c92

// create a range of numbers, then test evenness. 
// scala objects are immutable
val myRange = spark.range(1000).toDF("number")
val divisBy2 = myRange.where("number % 2 = 0") 

// lazy evaluation, nothing is done yet but a plan is forming
// now lets take an action
divisBy2.count()
res2: Long = 500
```

### actions: `count`, `read`, `take`, etc
```scala
```
