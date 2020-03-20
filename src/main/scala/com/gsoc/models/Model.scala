package com.gsoc.models

import zamblauskas.csv.parser.{ColumnReads, column}
import zamblauskas.functional._

sealed trait Model extends Product with Serializable

final case class Alert(field1: String, field2: String, field3: String) extends Model

object Alert {
  implicit val alertReads: ColumnReads[Alert] = (
    column("field1").as[String] and
      column("field2").as[String] and
      column("field3").as[String]
    )((field1, field2, field3) => Alert(field1, field2, field3))
}