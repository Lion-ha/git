package prewarning

import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.spark.{SparkConf, SparkContext}

object ETL {
  def main(args: Array[String]): Unit = {
    val input = args(0)
    val output = args(1)

    val spark =  new SparkConf()
      .setMaster("local[*]")
    val sc = new SparkContext(spark)
    val rdd = sc.textFile(input)

    val rdd1 = rdd.map(log=>{
      val json: JSONObject = JSON.parseObject(log)
      val hostname = json.getString("hostname")
      val servicename = json.getString("servicename")
      val time = json.getString("time")
      val logtype = json.getString("logtype")
      val loginfo = json.getString("loginfo")
      Logs(hostname,servicename,time, logtype, loginfo)
    })

    rdd1.saveAsTextFile(output)

    sc.stop()
  }

  case class Logs(hostname:String,servicename:String,time:String,logtype:String,loginfo: String){
    override def toString: String =hostname+"\t"+ servicename+"\t"+time+"\t"+logtype+"\t"+loginfo
  }
}
