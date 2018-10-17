// release
addCommandAlias("fullRelease", ";clean;publishSigned;sonatypeRelease")

// core testing
addCommandAlias(
  "coreTest",
  ";project coreShared;clean;compile;pt:compile;pt:test")

// akka testing
addCommandAlias(
  "akkaTest",
  """
    |;project akkaHttpShared;clean;compile;test:compile;coverage;test
    |;project akkaHttpManagement;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test
    |;project akkaHttpIO;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test
  """.stripMargin)

addCommandAlias("macrosTest", ";project macros;clean;compile;test:compile;coverage;test;pt:compile;coverage;pt:test;coverageReport")
addCommandAlias("asyncTest", ";project asyncHttp;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test;coverageReport")
addCommandAlias("udpTest", ";project udp;clean;compile;it:compile;coverage;it:test;coverageReport")
addCommandAlias("urlTest", ";project urlHttp;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test;coverageReport")
