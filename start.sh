#!/bin/bash

sudo service storm-nimbus start
sudo service storm-ui start
sudo service storm-supervisor start

or

storm-0.8.1/bin/storm nimbus &
storm-0.8.1/bin/storm ui &
storm-0.8.1/bin/storm supervisor &

redis-server & 
redis-cli

