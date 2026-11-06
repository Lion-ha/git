package prewarning

import org.apache.spark.sql.SparkSession

object JDBC {
  def main(args: Array[String]): Unit = {
    val spark:SparkSession = SparkSession.builder()
      .master("local[*]")
      .appName(this.getClass.getSimpleName)
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    val partition = args(0)
    val df1 = spark.sql(
      s"""
         |SELECT
         | hostname,
         | servicename,
         | logtype,
         | words,
         | COUNT(1)  AS cnt,
         | ${partition} as dt
         |FROM(
         | SELECT
         |   hostname,
         |   servicename,
         |   logtype,
         |   words
         | FROM
         |   ods_prewarning o
         | LATERAL VIEW
         |   EXPLODE(SPLIT(loginfo," ")) tb1 AS words
         | WHERE
         |   dt = $partition AND (logtype = "ERROR" OR logtype = "WARN" )
         |  )tb2
         |GROUP BY
         | hostname,servicename,logtype,words
         |""".stripMargin)

    df1.write.mode("append")
      .format("jdbc")
      .option("url","jdbc:mysql://node01:3306/mydata")
      .option("driver","com.mysql.jdbc.Driver")
      .option("dbtable","ods_prewarning")
      .option("user","root")
      .option("password","123456")
      .option("batchsize", "1000")
      .save()

    spark.stop()
  }
}
