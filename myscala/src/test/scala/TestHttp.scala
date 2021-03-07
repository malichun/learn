import java.io.{BufferedReader, InputStreamReader}
import java.nio.charset.Charset
import java.util

import com.alibaba.fastjson.JSONObject
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
//import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks.{break, breakable}

/**
 * @description:
 * @author: malichun
 * @time: 2020/12/15/0015 19:53
 *
 */
object TestHttp {

    def doPost(k: String, iter: Iterable[String]) = {
//        val tagFront = k match {
//            case "IMEI" => "hv:m:"
//            case "OAID" => "hv:o:"
//            case "IDFA" => "hv:i:"
//        }
//
//        val return_ = ListBuffer[String]()
//
//        val cm = new PoolingHttpClientConnectionManager()
//        cm.setMaxTotal(100)
//        cm.setDefaultMaxPerRoute(20)
//        cm.setDefaultMaxPerRoute(50)
//        val httpClient: CloseableHttpClient = HttpClients.custom().setConnectionManager(cm).build()
//        val url = "http://172.16.209.166:8567/alipay_rta"
//        val httpPost = new HttpPost(url)
//        val requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build()
//        httpPost.setConfig(requestConfig)
//        httpPost.setConfig(requestConfig)
//        httpPost.addHeader("Content-type", "application/json; charset=utf-8")
//        httpPost.setHeader("Accept", "application/json")
//        val NL = System.getProperty("line.separator")
//        val iterator: Iterator[String] = iter.iterator
//        val deviceIds = new util.ArrayList[String]()
//        val jsonObject = new JSONObject()
//        jsonObject.put("device_type", k)
//        jsonObject.put("device_ids", deviceIds)
//
//        while (iterator.hasNext) {
//            if (deviceIds.size != 5) {
//                deviceIds.add(iterator.next())
//            } else {
//                val jsonString: String = jsonObject.toJSONString
//                httpPost.setEntity(new StringEntity(jsonString, Charset.forName("UTF-8")))
//                val response = httpClient.execute(httpPost)
//                val in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
//                val sb = new StringBuilder();
//                var line = ""
//
//                breakable {
//                    while (true) {
//                        line = in.readLine()
//                        if (line nonEmpty) {
//                            sb.append(line + NL)
//                        } else {
//                            break()
//                        }
//                    }
//                }
//                in.close()
//                val result = sb.toString
//                deviceIds.clear()
//                response.close()
//                val jsonObject1 = JSON.parseObject(result)
//                val status = jsonObject1.getIntValue("status")
//                if (status != 0) {
//                    throw new Exception("==返回非0,接口报错==")
//                }
//                val data = jsonObject1.getJSONArray("data")
//                for (i <- 0 until data.size()) {
//                    val nObject: JSONObject = data.getJSONObject(i)
//                    val deviceId = nObject.getString("device_id")
//                    val deviceLabel = nObject.getString("device_label")
//
//                    return_.append(tagFront + deviceId + "_"+ deviceLabel)
//                }
//            }
//
//
//
//        }
//        if(deviceIds.size() != 0){
//            val jsonString: String = jsonObject.toJSONString
//            httpPost.setEntity(new StringEntity(jsonString, Charset.forName("UTF-8")))
//            val response = httpClient.execute(httpPost)
//            val in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
//            val sb = new StringBuilder();
//            var line = ""
//
//            breakable {
//                while (true) {
//                    line = in.readLine()
//                    if (line nonEmpty) {
//                        sb.append(line + NL)
//                    } else {
//                        break()
//                    }
//                }
//            }
//            in.close()
//            val result = sb.toString
//            deviceIds.clear()
//            response.close()
//            val jsonObject1 = JSON.parseObject(result)
//            val status = jsonObject1.getIntValue("status")
//            if (status != 0) {
//                throw new Exception("==返回非0,接口报错==")
//            }
//            val data = jsonObject1.getJSONArray("data")
//            for (i <- 0 until data.size()) {
//                val nObject: JSONObject = data.getJSONObject(i)
//                val deviceId = nObject.getString("device_id")
//                val deviceLabel = nObject.getString("device_label")
//
//                return_.append(tagFront + deviceId + "_"+ deviceLabel)
//            }
//        }
//
//        return_.toList
    }

    def main(args: Array[String]): Unit = {
        doPost("IMEI",List("9969ef1d73289350111b2755a347ceb4","9123124453289350111b2755a347ceb4"))
    }
}
