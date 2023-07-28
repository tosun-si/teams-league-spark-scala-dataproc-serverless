package fr.groupbees

import fr.groupbees.domain.TeamStatsRaw
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class TeamStatsDatasetMapperTest extends AnyFlatSpec with should.Matchers {

  "Given input Team Stats Raw file When Transform it to Team Stats Domain Dataset" should "be equals to the expected file" in {
    val spark = SparkSession
      .builder
      .appName("Team League Application")
      .master("local[*]")
      .getOrCreate()

    val teamStatsRawEncoder = org.apache.spark.sql.Encoders.product[TeamStatsRaw]

    val teamStatsRawDataset: Dataset[TeamStatsRaw] = spark
      .read
      .json("src/test/resources/files/input_teams_stats_raw.json")
      .as(teamStatsRawEncoder)

    val teamSlogansDataframe = spark
      .read
      .json("src/test/resources/files/input_team_slogans.json")

    val teamStatsDataset: Dataset[Row] = TeamStatsDatasetMapper.toTeamStatsDataset(
      teamStatsRawDataset = teamStatsRawDataset,
      teamSlogansDataframe = teamSlogansDataframe,
    ).withColumn("ingestionDate", current_timestamp())

    teamStatsDataset.show()
    teamStatsDataset.printSchema()
  }
}
