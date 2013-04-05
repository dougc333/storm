#!/bin/bash

storm-0.8.1/bin/storm nimbus &
storm-0.8.1/bin/storm ui &
storm-0.8.1/bin/storm supervisor &


