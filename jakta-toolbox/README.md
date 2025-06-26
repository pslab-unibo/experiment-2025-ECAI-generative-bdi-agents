# JaKtA Toolbox

The `infra` directory provides support for running a centralized logging system, based on the ELK stack (with Opensearch in place of Elastic).

To deploy the single-node cluster in development mode, a docker compose can be used:

```shell
docker compose up
```

Both the Docker Engine and Docker Compose need to be installed.
This setup will build an extended version of Logstash with the plugin to work with OpenSearch.
In addition, it will read the configuration of the instance from the logstash.conf file on the filesystem, it will bind ports 9200 and 4054–4056 and create a volume to store the server data.