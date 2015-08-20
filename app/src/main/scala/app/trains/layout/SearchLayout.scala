package app.trains.layout

import android.content.Context
import android.widget.LinearLayout
import app.trains.R
import reactive.Var
import reactive.android.widget.ReactiveAutoComplete
import ru.trains.api.{RzdApi, Station}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class SearchLayout(implicit ctx: Context) extends LinearLayout(ctx) {
  setOrientation(LinearLayout.VERTICAL)

  val vFromStation = Var[Option[Station]](None)
  val vToStation = Var[Option[Station]](None)

  private def load(s:String) = Await.result(RzdApi.searchStation(s), Duration.Inf)
  private def autocomplete(v: Var[Option[Station]]) = new ReactiveAutoComplete[Station](v)(load)(R.layout.list_item, R.id.autoCompleteItem)

  addView(autocomplete(vFromStation))
  addView(autocomplete(vToStation))
}
