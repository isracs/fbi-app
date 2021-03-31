
##Handy copy&paste commands

#####Run docker & execute the bash terminal
````
docker run -d -p 27017-27019:27017-27019 --name mongo-fbi mongo
docker cp fbi_records.json mongo-fbi:/fbi_records.json
docker exec -it mongo-fbi bash
````
#####Import the data and execute mongo
````
mongoimport --db mobstersdb --collection mobsters --drop --file /fbi_records.json
mongo
````

#####Test if everything seems fine
````
use mobstersdb
db.mobsters.find({nickname: "Fat"})
````