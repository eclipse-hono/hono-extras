# Device registry migration

Contains example scripts to migrate from 1 device registry type to another. 

## File based to mongo db
[file2mongo.py](file2mongo.py) is a python script which transforms the data from a file based registry to a mongo db based registry.

> **⚠ WARNING: Use at your own risk.**  
> At the time of writing, this script suits our specific needs. This script must be handle with care and double checked by the one used.

You can follow following guide to move your data:

- Export from file-based registry:

```sh
kubectl cp hono-service-device-registry-0:/var/lib/hono/device-registry .
```

- Transform all collections:

```sh
./file2mongo.py tenants device-registry/tenants.json > tenants-converted.json
./file2mongo.py devices device-registry/devices.json > devices-converted.json
./file2mongo.py credentials device-registry/credentials.json > credentials-converted.json
```

- Import in MongoDB (see https://github.com/txn2/kubefwd)

```sh
sudo -E kubefwd svc
```

```sh
mongoimport --host hono-monogodb-server:27017 -u device-registry@HONO -p hono-secret -d honodb -c tenants --jsonArray --file tenants-converted.json
mongoimport --host hono-monogodb-server:27017 -u device-registry@HONO -p hono-secret -d honodb -c devices --jsonArray --file devices-converted.json
mongoimport --host hono-monogodb-server:27017 -u device-registry@HONO -p hono-secret -d honodb -c credentials --jsonArray --file credentials-converted.json
```
