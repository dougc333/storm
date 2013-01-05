#!/bin/bash

sudo service storm-nimbus start
sudo service storm-ui start
sudo service storm-supervisor start

redis-server & 
redis-cli

