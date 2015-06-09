package ru.trains.api

import java.text.SimpleDateFormat
import java.util
import java.util.Date
import java.util.Map.Entry

import de.mastacode.http.Http
import org.apache.http.impl.client.DefaultHttpClient
import org.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

object RzdApi extends Api {
  private val hardCacheCapacity = 10
  private val format = new SimpleDateFormat("dd.MM.yyyy")
  private val timeFormat = new SimpleDateFormat("HH:mm")

  val client = new DefaultHttpClient()

  val cache = new util.LinkedHashMap[String, Seq[Station]]() {
    override def removeEldestEntry(eldest: Entry[String, Seq[Station]]): Boolean = size() > hardCacheCapacity
  }

  private def cacheAndReturn(search: String, result: Seq[Station]) = {
    cache.put(search, result)
    result
  }

  private class JSONArraySeq(arr: JSONArray) extends Seq[JSONObject] {
    override def length: Int = arr.length()

    override def apply(idx: Int): JSONObject = arr.getJSONObject(idx)

    override def iterator: Iterator[JSONObject] = new Iterator[JSONObject] {
      var idx = -1

      override def next(): JSONObject = {
        idx = idx + 1
        arr.getJSONObject(idx)
      }

      override def hasNext: Boolean = idx + 1 < arr.length()
    }
  }

  implicit def jsonArrToSeq(a: JSONArray): Seq[JSONObject] = new JSONArraySeq(a)

  private def suggest(search: String) =
    if (cache.containsKey(search)) {
      Promise.successful(cache.get(search)).future
    } else Future {
      cacheAndReturn(search, new JSONArray(Http.get("http://pass.rzd.ru/suggester").use(client).data("lang", "ru").data("lat", "0").data("compactMode", "y").data("stationNamePart", search).asString()).map {
        case item:JSONObject => Station(item.getString("c"), item.getString("n"))
      })
    }

  override def searchStation(name: String) = if(name == null || name.trim.length < 2) {
    Promise.successful(List()).future
  } else {
    suggest(name.trim.substring(0, 2).toUpperCase).map(_.filter(_.name.contains(name.toUpperCase)))
  }

  override def getTimetable(from: Station, to: Station) = Future {
    def map(item: JSONObject) = Thread(
      item.getString("route0"),
      item.getString("route1"),
      timeFormat.parse(item.getString("time0")),
      timeFormat.parse(item.getString("time1")),
      item.getString("stList")
    )

    val now: Date = new Date()
    val tomorrow = new Date(now.getTime + 86400000)
    val result: String = Http.get("http://pass.rzd.ru/timetable/public/ru").use(client)
      .data("STRUCTURE_ID", "735").data("layer_id", "5371").data("dir", "0").data("tfl", "2")
      .data("checkSeats", "0").data("withoutSeats", "y").data("base", "0")
      .data("code0", from.id).data("code1", to.id)
      .data("dt0", format.format(now)).data("dt1", format.format(tomorrow)).asString()
    new JSONObject(result).getJSONArray("tp").getJSONObject(0).getJSONArray("list").map {
      case item:JSONObject => map(item)
    }
  }
}
