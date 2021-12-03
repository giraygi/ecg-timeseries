curl -X POST -F file=@pinot1.csv -H "Content-Type: multipart/form-data" "http://localhost:9000/ingestFromFile?tableNameWithType=ecg_OFFLINE&batchConfigMapStr=%7B%22inputFormat%22%3A%22csv%22%2C%22recordReader.prop.delimiter%22%3A%22%2C%22%7D"

curl -X POST -F file=@pinot1.json -H "Content-Type: multipart/form-data" "http://localhost:9000/ingestFromFile?tableNameWithType=ecg_OFFLINE&batchConfigMapStr=%7B%22inputFormat%22%3A%22json%22%2C%22recordReader.prop.delimiter%22%3A%22%2C%22%7D"







