package fr.groupbees.domain

case class TeamStats(teamName: String,
                     teamScore: Long,
                     teamTotalGoals: Long,
                     teamSlogan: String = "",
                     topScorerStats: TeamTopScorerStats,
                     bestPasserStats: TeamBestPasserStats)
  extends Serializable
