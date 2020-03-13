package com.gsoc.model

sealed trait Model

final case class Alert(field1: String, field2: String, field3: String) extends Model
