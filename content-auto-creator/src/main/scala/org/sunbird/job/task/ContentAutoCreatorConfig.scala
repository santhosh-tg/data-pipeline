package org.sunbird.job.task

import java.util
import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.job.BaseJobConfig
import org.sunbird.job.contentautocreator.domain.Event

import scala.collection.JavaConverters._

class ContentAutoCreatorConfig(override val config: Config) extends BaseJobConfig(config, "content-auto-creator") {

  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
  implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
  implicit val contentAutoCreatorTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  // Kafka Topics Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  override val kafkaConsumerParallelism: Int = config.getInt("task.consumer.parallelism")
  override val parallelism: Int = config.getInt("task.parallelism")
  val contentAutoCreatorParallelism: Int = if (config.hasPath("task.content-auto-creator.parallelism"))
    config.getInt("task.content-auto-creator.parallelism") else 1

  // Metric List
  val totalEventsCount = "total-events-count"
  val successEventCount = "success-events-count"
  val failedEventCount = "failed-events-count"
  val skippedEventCount = "skipped-events-count"

  // Consumers
  val eventConsumer = "content-auto-creator-consumer"
  val contentAutoCreatorFunction = "content-auto-creator-process"
  val contentAutoCreatorEventProducer = "content-auto-creator-producer"

  // Tags
  val contentAutoCreatorOutputTag: OutputTag[Event] = OutputTag[Event]("content-auto-creator")

  val configVersion = "1.0"

  // DB Config
  val cassandraHost: String = config.getString("lms-cassandra.host")
  val cassandraPort: Int = config.getInt("lms-cassandra.port")
  val graphRoutePath: String = config.getString("neo4j.routePath")
  val graphName: String = config.getString("neo4j.graph")


  // Schema Config
  val definitionBasePath: String = if (config.hasPath("schema.basePath")) config.getString("schema.basePath") else "https://sunbirddev.blob.core.windows.net/sunbird-content-dev/schemas/local"
  val schemaSupportVersionMap: Map[String, AnyRef] = if (config.hasPath("schema.supportedVersion")) config.getObject("schema.supportedVersion").unwrapped().asScala.toMap else Map[String, AnyRef]()

  val cloudProps: List[String] = if (config.hasPath("object.cloud_props")) config.getStringList("object.cloud_props").asScala.toList else List("variants", "downloadUrl", "appIcon", "posterImage", "pdfUrl")
  val overrideManifestProps: List[String] = if (config.hasPath("object.override_manifest_props")) config.getStringList("object.override_manifest_props").asScala.toList else List("variants", "downloadUrl", "previewUrl", "pdfUrl", "lastPublishedBy")
  val contentServiceBaseUrl : String = config.getString("service.content.basePath")
  val sourceBaseUrl: String = config.getString("source.baseUrl")
  val allowedContentStages: List[String] = if (config.hasPath("content_auto_creator.allowed_content_stages")) config.getStringList("content_auto_creator.allowed_content_stages").asScala.toList else List("create", "upload", "review", "publish")
  val allowedContentObjectTypes: List[String] = if (config.hasPath("content_auto_creator.allowed_object_types")) config.getStringList("content_auto_creator.allowed_object_types").asScala.toList else List("Content")
  val mandatoryContentMetadata: List[String] = if (config.hasPath("content_auto_creator.content_mandatory_fields")) config.getStringList("content_auto_creator.content_mandatory_fields").asScala.toList else List.empty

  def getConfig: Config = config
}
