package model.commercial.books

import common.ExecutionContexts
import model.commercial._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

import scala.concurrent.Future
import scala.util.control.NonFatal

case class Book(title: String,
                author: Option[String],
                isbn: String,
                price: Option[Double] = None,
                offerPrice: Option[Double] = None,
                description: Option[String] = None,
                jacketUrl: Option[String],
                buyUrl: Option[String] = None,
                position: Option[Int] = None,
                category: Option[String] = None,
                keywordIdSuffixes: Seq[String] = Nil)

object Book {

  private val authorReads = {
    ((JsPath \ "author_firstname").readNullable[String] and
      (JsPath \ "author_lastname").readNullable[String])
      .tupled.map { case (optFirstName, optLastName) =>
      for {
        firstName <- optFirstName
        lastName <- optLastName
      } yield s"$firstName $lastName"
    }
  }

  implicit val bookReads: Reads[Book] = (
    (JsPath \ "name").read[String] and
      authorReads and
      (JsPath \ "isbn").read[String] and
      (JsPath \ "regular_price_with_tax").readNullable[String].map(_.map(_.toDouble)) and
      (JsPath \ "final_price_with_tax").readNullable[Double] and
      (JsPath \ "description").readNullable[String] and
      (JsPath \ "images")(0).readNullable[String] and
      (JsPath \ "product_url").readNullable[String] and
      (JsPath \ "guardian_bestseller_rank").readNullable[String].map(_.map(_.toDouble.toInt)) and
      ((JsPath \ "categories")(0) \ "name").readNullable[String] and
      (JsPath \ "keywordIds").readNullable[Seq[String]].map(_ getOrElse Nil)
    )(Book.apply _)
}


object BestsellersAgent extends MerchandiseAgent[Book] with ExecutionContexts {

  private lazy val feeds = Seq(MagentoBestsellersFeed)

  def getSpecificBook(isbn: String) = available find (_.isbn == isbn)

  def getSpecificBooks(isbns: Seq[String]): Future[Seq[Book]] = {
    Future.sequence {
      isbns map (BookFinder.findByIsbn(_))
    } map (_.flatten.sortBy(book => isbns.indexOf(book.isbn)))
  }

  def bestsellersTargetedAt(segment: Segment): Seq[Book] = {
    val targetedBestsellers = available filter { book =>
      Keyword.idSuffixesIntersect(segment.context.keywords, book.keywordIdSuffixes)
    }
    lazy val defaultBestsellers = available filter (_.category.contains("General"))
    val bestsellers = if (targetedBestsellers.isEmpty) defaultBestsellers else targetedBestsellers
    bestsellers.filter(_.jacketUrl.nonEmpty).sortBy(_.position).take(10)
  }

  def refresh(): Unit = {

    val bookListsLoading: Future[Seq[Seq[Book]]] = Future.sequence {
      feeds.foldLeft(Seq[Future[Seq[Book]]]()) {
        (soFar, feed) =>
          soFar :+ feed.loadBestsellers().recover {
            case e: FeedSwitchOffException =>
              log.warn(e.getMessage)
              Nil
            case e: FeedMissingConfigurationException =>
              log.warn(e.getMessage)
              Nil
            case NonFatal(e) =>
              log.error(e.getMessage)
              Nil
          }
      }
    }

    for (books <- bookListsLoading) {
      updateAvailableMerchandise(books.flatten.distinct)
    }
  }

}
