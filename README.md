# ecg-timeseries Project

This repo intends to build a platform independent pipeline for classifying the time-series data of https://zenodo.org/record/5711347#.YaqbvJHMLmF . The pipeline steps such as Data Conversion, Development Environment and Data Ingestion to Time Series Databases are explained below:

## Data Conversion

### ConvertTimeSeriesData:
The java project ConvertTimeSeriesData includes several methods that may be used in combination to convert input data to the desired csv and json formats for data ingestion. In addition, the class includes a schema configuration generator (required by Pinot) and a data partition method to enable ingesting data in segments for performance. 

The columns in the original data source are actually time points and patient attributes. This input needs to be transposed to yield a data source to represent the time points in a time series column. It is known that the whole sampling has been performed for 10 seconds with 1000 time points in each ECG. For this reason, the respective methods in ConvertTimeSeriesData generate the Time Series column (in alternative formats) by incrementing 10 milli seconds for the 1000 rows. The remaining dimensional columns are appended to this Time Series column via these conversion methods.     

## Development Environment (Notebook)

### JupyterLab:
The jupyterlab directory includes a run.sh script that pulls a jupyterlab image, sets an application password "password123", clones the ecg notebook (https://github.com/spdrnl/ecg/blob/master/ECG.ipynb) into the notebooks directory of the container (alternative method is possible from git with GIT_URL parameter), installs pip requirements in requirements.txt and publishes the service on port 8889 . The port number has been changed for preventing collision with the druid service that is published on 8888

The original docker image was retrieved from https://hub.docker.com/r/amalic/jupyterlab

## Time Series Databases and Data Import

### Pinot:
The pinot_docker directory includes run.sh script that deploys a pinot service published on the 9000 port of localhost. The memory and memory-swap options of the container can be configured for better performance as seen in the second line of the script. schema.json (generated with ConvertTimeSeriesData) and table.json (OFFLINE) files in the directory are used to define a table and its respective schema by either swagger documentation or equivalent curl methods of the pinot service. Then the partitioned data (by ConvertTimeSeriesData methods) can be ingested piece by piece with the following command:

curl -X POST -F file=@pinot1.csv -H "Content-Type: multipart/form-data" "http://localhost:9000/ingestFromFile?tableNameWithType=ecg_OFFLINE&batchConfigMapStr=%7B%22inputFormat%22%3A%22csv%22%2C%22recordReader.prop.delimiter%22%3A%22%2C%22%7D"

Additionally, the batch-job-spec.yaml in the directory can be used for batch ingestion with previously partitioned data. It is needed to execute the spec from inside the container with the following command:

bin/pinot-admin.sh LaunchDataIngestionJob -jobSpecFile /tmp/pinot-quick-start/batch-job-spec.yaml Both of the OFFLINE batch approaches are explained further in https://docs.pinot.apache.org/basics/data-import/batch-ingestion

The container can be accessed with _docker exec -it container_name_or_id sh_ 

More information about batch ingestion methods is given here: https://docs.pinot.apache.org/basics/data-import/batch-ingestion 

In addition to the batch ingestion method, pinot provides stream ingestion methods. The ingest_with_kafka.sh script defines a kafka based stream ingestion procedure using the real time table configuration in ecg-realtime.json : More information about this procedure can be found at

https://docs.pinot.apache.org/basics/data-import/pinot-stream-ingestion/import-from-apache-kafka


### Druid:
 Druid database is enabled by _docker-compose up_ command in druid directory. The docker compose file uses an additional environment file in which the number of data processing threads are configured as (NO OF CORES IN THE HOST PLATFORM) -1 along with memory requirements of druid. It is further possible to enable additional extensions such as the "druid-kafka-indexing-service" enabled in this configuration.  
Druid accepts CSV and JSON formats locally and it has several more options of data ingestion. Druid enables to reindex local data sources by adding new partitions or columns so partitioned local data can be used in druid. The removedata.sh and senddata.sh scripts are used to send and remove data sources to all the 5 druid components (middlemanager, router, historical, broker, coordinator) 


### MongoDB: 
MongoDB has a timeseries feature as of MongoDB 5.0 and it is present as a docker image @ docker.io/bitnami/mongodb:5.0 that is activated with _docker-compose up_ command.
MongoDB 5.0 version  requires a Bson UTC DateTime column in a data source to be able to use the time series feature. The ConvertTimeSeriesData class includes methods that generate input with all required dimensions along with a UTC Date Time value. JSON or CSV format inputs can be used to add a time series collection in the MongoDB container. The container can be accessed from MongoDB Compass Application interface for convenience. 

### Docker: 
clean_docker_images.sh and clean_docker_containers.sh scripts can be used for stopping and removing all containers and images during any configuration.



## REFERENCES:
[1]	N. Strodthoff, P. Wagner, T. Schaeffter, and W. Samek, “Deep Learning for ECG Analysis: Benchmarks and Insights from PTB-XL,” IEEE J. Biomed. Health Inform., vol. 25, no. 5, pp. 1519–1528, May 2021, doi: 10.1109/JBHI.2020.3022989.

[2]	S. Aziz, S. Ahmed, and M.-S. Alouini, “ECG-based machine-learning algorithms for heartbeat classification,” Sci Rep, vol. 11, no. 1, p. 18738, Sep. 2021, doi: 10.1038/s41598-021-97118-5.

[3]	P. Wagner et al., “PTB-XL, a large publicly available electrocardiography dataset,” Sci Data, vol. 7, no. 1, p. 154, May 2020, doi: 10.1038/s41597-020-0495-6. 
