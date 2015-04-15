package model.commercial.money

import scala.xml.{Elem, Node}

object CreditCards {

  def currentAds: Map[String, Seq[CreditCard]] = Map(
    "balance-transfer" -> creditCardsAgent.BalanceTransferAndPurchase.available,
    "purchases" -> creditCardsAgent.Purchase.available,
    "balance-transfer-and-purchases" -> creditCardsAgent.BalanceTransfer.available,
    "cashback" -> creditCardsAgent.Cashback.available,
    "low-standard-rate" -> creditCardsAgent.LowStandardRate.available,
    "rewards" -> creditCardsAgent.Rewards.available,
    "bad-credit" -> creditCardsAgent.LowCredit.available
  )

}

case class CreditCard(name: String,
                      provider: String,
                      balanceTransferRate: Double,
                      balanceTransferRateDuration: Int,
                      balanceTransferFee: Double,
                      example: CreditExample,
                      logoUrl: String,
                      applyUrl: String,
                      rewardNotes: String,
                      cashbackNotes: String,
                      representativeApr: Double,
                      purchaseRate: Double,
                      purchaseRateDuration: Int)


case class CreditExample(amount: Double,
                         interestRate: Double,
                         interestRateDescription: String,
                         interestRateFixed: Boolean,
                         apr: Double,
                         aprFixed: Boolean,
                         fee: Double) {

  override val toString: String = {
    def moneyFormat(d: Double) = f"£$d%,.0f"
    def fixedText(b: Boolean) = if (b) "fixed" else "variable"
    val rateType = fixedText(interestRateFixed)
    val aprType = fixedText(aprFixed)
    val spendText = s"If you spend ${moneyFormat(amount)}"
    val rateText = s"at $interestRateDescription interest rate of $interestRate% p.a. ($rateType)"
    val feeText = if (fee > 0) s" with a ${moneyFormat(fee)} annual fee" else ""
    val aprText = s"your representative rate will be $apr% APR ($aprType)"
    s"$spendText $rateText$feeText $aprText"
  }
}


trait CreditCardsFeed extends MoneySupermarketFeed[CreditCard] {

  def parse(xml: Elem): Seq[CreditCard] = {

    def parseCreditExample(product: Node) = {
      CreditExample(
        (product \ "AmountOfCredit").text.toDouble,
        (product \ "InterestRate").text.toDouble,
        (product \ "InterestRateDescription").text,
        (product \ "InterestRateIsFixedRate").text.toBoolean,
        (product \ "RepresentiveApr").text.toDouble,
        (product \ "RepresentiveAprIsFixedRate").text.toBoolean,
        (product \ "Fee").text.toDouble
      )
    }

    xml \ "Product" map {
      product =>
        CreditCard(
          (product \ "CardName").text,
          (product \ "ProviderName").text,
          (product \ "BalanceTransferRate").text.toDouble,
          (product \ "BalanceTransferRateDuration").text.toInt,
          (product \ "BalanceTransferFee").text.toDouble,
          parseCreditExample(product),
          (product \ "LogoUrl").text,
          (product \ "ApplyUrl").text,
          (product \ "RewardNotes").text,
          (product \ "CashbackNotes").text,
          (product \ "RepresentiveApr").text.toDouble,
          (product \ "PurchaseRate").text.toDouble,
          (product \ "PurchaseRateDuration").text.toInt
        )
    }
  }
}

package creditCardsApi {

  object BalanceTransfer extends CreditCardsFeed {
    protected val adTypeName = "Credit Cards - Balance Transfer"
    protected lazy val path = "cards/balance-transfer"
  }
  object Purchase extends CreditCardsFeed {
    protected val adTypeName = "Credit Cards - Purchase"
    protected lazy val path = "cards/purchase"
  }
  object BalanceTransferAndPurchase extends CreditCardsFeed {
    protected val adTypeName = "Credit Cards - Balance Transfer and Purchase"
    protected lazy val path = "cards/balance-transfer-and-purchase"
  }
  object Cashback extends CreditCardsFeed {
    protected val adTypeName = "Credit Cards - Cashback"
    protected lazy val path = "cards/cashback"
  }
  object LowStandardRate extends CreditCardsFeed {
    protected val adTypeName = "Credit Cards - Low Standard Rate"
    protected lazy val path = "cards/low-standard-rate"
  }
  object Rewards extends CreditCardsFeed {
    protected val adTypeName = "Credit Cards - Rewards"
    protected lazy val path = "cards/rewards"
  }
  object LowCredit extends CreditCardsFeed {
    protected val adTypeName = "Credit Cards - Low Credit"
    protected lazy val path = "cards/low-credit"
  }

}


package creditCardsAgent {

  object BalanceTransfer extends MoneyAgent[CreditCard] {
    protected def loadProducts() = creditCardsApi.BalanceTransfer.loadAds()
  }
  object Purchase extends MoneyAgent[CreditCard] {
    protected def loadProducts() = creditCardsApi.Purchase.loadAds()
  }
  object BalanceTransferAndPurchase extends MoneyAgent[CreditCard] {
    protected def loadProducts() = creditCardsApi.BalanceTransferAndPurchase.loadAds()
  }
  object Cashback extends MoneyAgent[CreditCard] {
    protected def loadProducts() = creditCardsApi.Cashback.loadAds()
  }
  object LowStandardRate extends MoneyAgent[CreditCard] {
    protected def loadProducts() = creditCardsApi.LowStandardRate.loadAds()
  }
  object Rewards extends MoneyAgent[CreditCard] {
    protected def loadProducts() = creditCardsApi.Rewards.loadAds()
  }
  object LowCredit extends MoneyAgent[CreditCard] {
    protected def loadProducts() = creditCardsApi.LowCredit.loadAds()
  }

}
