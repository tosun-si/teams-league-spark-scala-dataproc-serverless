package fr.groupbees.domain

case class TeamTopScorerStats(firstName: String,
                              lastName: String,
                              goals: Long,
                              games: Long)
  extends Serializable
