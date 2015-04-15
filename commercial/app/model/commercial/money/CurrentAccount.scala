package model.commercial.money

import scala.xml.{Elem, Node}

object CurrentAccounts {

  def currentAds: Map[String, Seq[CurrentAccount]] = Map(
    "reward-incentive" -> currentAccountsAgent.Rewards.available,
    "high-interest" -> currentAccountsAgent.HighInterest.available,
    "overdraft" -> currentAccountsAgent.BestOverdraft.available,
    "with-benefits" -> currentAccountsAgent.WithBenefits.available,
    "basic-accounts" -> currentAccountsAgent.BasicAccounts.available,
    "standard-accounts" -> currentAccountsAgent.StandardAccounts.available
  )

}

case class CurrentAccount(provider: String,
                          name: String,
                          interestRate: Option[Double],
                          minimumBalance: Int,
                          overdraftRate: Option[Double],
                          overdraftBuffer: Option[Int],
                          logoUrl: String,
                          applyUrl: String,
                          rewardAmount: String,
                          monthlyCharge: String,
                          benefitAmount: String,
                          serviceCharge: String,
                          access: Map[String, Boolean])


trait CurrentAccountsFeed extends MoneySupermarketFeed[CurrentAccount] {

  override def cleanResponseBody(body: String) = body.replace("&", "&amp;")

  def parse(xml: Elem): Seq[CurrentAccount] = {

    def parseOptional(node: Node): Option[Double] = {
      val text = node.text
      if (text.length == 0 || text == "-") None else Some(text.toDouble)
    }

    xml \ "Product" map {
      product =>
        CurrentAccount(
          (product \ "ProviderName").text,
          (product \ "AccountName").text,
          parseOptional((product \ "InterestRate").head),
          (product \ "MinimumBalance").text.toInt,
          parseOptional((product \ "OverdraftRate").head),
          parseOptional((product \ "OverdraftBuffer").head).map(_.toInt),
          (product \ "LogoUrl").text.trim(),
          (product \ "ApplyUrl").text.trim(),
          (product \ "RewardAmount").text,
          (product \ "MonthlyCharge").text,
          (product \ "BenefitAmount").text,
          (product \ "ServiceCharge").text,
          Map(
            ("Branch", (product \ "AccessBranch").text.toBoolean),
            ("Post office", (product \ "AccessPostOffice").text.toBoolean),
            ("Online", (product \ "AccessOnline").text.toBoolean),
            ("Post", (product \ "AccessPost").text.toBoolean),
            ("Telephone", (product \ "AccessTelephone").text.toBoolean)
          )
        )
    }
  }
}

package currentAccountsFeed {

  object Rewards extends CurrentAccountsFeed {
    protected val adTypeName = "Current Accounts - Rewards"
    protected lazy val path = "currentaccounts/rewards"
  }
  object HighInterest extends CurrentAccountsFeed {
    protected val adTypeName = "Current Accounts - High Interest"
    protected lazy val path = "currentaccounts/high-interest"
  }
  object BestOverdraft extends CurrentAccountsFeed {
    protected val adTypeName = "Current Accounts - Best Overdraft"
    protected lazy val path = "currentaccounts/best-overdraft"
  }
  object WithBenefits extends CurrentAccountsFeed {
    protected val adTypeName = "Current Accounts - With Benefits"
    protected lazy val path = "currentaccounts/with-benefits"
  }
  object BasicAccounts extends CurrentAccountsFeed {
    protected val adTypeName = "Current Accounts - Basic Accounts"
    protected lazy val path = "currentaccounts/basic-accounts"
  }
  object StandardAccounts extends CurrentAccountsFeed {
    protected val adTypeName = "Current Accounts - Standard Accounts"
    protected lazy val path = "currentaccounts/standard-accounts"
  }

}


package currentAccountsAgent {

  object Rewards extends MoneyAgent[CurrentAccount] {
    protected def loadProducts() = currentAccountsFeed.Rewards.loadAds()
  }
  object HighInterest extends MoneyAgent[CurrentAccount] {
    protected def loadProducts() = currentAccountsFeed.HighInterest.loadAds()
  }
  object BestOverdraft extends MoneyAgent[CurrentAccount] {
    protected def loadProducts() = currentAccountsFeed.BestOverdraft.loadAds()
  }
  object WithBenefits extends MoneyAgent[CurrentAccount] {
    protected def loadProducts() = currentAccountsFeed.WithBenefits.loadAds()
  }
  object BasicAccounts extends MoneyAgent[CurrentAccount] {
    protected def loadProducts() = currentAccountsFeed.BasicAccounts.loadAds()
  }
  object StandardAccounts extends MoneyAgent[CurrentAccount] {
    protected def loadProducts() = currentAccountsFeed.StandardAccounts.loadAds()
  }

}
