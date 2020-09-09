setup:
	npm i -g kinesalite

install:
	mvn clean install -Dmaven.test.skip=true

package:
	mvn clean package -Dmaven.test.skip=true

run-gateway:
	mvn -pl gateway quarkus:dev

start-local-kinesis:
	kinesalite
