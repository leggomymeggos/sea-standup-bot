### Generating keys

To generate concourse keys, run `./generate-keys --use-pem`

`./generate-keys` was taken from the [concourse-docker github](https://github.com/concourse/concourse-docker/blob/master/generate-keys.sh)

### Starting concourse
Run `docker-compose up -d` to start concourse in the background. Check out [localhost:8082](http://localhost:8082) once it's started
to make sure it's running properly.

### Set pipelines
```
fly -t main set-pipeline -p standup-selector-prod -c pipeline-prod.yml -l secrets.yml
```
in the terminal

### Configuring concourse
There's an excellent [tutorial by Stark & Wayne](https://concoursetutorial.com/) that should get you up and running 