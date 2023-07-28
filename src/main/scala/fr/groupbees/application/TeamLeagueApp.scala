package fr.groupbees.application

import fr.groupbees.TeamStatsDatasetMapper
import fr.groupbees.domain.{TeamStats, TeamStatsRaw}
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.{Dataset, SparkSession}

object TeamLeagueApp {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("Team League Application")
      .getOrCreate()

    val inputTeamStatsFilePath = args(0)
    val inputTeamSlogansFilePath = args(1)
    val outputTeamLeagueDataset = args(2)
    val outputTeamStatsTable = args(3)

    val teamStatsRawEncoder = org.apache.spark.sql.Encoders.product[TeamStatsRaw]
    val teamStatsRawDataset = spark.read.json(inputTeamStatsFilePath).as(teamStatsRawEncoder)

    val teamSlogansDataframe = spark
      .read
      .json(inputTeamSlogansFilePath)

    val teamStatsDataset: Dataset[TeamStats] = TeamStatsDatasetMapper.toTeamStatsDataset(
      teamStatsRawDataset = teamStatsRawDataset,
      teamSlogansDataframe = teamSlogansDataframe
    )

    teamStatsDataset
      .withColumn("ingestionDate", current_timestamp())
      .write
      .format("bigquery")
      .option("writeMethod", "direct")
      .option("createDisposition", "CREATE_NEVER")
      .mode("append")
      .save(s"$outputTeamLeagueDataset.$outputTeamStatsTable")

    spark.stop()
  }
}