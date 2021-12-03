docker pull apachepinot/pinot:latest
docker run -m=8g --memory-swap=24g -p 9000:9000 apachepinot/pinot QuickStart -type batch
