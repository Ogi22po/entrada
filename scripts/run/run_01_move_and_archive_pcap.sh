#!/usr/bin/env bash

# This bash script is made to move incoming pcap files from S3 to local storage
# and then copy those files onto an archive built in S3.

INPUT_FILTER="*.pcap"
SOURCE="$S3_HOME/input"
OUTPUT_DIR="$DATA_DIR/processing"
PID=$TMP_DIR/moving_pcaps

# copied with modifications from the original scripts:
cleanup(){
  #remove pid file
  if [ -f $PID ];
  then
    rm $PID
  fi
}

echo "[$(date)] : starting movement of pcaps"

if [ -f $PID ];
then
  echo "[$(date)] : $PID  : Process is already running, do not start new process."
  exit 1
fi

#check if tmp dir exists, if not create it
if ! [ -f "$TMP_DIR" ]
then
  mkdir -p $TMP_DIR
fi

#create pid file
echo 1 > $PID

#Make sure cleanup() is called when script is done processing or crashed.
trap cleanup EXIT

#
