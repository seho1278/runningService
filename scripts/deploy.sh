#!/usr/bin/env bash

REPOSITORY=/home/ubuntu
PROJECT_NAME=wadadakBackend

JAR_NAME=$(ls -tr $REPOSITORY/$PROJECT_NAME/ | grep jar | tail -n 1)
echo "> Jar Name: $JAR_NAME"

echo "> directory 이동"
cd $REPOSITORY/$PROJECT_NAME

echo "> 현재 구동 중인 애플리케이션 pid 확인"
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]; then
  echo "현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> 현재 구동 중인 애플리케이션 pid: $CURRENT_PID"
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
  if ps -p $CURRENT_PID > /dev/null
  then
      echo "프로세스가 종료되지 않았습니다."
  fi
fi

echo "> 새 애플리케이션 배포"
nohup java -jar ./$JAR_NAME > output.log 2>&1 &
