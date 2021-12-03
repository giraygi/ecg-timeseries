sudo docker stop $(sudo docker ps -aq)
yes | sudo docker system prune -a
yes | sudo docker volume prune
