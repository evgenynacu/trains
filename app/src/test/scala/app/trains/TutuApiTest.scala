package app.trains

import org.junit.Test

import scala.concurrent.Await
import scala.concurrent.duration._

class TutuApiTest {
  @Test
  def test(): Unit = {
    println(Await.result(TutuApi.searchStation("Переделкино"), 10 seconds))
  }
}
