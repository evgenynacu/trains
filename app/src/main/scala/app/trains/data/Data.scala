package app.trains.data

import android.content.{ContentValues, ContentProvider}
import android.net.Uri

class Data extends ContentProvider {
  def getType(uri: Uri) = ???

  def update(uri: Uri, values: ContentValues, selection: String, selectionArgs: Array[String]) = ???

  def insert(uri: Uri, values: ContentValues) = ???

  def delete(uri: Uri, selection: String, selectionArgs: Array[String]) = ???

  def onCreate() = ???

  def query(uri: Uri, projection: Array[String], selection: String, selectionArgs: Array[String], sortOrder: String) = ???
}
