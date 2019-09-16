// test your spark with π computation
// part of default scala install
// spark/examples/src/main/scala/org/apache/spark/examples/SparkPi.scala 

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

import scala.math.random

object Pi {
  val conf = new SparkConf().setAppName("Spark Pi")

  def main(args: Array[String]) {
    val spark: SparkSession = SparkSession
      .builder
      .config(conf)
      .getOrCreate()
    val slices = if (args.length > 0) args(0).toInt else 2
    val n = math.min(100000L * slices, Int.MaxValue).toInt // avoid overflow
    val count = spark.sparkContext.parallelize(1 until n, slices).map { i =>
      val x = random * 2 - 1
      val y = random * 2 - 1
      if (x*x + y*y <= 1) 1 else 0
    }.reduce(_ + _)
    println("Pi is roughly " + 4.0 * count / (n - 1))
    spark.stop()
  }
}
