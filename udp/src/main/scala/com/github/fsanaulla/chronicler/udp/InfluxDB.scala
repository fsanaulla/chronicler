package com.github.fsanaulla.chronicler.udp

import scala.concurrent.ExecutionContext

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 14.03.18
  */
object InfluxDB {

  /***
    * Constructor for creating UDP client
    * @param host - InfluxDB host value
    * @param port - InfluxDB port value
    * @param ex - Execution context
    * @return - InfluxUDPClient
    */
  def connect(host: String = "localhost", port: Int = 8089)(implicit ex: ExecutionContext): InfluxUdpClient =
    new InfluxUdpClient(host, port)

}
