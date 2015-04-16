package views.support

object Treat {
    def fromUrl(url: String) = {
        if (url.contains("/crosswords")) {
            CrosswordTreat
        } else if (url.contains("/how-to-register-to-vote")) {
            RegisterToVoteTreat
        } else {
            NormalTreat
        }
    }
}

sealed trait Treat

case object CrosswordTreat extends Treat
case object RegisterToVoteTreat extends Treat
case object NormalTreat extends Treat

