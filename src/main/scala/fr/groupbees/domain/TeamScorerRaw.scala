package fr.groupbees.domain

case class TeamScorerRaw(scorerFirstName: String,
                         scorerLastName: String,
                         goals: Long,
                         goalAssists: Long,
                         games: Long)
  extends Serializable