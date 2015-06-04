package app.trains

import java.net.URL


import scala.concurrent.Future
import scala.io.Source
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.parsing.json.{JSONObject, JSON, JSONArray}

object TutuApi extends Api {
  private def mapStation(jsonObject: JSONObject) = Station(jsonObject.obj("value").asInstanceOf[String], jsonObject.obj("label").asInstanceOf[String])

  override def searchStation(name: String): Future[List[Station]] = Future {
    JSON.parseRaw(Source.fromInputStream(new URL("http://www.tutu.ru/station/suggest.php?name=" + name).openStream()).mkString)
      .get.asInstanceOf[JSONArray].list.map(item => mapStation(item.asInstanceOf[JSONObject]))
  }
}
