package ru.trains.api

import junit.framework.TestCase

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class RzdApiTest extends TestCase {
  def testSearchStation(): Unit = {
    println(Await.result(RzdApi.searchStation("Переделкино"), 10 seconds))
  }

  def testGetTimetable(): Unit = {
    println(Await.result(RzdApi.getTimetable(Station("2000000", ""), Station("2001795", "")), 10 seconds))
  }
}
