# Creating Instances for a Replica Set
Run the following commands in the shell:
```
mkdir -p /_mongodb_data/rs1 /_mongodb_data/rs2 /_mongodb_data/rs3 /_mongodb_logs

mongod --replSet example-replica-set --logpath /_mongodb_logs/rs1.log --dbpath /_mongodb_data/rs1 --port 27017 --oplogSize 64 --smallfiles --fork
mongod --replSet example-replica-set --logpath /_mongodb_logs/rs2.log --dbpath /_mongodb_data/rs2 --port 27018 --oplogSize 64 --smallfiles --fork
mongod --replSet example-replica-set --logpath /_mongodb_logs/rs3.log --dbpath /_mongodb_data/rs3 --port 27019 --oplogSize 64 --smallfiles --fork
```

## On Windows OS

```
mkdir M:\_mongodb_data\rs1 M:\_mongodb_data\rs2 M:\_mongodb_data\rs3 M:\_mongodb_logs
```

Since `mongod` Windows version doesn't support `--fork`, you'll need to open three console windows and run the below commands one by one:
```
mongod --replSet example-replica-set --logpath M:\_mongodb_logs\rs1.log --dbpath M:\_mongodb_data\rs1 --port 27017 --oplogSize 64 --smallfiles
```
```
mongod --replSet example-replica-set --logpath M:\_mongodb_logs\rs2.log --dbpath M:\_mongodb_data\rs2 --port 27018 --oplogSize 64 --smallfiles
```
```
mongod --replSet example-replica-set --logpath M:\_mongodb_logs\rs3.log --dbpath M:\_mongodb_data\rs3 --port 27019 --oplogSize 64 --smallfiles
```

# Configuring and Initialising the Replica Set

```
mongo --port 27017 < initialise-replica-set.js
```