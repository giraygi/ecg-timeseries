docker pull amalic/jupyterlab:latest
docker run --rm -it -p 8889:8888 -v $(pwd):/notebooks -e PASSWORD="password123" -e GIT_URL="https://github.com/spdrnl/ecg/" amalic/jupyterlab:latest
