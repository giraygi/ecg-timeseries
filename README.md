# ecg-timeseries


ConvertTimeSeriesData:
The java project ConvertTimeSeriesData includes several methods that may be used in combination to convert input data to the desired csv and json formats for data ingestion. In addition, the class includes a schema configuration generator (required by Pinot) and a data partition method to enable ingesting data in segments for performance. 

The columns in https://zenodo.org/record/5711347#.YaqbvJHMLmF are actually time points and patient attributes. This input needs to be transposed to yield a data source to represent the time points as columns. It is known that the whole sampling has been performed for 10 seconds with 1000 time points in each ECG. For this reason, the respective methods in ConvertTimeSeriesData generate the Time Series column by incrementing 10 milli seconds for the 1000 rows. The remaining dimensional columns are appended to this Time Series column     


JupyterLab:
The jupyterlab directory includes a run.sh script that pulls a jupyterlab image, sets an application password "password123", clones the ecg notebook into the notebooks directory of the container, installs pip requirements in requirements.txt and publishes the service on port 8889 . The port number has been changed for preventing collision with the druid service that is published on 8888



Pinot:
The pinot_docker directory includes run.sh script that deploys a pinot service published on the 9000 port of localhost. schema.json (generated with ConvertTimeSeriesData) and table.json (OFFLINE) files are used to define a table and its respective schema by either swagger documentation or equivalent curl methods. Then the partitioned data (by ConvertTimeSeriesData methods) can be ingested piece by piece iwith the following command:

curl -X POST -F file=@pinot1.csv -H "Content-Type: multipart/form-data" "http://localhost:9000/ingestFromFile?tableNameWithType=ecg_OFFLINE&batchConfigMapStr=%7B%22inputFormat%22%3A%22csv%22%2C%22recordReader.prop.delimiter%22%3A%22%2C%22%7D"



https://docs.pinot.apache.org/basics/data-import/batch-ingestion
https://docs.pinot.apache.org/basics/data-import/pinot-stream-ingestion/import-from-apache-kafka


Druid:


MongoDB: 
MongoDB has a timeseries feature as of MongoDB 5.0 and it is present as a docker image @ docker.io/bitnami/mongodb:5.0 
This version  requires a Bson UTC DateTime column in a data source to be able to use the time series feature. The ConvertTimeSeriesData class includes methods that generate input with all required dimensions along with a UTC Date Time value. Although several different UTC Date Time   
