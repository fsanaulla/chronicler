// release
addCommandAlias("fullRelease", ";clean;publishSigned;sonatypeRelease")

// testing
addCommandAlias("coreTest", ";project coreModel;clean;compile;pt:compile;pt:test")
addCommandAlias("macrosTest", ";project macros;clean;compile;test:compile;coverage;test;pt:compile;coverage;pt:test;coverageReport")
addCommandAlias("asyncTest", ";project asyncHttp;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test;coverageReport")
addCommandAlias("udpTest", ";project udp;clean;compile;it:compile;coverage;it:test;coverageReport")
addCommandAlias("urlTest", ";project urlHttp;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test;coverageReport")
addCommandAlias("akkaTest", ";project akkaHttp;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test;coverageReport")