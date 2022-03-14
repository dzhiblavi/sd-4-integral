# Building app and docker image

```console
mvn -am -pl stock package
```

# Running integration tests with docker

```console
mvn -am -pl client test
```

# Example scripts

* Run both server and client.
* Run `./prepare-stock.sh` to fill market with companies and stocks.
* Run `./client-example.sh` to run example.
