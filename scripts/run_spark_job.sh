#!/usr/bin/env bash

set -e
set -o pipefail
set -u

echo "#######Running the Spark job with Dataproc Serverless"

gcloud dataproc batches submit spark \
  --project="$PROJECT_ID" \
  --region="$LOCATION" \
  --service-account="$SERVICE_ACCOUNT" \
  --jars="$JAR" \
  --class="$MAIN_CLASS" \
  --history-server-cluster="$HISTORY_SERVER_CLUSTER" \
  -- "$INPUT_TEAM_STATS_FILE_PATH" "$INPUT_TEAM_SLOGANS_FILE_PATH" "$OUTPUT_TEAM_LEAGUE_DATASET" "$OUTPUT_TEAM_STATS_TABLE"
