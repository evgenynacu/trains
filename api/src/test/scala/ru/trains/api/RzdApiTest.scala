package ru.trains.api

import junit.framework.TestCase

import scala.concurrent.Await
import scala.concurrent.duration._

class RzdApiTest extends TestCase {
   def testSuggest(): Unit = {
     println(Await.result(RzdApi.searchStation("Переделкино"), 10 seconds))
   }
 }
