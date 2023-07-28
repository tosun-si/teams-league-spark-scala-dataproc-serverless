package fr.groupbees.domain

import TeamStatsRaw.TEAM_EMPTY_ERROR_MESSAGE
import fr.groupbees.domain.exception.TeamStatsRawValidatorException

import java.util.Objects

case class TeamStatsRaw(teamName: String,
                        teamScore: Long,
                        scorers: List[TeamScorerRaw])
  extends Serializable {

  def validateFields(): TeamStatsRaw = {
    if (Objects.isNull(teamName) || teamName == "") {
      throw new TeamStatsRawValidatorException(TEAM_EMPTY_ERROR_MESSAGE)
    }
    this
  }
}

object TeamStatsRaw {
  private val TEAM_EMPTY_ERROR_MESSAGE = "Team name cannot be null or empty"
}
