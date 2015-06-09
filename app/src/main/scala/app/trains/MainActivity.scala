package app.trains

import android.widget.LinearLayout
import reactive.android.app.ReactiveActivity
import reactive.android.widget.ReactiveAutoComplete
import reactive.{Observing, Var}
import ru.trains.api.{RzdApi, Station}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class MainActivity extends ReactiveActivity with Observing {
  lazy val main = findViewById(R.id.main).asInstanceOf[LinearLayout]
  val vStation = Var[Option[Station]](None)

  def autocomplete(s:String) = Await.result(RzdApi.searchStation(s), Duration.Inf)

  for (e <- eCreate) {
    setContentView(R.layout.main)
    main.addView(new ReactiveAutoComplete[Station](vStation)(autocomplete)(R.layout.list_item, R.id.autoCompleteItem))
  }
}


