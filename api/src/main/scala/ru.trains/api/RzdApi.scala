package ru.trains.api

import java.text.SimpleDateFormat
import java.util
import java.util.Date
import java.util.Map.Entry

import de.mastacode.http.Http
import org.apache.http.impl.client.DefaultHttpClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.parsing.json.{JSON, JSONArray, JSONObject}

object RzdApi extends Api {
  private val hardCacheCapacity = 10
  private val format = new SimpleDateFormat("dd.MM.yyyy")
  private val timeFormat = new SimpleDateFormat("HH:mm")

  val client = new DefaultHttpClient()

  val cache = new util.LinkedHashMap[String, List[Station]]() {
    override def removeEldestEntry(eldest: Entry[String, List[Station]]): Boolean = size() > hardCacheCapacity
  }

  private def suggest(search: String) =
    if (cache.containsKey(search)) {
      Promise.successful(cache.get(search)).future
    } else Future {
      val result = JSON.parseRaw(Http.get("http://pass.rzd.ru/suggester").use(client).data("lang", "ru").data("lat", "0").data("compactMode", "y").data("stationNamePart", search).asString())
        .get.asInstanceOf[JSONArray].list.map {
        case item:JSONObject => Station(item.obj("c").asInstanceOf[Double].toInt.toString, item.obj("n").asInstanceOf[String])
      }
      cache.put(search, result)
      result
    }

  override def searchStation(name: String) = if(name == null || name.trim.length < 2) {
    Promise.successful(List()).future
  } else {
    suggest(name.trim.substring(0, 2).toUpperCase).map(_.filter(_.name.contains(name.toUpperCase)))
  }

  override def getTimetable(from: Station, to: Station) = Future {
    def map(item: JSONObject) = Thread(
      item.obj("route0").asInstanceOf[String],
      item.obj("route1").asInstanceOf[String],
      timeFormat.parse(item.obj("time0").asInstanceOf[String]),
      timeFormat.parse(item.obj("time1").asInstanceOf[String]),
      item.obj("stList").asInstanceOf[String]
    )

    val now: Date = new Date()
    val tomorrow = new Date(now.getTime + 86400000)
    val result: String = Http.get("http://pass.rzd.ru/timetable/public/ru").use(client)
      .data("STRUCTURE_ID", "735").data("layer_id", "5371").data("dir", "0").data("tfl", "2")
      .data("checkSeats", "0").data("withoutSeats", "y").data("base", "0")
      .data("code0", from.id).data("code1", to.id)
      .data("dt0", format.format(now)).data("dt1", format.format(tomorrow)).asString()
    JSON.parseRaw(result).get.asInstanceOf[JSONObject]
      .obj("tp").asInstanceOf[JSONArray].list.head.asInstanceOf[JSONObject].obj("list").asInstanceOf[JSONArray].list.map {
      case item:JSONObject => map(item)
    }
  }
}
