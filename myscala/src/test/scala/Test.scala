/**
 * @description:
 * @author: malichun
 * @time: 2020/9/16/0016 15:09
 *
 */
object TestSymbol {
    implicit class richSymbol(s:Symbol){

        def s2Person: Person ={
            new Person(s)
        }
    }

    def main(args: Array[String]): Unit = {
        val s:Symbol = 'aSymbolName

        s.s2Person.printName()

    }

}


class Person(name:Symbol){

    def printName(): Unit ={
        println(name)
    }

}