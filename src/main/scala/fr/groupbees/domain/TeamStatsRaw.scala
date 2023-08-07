package fr.groupbees.domain

case class TeamStatsRaw(teamName: String,
                        teamScore: Long,
                        scorers: List[TeamScorerRaw])
  extends Serializable
