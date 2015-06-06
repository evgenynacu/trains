package ru.trains.api

import java.util
import java.util.Map.Entry

import de.mastacode.http.Http
import org.apache.http.impl.client.DefaultHttpClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.parsing.json.{JSON, JSONArray, JSONObject}

object RzdApi extends Api {
  val hardCacheCapacity = 10

  val cache = new util.LinkedHashMap[String, List[Station]]() {
    override def removeEldestEntry(eldest: Entry[String, List[Station]]): Boolean = size() > hardCacheCapacity
  }

  private def suggest(search: String) =
    if (cache.containsKey(search)) {
      Promise.successful(cache.get(search)).future
    } else Future {
      JSON.parseRaw(Http.get("http://pass.rzd.ru/suggester").use(new DefaultHttpClient()).data("lang", "ru").data("lat", "0").data("compactMode", "y").data("stationNamePart", search).asString())
        .get.asInstanceOf[JSONArray].list.map {
        case item:JSONObject => Station(item.obj("c").asInstanceOf[Double].toInt.toString, item.obj("n").asInstanceOf[String])
      }
    }

  override def searchStation(name: String): Future[List[Station]] = if(name == null || name.trim.length < 2) {
    Promise.successful(List()).future
  } else {
    suggest(name.trim.substring(0, 2).toUpperCase).map(_.filter(_.name.contains(name.toUpperCase)))
  }
}
