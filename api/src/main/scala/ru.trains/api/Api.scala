package ru.trains.api

import java.util.Date

import scala.concurrent.Future

trait Api {
  def searchStation(name: String):Future[List[Station]]
  def getTimetable(from: Station, to:Station):Future[List[Thread]]
}

case class Station(id: String, name: String) {
  override def toString = name
}
case class Thread(from: String, to: String, start: Date, end: Date, info: String)