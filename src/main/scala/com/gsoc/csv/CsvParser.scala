package com.gsoc.csv

import zamblauskas.csv.parser._

trait Parser[T] {
  def parse(fileAsString: String): Either[Parser.Failure, Seq[T]]
}


final class CsvParser[T] extends Parser[T] {

  override def parse(fileAsString: String): Either[Parser.Failure, Seq[T]] = Parser.parse[T](fileAsString)

}

final class CsvParserMock[T] extends Parser[T] {

  override def parse(fileAsString: String): Either[Parser.Failure, Seq[T]] = Right(Seq.empty[T])

}