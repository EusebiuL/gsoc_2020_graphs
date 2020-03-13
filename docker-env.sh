#!/usr/bin/env bash

HBASE_CONTAINER_NAME=hbase-master
JANUS_CONTAINER_NAME=janusgraph-default


mkdir -p ~/data/hbase

if [ "$(docker ps -aq -f name=$HBASE_CONTAINER_NAME)" ]; then
	if [ ! "$(docker ps -aq -f name=$HBASE_CONTAINER_NAME -f status=exited)" ]; then
		echo "Stopping hbase container"
		docker stop $HBASE_CONTAINER_NAME
	fi
	echo "Starting hbase container"
	docker start $HBASE_CONTAINER_NAME
else
docker run -d --name $HBASE_CONTAINER_NAME -h $HBASE_CONTAINER_NAME -p 16010:16010 \
       -v $HOME/data/hbase:/data \
       gelog/hbase hbase master start && \

docker logs -f $HBASE_CONTAINER_NAME
fi

if [ "$(docker ps -aq -f name=$JANUS_CONTAINER_NAME)" ]; then
	if [ ! "$(docker ps -aq -f name=$JANUS_CONTAINER_NAME -f status=exited)" ]; then
		echo "Stopping janusgraph container"
		docker stop $JANUS_CONTAINER_NAME
	fi
	echo "Starting janusgraph container"
	docker start $JANUS_CONTAINER_NAME
else
docker run --name $JANUS_CONTAINER_NAME janusgraph/janusgraph:latest

fi