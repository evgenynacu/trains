package app.trains.adapter

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.ViewGroup
import reactive.{Observing, Signal}

class SignalSeqAdapter[T, VH <: ViewHolder](signal: Signal[Option[Seq[T]]], renderer: AdapterRenderer[T, VH])
                                           (implicit observing: Observing)
  extends RecyclerView.Adapter[VH] {

  def getItemCount = signal.now.map(_.size).getOrElse(0)

  def onBindViewHolder(holder: VH, position: Int) = renderer.onBindViewHolder(holder, signal.now.map(_(position)).get)

  def onCreateViewHolder(parent: ViewGroup, viewType: Int) = renderer.onCreateViewHolder(parent, viewType)

  for(e <- signal.change) {
    notifyDataSetChanged()
  }
}

trait AdapterRenderer[T, VH <: ViewHolder] {
  def onBindViewHolder(holder: VH, value: T)

  def onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
}