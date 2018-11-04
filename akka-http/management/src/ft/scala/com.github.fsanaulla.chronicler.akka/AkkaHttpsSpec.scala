package com.github.fsanaulla.chronicler.akka

import java.io.{File, FileInputStream, InputStream}
import java.security.{KeyStore, SecureRandom}

import akka.actor.ActorSystem
import akka.http.scaladsl.{ConnectionContext, HttpsConnectionContext}
import com.github.fsanaulla.chronicler.akka.management.InfluxMng
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

import scala.concurrent.ExecutionContextExecutor

class AkkaHttpsSpec
  extends TestKit(ActorSystem())
    with FlatSpecWithMatchers
    with ScalaFutures
    with IntegrationPatience {

  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val pass: Array[Char] = "".toCharArray
  val ks: KeyStore = KeyStore.getInstance("PKCS12")
  val keystore: InputStream = new FileInputStream(new File("server.csr"))

  require(keystore != null, "Keystore required!")
  ks.load(keystore, pass)

  val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
  keyManagerFactory.init(ks, pass)

  val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
  tmf.init(ks)

  val sslContext: SSLContext = SSLContext.getInstance("TLS")
  sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)

  "Https" should "work" in {
    val ctx: Option[HttpsConnectionContext] = Some(ConnectionContext.https(sslContext))
    val mng = InfluxMng("localhost", httpsContext = ctx)

    mng.ping.futureValue.isSuccess shouldBe true

    mng.close()
  }
}
