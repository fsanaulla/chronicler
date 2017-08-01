

case class Test(fname: String, lname: String)

object Test {
  def getFieldValues(arg: Test) = List(arg.fname, arg.lname)
}

val t = Test("fayaz", "sanaulla")


def extractFieldNames[T<:Product:Manifest] = {
  implicitly[Manifest[T]].runtimeClass.getDeclaredFields
}

val values = Test.getFieldValues(t)

val fieldNames = extractFieldNames[Test].map(_.getName()).filterNot(_ == "$outer")

fieldNames.zip(values)

System.currentTimeMillis() * 1000000