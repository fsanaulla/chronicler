#!/usr/bin/env bash
wget https://dl.influxdata.com/influxdb/releases/influxdb_1.3.2_amd64.deb
sudo dpkg -i influxdb_1.3.2_amd64.deb
sudo service influxdb start
sleep 5
#/usr/bin/influx --execute "CREATE USER influx_user WITH PASSWORD 'influx_password' WITH ALL PRIVILEGES"