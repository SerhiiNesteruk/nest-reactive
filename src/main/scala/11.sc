import scala.collection.mutable.ListBuffer

val buf = new ListBuffer[Int]()
buf ++=Seq(1,2,3)
val lst: List[Int] = buf.toIterable.toList //List(1,2,3)
println(lst) //List(1,2,3)
buf ++=Seq(4,5,6)
println(lst) //List(1,2,3,4,5,6)
