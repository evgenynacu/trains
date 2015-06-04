package app.trains

import android.database.Cursor
import android.provider.ContactsContract.Contacts
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.ViewGroup
import android.widget.TextView
import app.trains.adapter.{AdapterRenderer, SignalSeqAdapter}
import reactive.android.app.ReactiveActivity
import reactive.android.content.{ContentDescriptor, CursorSignal, RichCursor}
import reactive.{Observing, Val}

class MainActivity extends ReactiveActivity with Observing {
  lazy val rv = findViewById(R.id.rv).asInstanceOf[RecyclerView]
  val descriptor = ContentDescriptor(Contacts.CONTENT_URI, Array("display_name"), null, null)
  lazy val signal = new CursorSignal(Val(descriptor), this, 0, getLoaderManager).map(_.map(c => RichCursor.cursorToRich(c)))

  for (e <- eCreate) {
    setContentView(R.layout.main)
    rv.setLayoutManager(new LinearLayoutManager(this))
    rv.setAdapter(new SignalSeqAdapter[Cursor, ViewHolder](signal, renderer))
  }

  object renderer extends AdapterRenderer[Cursor, ViewHolder] {
    def onBindViewHolder(holder: ViewHolder, value: Cursor) = holder.tv.setText(value.getString(0))

    def onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(new TextView(MainActivity.this))
  }
}

case class ViewHolder(tv: TextView) extends RecyclerView.ViewHolder(tv)

