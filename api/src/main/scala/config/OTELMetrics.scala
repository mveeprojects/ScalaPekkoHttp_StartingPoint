package config

import io.opentelemetry.api.metrics.{LongCounter, Meter}
import io.opentelemetry.api.{GlobalOpenTelemetry, OpenTelemetry}

object OTELMetrics {
  lazy val sdk: OpenTelemetry = GlobalOpenTelemetry.get()
  lazy val randomDelayMeter: Meter = sdk.meterBuilder("api").build()
  lazy val randomDelayCounter: LongCounter = randomDelayMeter
    .counterBuilder("random_delay_count")
    .setDescription("Total number of random delay requests")
    .setUnit("1")
    .build()
}
