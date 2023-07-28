# teams-league-spark-scala-dataproc-serverless

This project shows a complete, concrete and a real world use case with a Spark Scala job run with Dataproc Serverless
on Google Cloud.

![spark_scala_job_dataproc_serverless.png](diagram%2Fspark_scala_job_dataproc_serverless.png)

## Set env vars in your Shell

```shell
# Common
export PROJECT_ID={{project}}
export LOCATION={{location}}

# Deploy Spark job
export LOCAL_JAR_PATH=target/scala-2.13/teams-league-spark-scala-assembly-0.1.0-SNAPSHOT.jar
export GCS_JARS_PATH=gs://mazlum_dev/spark/jars

# Run Spark job
export SERVICE_ACCOUNT={{your_service_account_email}}
export JAR="gs://mazlum_dev/spark/jars/teams-league-spark-scala-assembly-0.1.0-SNAPSHOT.jar"
export MAIN_CLASS=fr.groupbees.application.TeamLeagueApp
export HISTORY_SERVER_CLUSTER=projects/gb-poc-373711/regions/europe-west1/clusters/gb-spark-job-history
export INPUT_TEAM_STATS_FILE_PATH="gs://mazlum_dev/hot/etl/spark/input_teams_stats_raw.json"
export INPUT_TEAM_SLOGANS_FILE_PATH="gs://mazlum_dev/hot/etl/spark/input_team_slogans.json"
export OUTPUT_TEAM_LEAGUE_DATASET="mazlum_test"
export OUTPUT_TEAM_STATS_TABLE="team_stat"
```

## Enable private access subnet

Dataproc Serverless requires Google Private Access to be enabled in the regional subnet where you run your Spark workloads since Spark drivers and executors require private IP addresses.

Enable Google Private Access on the default subnet in your selected region.

```bash
gcloud compute networks subnets \
  update default \
  --region=europe-west1 \
  --enable-private-ip-google-access
```

Verify that Google Private Access is enabled. The output should be True.

```bash
gcloud compute networks subnets \
    describe default \
    --region=europe-west1 \
    --format="get(privateIpGoogleAccess)"
```

Create a Persistent History Server

The Spark UI provides insights into Spark batch workloads. You can create a single-node Dataproc persistent history server that hosts the Spark UI and provides access to the history of completed Dataproc Serverless workloads.

Set a name for your persistent history server.

```bash
PHS_CLUSTER_NAME=gb-spark-job-history
```

```bash
gcloud dataproc clusters create \
    ${PHS_CLUSTER_NAME} \
    --region=europe-west1 \
    --single-node \
    --enable-component-gateway \
    --properties=spark:spark.history.fs.logDirectory='gs://mazlum_dev/phs/*/spark-job-history'
```

## Generate the fat jar with sbt and the assembly plugin

```bash
sbt assembly
```

## Copy the fat jar in the bucket

```bash
gcloud alpha storage cp teams-league-spark-scala-assembly-0.1.0-SNAPSHOT.jar gs://mazlum_dev/spark/jars
```

## Run the Spark job locally :

```bash
gcloud dataproc batches submit spark \
    --project=gb-poc-373711 \
    --region=europe-west1 \
    --service-account=sa-dataproc-serverless-dev@gb-poc-373711.iam.gserviceaccount.com \
    --jars="gs://mazlum_dev/spark/jars/teams-league-spark-scala-assembly-0.1.0-SNAPSHOT.jar" \
    --class="fr.groupbees.application.TeamLeagueApp" \
    --history-server-cluster=projects/gb-poc-373711/regions/europe-west1/clusters/gb-spark-job-history \
    -- gs://mazlum_dev/hot/etl/spark/input_teams_stats_raw.json gs://mazlum_dev/hot/etl/spark/input_team_slogans.json mazlum_test team_stat 
```

## Deploy the Spark job with Cloud Build from local machine

```shell
gcloud builds submit \
    --project=$PROJECT_ID \
    --region=$LOCATION \
    --config deploy-spark-job.yaml \
    --substitutions _LOCAL_JAR_PATH="$LOCAL_JAR_PATH",_GCS_JARS_PATH="$GCS_JARS_PATH" \
    --verbosity="debug" .
```

## Run the Spark job with Cloud Build from local machine

```shell
gcloud builds submit \
    --project=$PROJECT_ID \
    --region=$LOCATION \
    --config run-spark-job.yaml \
    --substitutions _SERVICE_ACCOUNT="$SERVICE_ACCOUNT",_JAR="$JAR",_MAIN_CLASS="$MAIN_CLASS",_HISTORY_SERVER_CLUSTER="$HISTORY_SERVER_CLUSTER",_INPUT_TEAM_STATS_FILE_PATH="$INPUT_TEAM_STATS_FILE_PATH",_INPUT_TEAM_SLOGANS_FILE_PATH="$INPUT_TEAM_SLOGANS_FILE_PATH",_OUTPUT_TEAM_LEAGUE_DATASET="$OUTPUT_TEAM_LEAGUE_DATASET",_OUTPUT_TEAM_STATS_TABLE="$OUTPUT_TEAM_STATS_TABLE" \
    --verbosity="debug" .
```

## Deploy the Spark job with Cloud Build manual trigger on Github repository

```bash
gcloud beta builds triggers create manual \
    --project=$PROJECT_ID \
    --region=$LOCATION \
    --name="deploy-spark-scala-job" \
    --repo="https://github.com/tosun-si/teams-league-spark-scala-dataproc-serverless" \
    --repo-type="GITHUB" \
    --branch="main" \
    --build-config="deploy-spark-job.yaml" \
    --substitutions _LOCAL_JAR_PATH="$LOCAL_JAR_PATH",_GCS_JARS_PATH="$GCS_JARS_PATH" \
    --verbosity="debug"
```

## Run the Spark job with Cloud Build manual trigger on Github repository

```bash
gcloud beta builds triggers create manual \
    --project=$PROJECT_ID \
    --region=$LOCATION \
    --name="run-spark-scala-job" \
    --repo="https://github.com/tosun-si/teams-league-spark-scala-dataproc-serverless" \
    --repo-type="GITHUB" \
    --branch="main" \
    --build-config="run-spark-job.yaml" \
    --substitutions _SERVICE_ACCOUNT="$SERVICE_ACCOUNT",_JAR="$JAR",_MAIN_CLASS="$MAIN_CLASS",_HISTORY_SERVER_CLUSTER="$HISTORY_SERVER_CLUSTER",_INPUT_TEAM_STATS_FILE_PATH="$INPUT_TEAM_STATS_FILE_PATH",_INPUT_TEAM_SLOGANS_FILE_PATH="$INPUT_TEAM_SLOGANS_FILE_PATH",_OUTPUT_TEAM_LEAGUE_DATASET="$OUTPUT_TEAM_LEAGUE_DATASET",_OUTPUT_TEAM_STATS_TABLE="$OUTPUT_TEAM_STATS_TABLE" \
    --verbosity="debug"
```