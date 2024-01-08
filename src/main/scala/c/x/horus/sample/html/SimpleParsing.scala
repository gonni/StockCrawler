package c.x.horus.sample.html

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model._


object SimpleParsing extends App {

  val browser = new JsoupBrowser()
//  val doc = browser.get("https://finance.naver.com/item/frgn.naver?code=247540")
  val doc = browser.get("https://finance.naver.com/item/main.naver?code=247540")
  println(doc.toHtml)


  println("---------------")
  val headerText = doc >> elementList(".sub_section") >> elementList(".right")
  println(headerText)

}
