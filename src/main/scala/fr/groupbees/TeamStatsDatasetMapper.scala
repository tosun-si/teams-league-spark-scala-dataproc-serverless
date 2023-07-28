package fr.groupbees

import fr.groupbees.domain._
import org.apache.spark.sql.functions.{col, udf}
import org.apache.spark.sql.{DataFrame, Dataset}

object TeamStatsDatasetMapper {

  private val rawColumnScorersName = "scorers"
  private val rawColumnTeamName = "teamName"
  private val rawColumnTeamSlogan = "slogan"
  private val domainColumnTotalGoalsName = "teamTotalGoals"
  private val domainColumnSloganName = "teamSlogan"
  private val domainColumnTopScorerName = "topScorerStats"
  private val domainColumnBestPasserName = "bestPasserStats"

  def toTeamStatsDataset(teamStatsRawDataset: Dataset[TeamStatsRaw],
                         teamSlogansDataframe: DataFrame): Dataset[TeamStats] = {
    val teamStatsEncoder = org.apache.spark.sql.Encoders.product[TeamStats]

    val totalGoalsUdf = udf(calculateTeamTotalGoals _)
    val topScorerUdf = udf(getTeamTopScorer _)
    val bestPasserUdf = udf(getTeamBestPasser _)

    teamStatsRawDataset
      .join(teamSlogansDataframe, rawColumnTeamName)
      .withColumnRenamed(rawColumnTeamSlogan, domainColumnSloganName)
      .withColumn(domainColumnTotalGoalsName, totalGoalsUdf(col(rawColumnScorersName)))
      .withColumn(domainColumnTopScorerName, topScorerUdf(col(rawColumnScorersName)))
      .withColumn(domainColumnBestPasserName, bestPasserUdf(col(rawColumnScorersName)))
      .drop(rawColumnScorersName)
      .as(teamStatsEncoder)
  }

  private def calculateTeamTotalGoals(scorers: List[TeamScorerRaw]): Long = {
    scorers.map(_.goals).sum
  }

  private def getTeamTopScorer(scorers: List[TeamScorerRaw]): TeamTopScorerStats = {
    val topScorerRaw: TeamScorerRaw = scorers.maxBy(_.goals)

    TeamTopScorerStats(
      firstName = topScorerRaw.scorerFirstName,
      lastName = topScorerRaw.scorerLastName,
      goals = topScorerRaw.goals,
      games = topScorerRaw.games
    )
  }

  private def getTeamBestPasser(scorers: List[TeamScorerRaw]): TeamBestPasserStats = {
    val bestPasserRaw: TeamScorerRaw = scorers.maxBy(_.goalAssists)

    TeamBestPasserStats(
      firstName = bestPasserRaw.scorerFirstName,
      lastName = bestPasserRaw.scorerLastName,
      goalAssists = bestPasserRaw.goalAssists,
      games = bestPasserRaw.games
    )
  }
}