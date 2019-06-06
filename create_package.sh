#!/usr/bin/env bash

VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | egrep -v '^\[|Downloading:' | tr -d ' \n')
BASE_DIR="entrada-$VERSION"
echo "Create ENTRADA installation package for version $VERSION"

#make 100% sure the package dir does not exist
rm -rf $BASE_DIR
mkdir $BASE_DIR

cp ./target/entrada-$VERSION-jar-with-dependencies.jar $BASE_DIR
cp -R ./scripts $BASE_DIR
cp -R ./grafana-dashboard $BASE_DIR
cp ./UPGRADE $BASE_DIR
cd $BASE_DIR
ln -s entrada-$VERSION-jar-with-dependencies.jar entrada-latest.jar
cd ..

tar -zcvf "$BASE_DIR.tar.gz" $BASE_DIR
rm -rf $BASE_DIR
