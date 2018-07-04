// release
addCommandAlias("fullRelease", ";clean;publishSigned;sonatypeRelease")

// testing
addCommandAlias("coreTest", ";project core;clean;compile;test:compile;test")
addCommandAlias("macrosTest", ";project macros;clean;compile;test:compile;coverage;test")
addCommandAlias("asyncTest", ";project asyncHttp;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test;coverageReport")
addCommandAlias("udpTest", ";project udp;clean;compile;it:compile;coverage;it:test;coverageReport")
addCommandAlias("urlTest", ";project urlHttp;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test;coverageReport")
addCommandAlias("akkaTest", ";project akkaHttp;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test;coverageReport")