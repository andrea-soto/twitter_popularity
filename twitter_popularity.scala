import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.SparkContext._
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.SparkConf
import twitter4j._

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config

import java.util.Date
import java.text.SimpleDateFormat
import java.util.Calendar

object Main extends App {
  println(s"\nI got executed with ${args size} args, they are: ${args mkString ", "}\n")

  // your code goes here
  // get user input for batch duration, window duration, top n hastags, and filters
    val batchDur = args(0).toInt  //batch duration
    val winDur = args(1).toInt    //aggregation window duration
    val topN = args(2).toInt
    val filters = args.takeRight(args.length - 3)

    val format = new SimpleDateFormat("HH:mm:ss")

    //println("Input: {batch : %d, win : %d, topN:%d}".format(batchDur, winDur,topN))

    // Twitter4j library was configured to generate OAuth credentials
    
    val sparkConf = new SparkConf().setAppName("TwitterPopularTags")
    val ssc = new StreamingContext(sparkConf, Seconds(batchDur))
    val stream = TwitterUtils.createStream(ssc, None, filters)

    // Create DStream with tuple (hashtag, (1,user,mentions))
    val hashKeyVal = stream.map(tweet => {
        val hashtags = tweet.getText().split(" ").filter(word => word.startsWith("#"))
        val authors  = tweet.getUser.getScreenName()
        val mentions = tweet.getUserMentionEntities().map(_.getScreenName).toArray
        (hashtags, Set(authors), mentions)})
        .flatMap(tup => tup._1.map(hashtag => (hashtag, (1, tup._2, tup._3))))
        
    // Reduce by counting hashtag occurances and creating a set of users and mentions
    // Then sort by count
    val hashSortedCnt = hashKeyVal
                     .reduceByKeyAndWindow({case (x, y) =>
                                   (x._1 + y._1, x._2 ++ y._2, x._3 ++ y._3)}, Seconds(winDur))
                     .transform(_.sortBy({case (_, (count, _, _)) => count}, ascending = false))
                     
    // Take top N hashtags and print results       
    hashSortedCnt.foreachRDD( rdd => {
        var topHashes = rdd.take(topN)
        println("\n")
        println(format.format(Calendar.getInstance().getTime()))
        println("Top %d hashtags in last %d seconds (%s total):".format(topN, winDur, rdd.count()))
        topHashes.foreach{ case (tag, (cnt,users,mentions)) =>
                      println("  %s (%s tweets)".format(tag, cnt))
                      println("     Users (%s):    %s".format(users.size, users.mkString(",")))
                      println("     Mentions (%s): %s".format(mentions.size, mentions.mkString(",")))
                      }
        })


    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
}

