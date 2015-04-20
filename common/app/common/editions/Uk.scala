package common.editions

import common._
import org.joda.time.DateTimeZone

object Uk extends Edition(
  id = "UK",
  displayName = "UK edition",
  timezone = DateTimeZone.forID("Europe/London"),
  lang = "en-gb"){

  implicit val UK = Uk

  val sportLocalNav: Seq[SectionLink] = Seq(
    football,
    cricket,
    rugbyunion,
    formulaOne,
    tennis,
    golf,
    cycling,
    boxing,
    racing,
    rugbyLeague,
    usSport
  )

  val cultureLocalNav: Seq[SectionLink] = Seq(
    film,
    televisionAndRadio,
    music,
    games,
    books,
    artanddesign,
    stage,
    classicalMusic
  )

  val ukLocalNav = Seq(
    politics,
    education,
    media,
    society,
    law,
    scotland,
    wales,
    northernIreland
  )

  val worldLocalNav = Seq(
    europeNews,
    us,
    americas,
    asia,
    australia,
    africa,
    middleEast,
    cities,
    globalDevelopment
  )

  val businessLocalNav = Seq(
    economics,
    banking,
    retail,
    markets,
    eurozone
  )

  val environmentLocalNav = Seq(
    climatechange,
    wildlife,
    energy,
    pollution
  )


  override val navigation: Seq[NavItem] = {
    Seq(
      NavItem(home),
      NavItem(uk, ukLocalNav),
      NavItem(election2015),
      NavItem(world, worldLocalNav),
      NavItem(sport, sportLocalNav),
      NavItem(football, footballNav),
      NavItem(opinion, Seq(columnists)),
      NavItem(culture, cultureLocalNav),
      NavItem(business, businessLocalNav),
      NavItem(lifeandstyle, Seq(foodanddrink, healthandwellbeing, loveAndSex, family, women, homeAndGarden)),
      NavItem(fashion),
      NavItem(environment, environmentLocalNav),
      NavItem(technology),
      NavItem(travel, Seq(uktravel, europetravel, usTravel)),
      NavItem(money, Seq(property, savings, pensions, borrowing, workAndCareers)),
      NavItem(science),
      NavItem(guardianProfessional),
      NavItem(observer),
      NavItem(todaysPaper, Seq(editorialsandletters, obituaries, g2, weekend, theGuide, saturdayreview)),
      NavItem(membership),
      NavItem(crosswords),
      NavItem(video)
    )
  }

  override val briefNav: Seq[NavItem] = Seq(
    NavItem(home),
    NavItem(uk, ukLocalNav),
    NavItem(election2015),
    NavItem(world, worldLocalNav),
    NavItem(sport, sportLocalNav),
    NavItem(football, footballNav),
    NavItem(opinion, Seq(columnists)),
    NavItem(culture, cultureLocalNav),
    NavItem(business, businessLocalNav),
    NavItem(lifeandstyle, Seq(foodanddrink, healthandwellbeing, loveAndSex, family, women, homeAndGarden)),
    NavItem(fashion),
    NavItem(environment, environmentLocalNav),
    NavItem(technology),
    NavItem(travel, Seq(uktravel, europetravel, usTravel))
  )
}
