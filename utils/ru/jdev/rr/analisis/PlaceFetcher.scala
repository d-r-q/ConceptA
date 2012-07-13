package ru.jdev.rr.analisis

import io.Source
import xml.pull.{EvElemEnd, EvText, EvElemStart, XMLEventReader}

/**
 * User: jdev
 * Date: 12.07.12
 */

object PlaceFetcher {

  def main(args: Array[String]) {
    fetchRatings
  }

  def fetchRatings {
    //val response = XML.loadString(urlToStream("http://darkcanuck.net/rumble/Rankings?game=roborumble").getLines().mkString)

    val toStream = Source.fromInputStream(urlToStream("http://darkcanuck.net/rumble/Rankings?game=roborumble"))
    println("is taked")
    val es = new XMLEventReader(toStream)

    var isBodyStarted = false
    var tdIdx = 0
    var place = 0
    var tdStarted = false
    while (es.hasNext) {
      es.next() match {
        case e: EvElemStart => {
          e.label match {
            case "tbody" => isBodyStarted = true
            case "tr" => tdIdx = 0
            case "td" => {
              tdStarted = true
              tdIdx += 1
            }
            case _ =>
          }
        }
        case e: EvText => {
          if (tdStarted) {
            if (tdIdx == 1) {
              place = Integer.parseInt(e.text)
              println(place)
            }
          }
        }
        case e: EvElemEnd =>
          e.label match {
            case "td" => tdStarted = false
            case _ =>
          }
        case _ =>
      }
    }
  }

  def urlToStream(url: String) =
    (new java.net.URL(url).openConnection match {
      case connection: java.net.HttpURLConnection => {
        connection.setInstanceFollowRedirects(true)
        connection
      }
      case connection => connection
    }).getInputStream

}
