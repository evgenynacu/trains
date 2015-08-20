package app.trains

import app.trains.layout.SearchLayout
import reactive.Observing
import reactive.android.app.ReactiveActivity

class MainActivity extends ReactiveActivity with Observing {

  for (e <- eCreate) {
    setContentView(new SearchLayout())
  }
}


