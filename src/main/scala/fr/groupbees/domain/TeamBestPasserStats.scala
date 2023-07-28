package fr.groupbees.domain

case class TeamBestPasserStats(firstName: String,
                               lastName: String,
                               goalAssists: Long = 0,
                               games: Long = 0)
  extends Serializable
