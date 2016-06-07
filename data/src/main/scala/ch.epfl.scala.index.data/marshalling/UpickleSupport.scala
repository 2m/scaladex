package ch.epfl.scala.index
package data
package marshalling

import akka.http.scaladsl.marshalling.{ Marshaller, ToEntityMarshaller }
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.unmarshalling.{ FromEntityUnmarshaller, Unmarshaller }
import upickle.default.{ Reader, Writer, readJs, writeJs }
import upickle.{ Js, json }

// https://github.com/hseeberger/akka-http-json/issues/69
trait UpickleSupport {
  implicit def upickleUnmarshaller[A](implicit reader: Reader[A]): FromEntityUnmarshaller[A] =
    Unmarshaller.byteStringUnmarshaller
      .forContentTypes(`application/json`)
      .mapWithCharset((data, charset) => readJs[A](json.read(data.decodeString(charset.nioCharset.name))))

  implicit def upickleMarshaller[A](implicit writer: Writer[A], 
                                             printer: Js.Value => String = json.write(_, 0)
  ): ToEntityMarshaller[A] =
    Marshaller.StringMarshaller.wrap(`application/json`)(printer).compose(writeJs[A])
}