package mazl.endcode.test

object column2row extends App {

  private val maps = List(
    Map("userId" -> "901183", "dayOfMonth" -> 1, "hour" -> 8.50, "projId" -> 22),
    Map("userId" -> "901183", "dayOfMonth" -> 2, "hour" -> 3.50, "projId" -> 22),
    Map("userId" -> "901183", "dayOfMonth" -> 3, "hour" -> 12.50, "projId" -> 22),
    Map("userId" -> "901183", "dayOfMonth" -> 4, "hour" -> 4.50, "projId" -> 22),
    Map("userId" -> "901183", "dayOfMonth" -> 5, "hour" -> 7.50, "projId" -> 22),
    Map("userId" -> "901183", "dayOfMonth" -> 6, "hour" -> 9.50, "projId" -> 22),
    Map("userId" -> "901183", "dayOfMonth" -> 7, "hour" -> 1.50, "projId" -> 22),
    Map("userId" -> "901183", "dayOfMonth" -> 8, "hour" -> 0.50, "projId" -> 22),
    Map("userId" -> "901183", "dayOfMonth" -> 1, "hour" -> 1.50, "projId" -> 22),
    Map("userId" -> "901111", "dayOfMonth" -> 1, "hour" -> 8.50, "projId" -> 22),
    Map("userId" -> "901111", "dayOfMonth" -> 2, "hour" -> 3.50, "projId" -> 22),
    Map("userId" -> "901111", "dayOfMonth" -> 3, "hour" -> 12.50, "projId" -> 22),
    Map("userId" -> "901111", "dayOfMonth" -> 4, "hour" -> 4.50, "projId" -> 22),
    Map("userId" -> "901111", "dayOfMonth" -> 5, "hour" -> 7.50, "projId" -> 22),
    Map("userId" -> "901111", "dayOfMonth" -> 6, "hour" -> 9.50, "projId" -> 22),
    Map("userId" -> "901111", "dayOfMonth" -> 7, "hour" -> 1.50, "projId" -> 22),
    Map("userId" -> "901111", "dayOfMonth" -> 8, "hour" -> 0.50, "projId" -> 22),
    Map("userId" -> "90111", "dayOfMonth" -> 1, "hour" -> 1.50, "projId" -> 22)
  )

  maps.groupBy(mapOrigin => mapOrigin.get("userId")).collect({case strMap:(String,List[Map[String,Any]]) => {
    println(strMap._1)
    println(strMap._2.length)
    val doubles = strMap._2.groupBy(dayGroup => dayGroup.get("dayOfMonth")).collect({
      case splitDay: (String, List[Map[String, Any]]) => {
        println(splitDay._1)
        //userid sameday all projId
        val oneManoneDaySum = splitDay._2.map(oneDayMan => {
          val option = oneDayMan.get("hour")
          if (option.nonEmpty) {
            option.get.asInstanceOf[Double]
          } else {
            0.00
          }
        }).foldLeft(0.00)((sum, i) => sum + i)
        println(oneManoneDaySum)
        ("day"+splitDay._1.get,oneManoneDaySum)
      }
    })
    val units = strMap._2.groupBy(projEveryDay => projEveryDay.get("dayOfMonth")).collect({
      case projSingleDay: (String, List[Map[String, Any]]) => {
        val projId = projSingleDay._2.map(projMapDay => {
          val value = projMapDay.get("projId").get
          value
        }).reduce((projId1, projId2) => projId1 + "," + projId2)


        ("dayProj" + projSingleDay._1.get, projId)
      }
    })
    units

    println(doubles+("userId"->strMap._1.get))
    println(units ++ doubles)
  }})
  maps
}
