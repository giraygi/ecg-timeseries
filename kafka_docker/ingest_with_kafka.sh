docker network create --driver bridge pinot-demo
docker run --network pinot-demo --name=kafka -e KAFKA_ZOOKEEPER_CONNECT=localhost:2123/kafka -e KAFKA_BROKER_ID=0 -e KAFKA_ADVERTISED_HOST_NAME=kafka -d wurstmeister/kafka:latest
docker ps --filter "status=exited"
docker start 228d24017c97
docker exec -t kafka /opt/kafka/bin/kafka-topics.sh --zookeeper localhost:2123/kafka --partitions=1 --replication-factor=1 --create --topic ecg-topic



docker run --network=pinot-demo -v /tmp/pinot-quick-start:/tmp/pinot-quick-start --name pinot-streaming-table-creation apachepinot/pinot:latest AddTable -schemaFile /tmp/pinot-quick-start/schema.json -tableConfigFile /tmp/pinot-quick-start/ecg-realtime.json -controllerHost pinot-quickstart -controllerPort 9000 -exec
