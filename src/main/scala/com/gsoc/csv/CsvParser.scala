package com.gsoc.csv

import com.gsoc.models.Model

import scala.util.Try
import zamblauskas.csv.parser._

trait Parser[T <: Model] {
  def parse(fileAsString: String)(implicit cr: ColumnReads[T]): Either[Parser.Failure, Seq[T]]
}

final class CsvParser[T <: Model] extends Parser[T] {

  def parse(fileAsString: String)(implicit cr: ColumnReads[T]): Either[Parser.Failure, Seq[T]] =
    Parser.parse[T](fileAsString)(cr)

}

final class CsvParserMock[T <: Model] extends Parser[T] {

  override def parse(fileAsString: String)(implicit cr: ColumnReads[T]): Either[Parser.Failure, Seq[T]] =
    Right(Seq.empty[T])

}
