package com.github.fsanaulla.chronicler.udp

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 14.03.18
  */
object Influx {

  /***
    * Constructor for creating UDP client
    * @param host - InfluxDB host value
    * @param port - InfluxDB port value
    * @return - InfluxUDPClient
    */
  def apply(host: String, port: Int = 8089): InfluxUDPClient =
    new InfluxUDPClient(host, port)
}
