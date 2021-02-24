import java.io.{BufferedWriter, File, FileWriter}
import java.time.{LocalDateTime, ZoneOffset}

import com.alibaba.fastjson.{JSON, JSONObject}

import scala.io.{BufferedSource, Source}



/**
 * @description:
 * @author: malichun
 * @time: 2021/1/20/0020 10:27
 *
 */
object FileTest {

    def main(args: Array[String]): Unit = {
//        val source = Source.fromFile("/data/algorithm/algorithm3/data/new_no_request_id/new_sample.txt")
//        val iter = source.getLines()
//        (new BufferedWriter(new FileWriter(new File("/data/algorithm/algorithm3/data/new_no_request_id/new_sample.txt2"))) /: iter){ case (f,v) =>
//            val arr = v.split(",")
//            val newLine = List(arr(1),arr(3),arr(4),arr(14),arr(15),arr(16),arr(17),arr(18),arr(19),arr(20),arr(21),arr(22),arr(23),arr(5),arr(6),arr(7),arr(8),arr(9),arr(10),arr(11),arr(12),arr(13)).mkString(",")
//
//            f.write(newLine)
//            f.newLine()
//            f.flush()
//            f
//        } close()

        val name="张三"
        val id = "123"
        val jsonObject  = json"""{"name":"$name","id":$id}"""
        println(jsonObject.getString("name"))
        println(jsonObject.getInteger("id"))
        println(jsonObject)

        val set: Set[Int] = (1 to 14).toSet
        val subsets: Seq[List[Int]] = set.subsets().map(s => s.toList.sortBy(x => x)).toVector.tail
        println(subsets.length+"==============")
        subsets foreach (arr => println("\"select_cols\":[" + arr.mkString(", ") + "]==="+ arr.count(e => (e == 11 || e == 12 || e == 13 || e == 14))))

        val source: BufferedSource = Source.fromFile("C:\\Users\\PC\\Desktop\\a.txt")
        println(source.getLines().map(_.toDouble).max)


        import java.time.format.DateTimeFormatter
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00")
        val time = LocalDateTime.ofEpochSecond("1611892800".toLong, 0, ZoneOffset.ofHours(8))
        formatter.format(time)

    }

    //emails"测试${email}测试"
    //
    //new EmailHelper(new StringContext("测试", "测试")).emails(email)

    implicit class JsonHelper(val sc:StringContext) extends AnyVal{

        def json(args: Any*):JSONObject = {
            val strings = sc.parts.iterator
            val expression = args.iterator
            val buf = new StringBuilder(strings.next())
            while(strings.hasNext){
                buf append expression.next
                buf append strings.next
            }
            JSON.parseObject(buf.toString)
        }
    }

    def getMax(): Unit ={
        val array = Array.ofDim[Int](1, 2)
    }

    def getMaxSum(d:Array[Array[Int]] ,n:Int,i:Int,j:Int) = {

    }


}



