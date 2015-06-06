package ru.trains.api

import scala.concurrent.Future

trait Api {
  def searchStation(name: String):Future[List[Station]]
}

case class Station(id: String, name: String)