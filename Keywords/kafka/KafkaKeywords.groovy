package kafka

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import java.time.Duration
import java.util.Arrays
import java.util.UUID

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

// Keyword classes take connection settings as parameters (passed in from the
// test scripts) instead of reading GlobalVariable directly — Keywords compile
// before Libs/internal/GlobalVariable.groovy in the Eclipse build, so importing
// it here makes the project fail to build.
public class KafkaKeywords {

	private static Properties producerProps(String bootstrapServers) {
		Properties props = new Properties()
		props.put('bootstrap.servers', bootstrapServers)
		props.put('key.serializer', 'org.apache.kafka.common.serialization.StringSerializer')
		props.put('value.serializer', 'org.apache.kafka.common.serialization.StringSerializer')
		return props
	}

	// Unique group id per run + earliest reset: the consumer always reads from
	// the beginning of the topic regardless of previous runs.
	private static Properties consumerProps(String bootstrapServers) {
		Properties props = new Properties()
		props.put('bootstrap.servers', bootstrapServers)
		props.put('group.id', 'katalon-consumer-' + UUID.randomUUID().toString())
		props.put('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
		props.put('value.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
		props.put('auto.offset.reset', 'earliest')
		props.put('enable.auto.commit', 'false')
		return props
	}

	/**
	 * Sends a raw string message to a Kafka topic.
	 */
	@Keyword
	static void sendMessage(String bootstrapServers, String topic, String message) {
		KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps(bootstrapServers))
		try {
			producer.send(new ProducerRecord<String, String>(topic, message))
			producer.flush()
			println "Sent to topic '${topic}': ${message}"
		} finally {
			producer.close()
		}
	}

	/**
	 * Consumes messages from a topic until the timeout expires and returns
	 * everything it saw (as raw strings).
	 */
	@Keyword
	static List<String> consumeMessages(String bootstrapServers, String topic, long timeoutMs) {
		List<String> messages = new ArrayList<String>()
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps(bootstrapServers))
		long deadline = System.currentTimeMillis() + timeoutMs
		try {
			consumer.subscribe(Arrays.asList(topic))
			while (System.currentTimeMillis() < deadline) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500))
				for (ConsumerRecord<String, String> record : records) {
					messages.add(record.value())
				}
			}
		} finally {
			consumer.close()
		}
		println "Consumed ${messages.size()} message(s) from topic '${topic}'"
		return messages
	}

	/**
	 * Consumes from a topic until a message containing `mustContain` is found
	 * (or the timeout expires). Returns the matching message string or null.
	 */
	@Keyword
	static String consumeUntilMatch(String bootstrapServers, String topic, long timeoutMs, String mustContain) {
		String matched = null
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps(bootstrapServers))
		long deadline = System.currentTimeMillis() + timeoutMs
		try {
			consumer.subscribe(Arrays.asList(topic))
			while (matched == null && System.currentTimeMillis() < deadline) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500))
				for (ConsumerRecord<String, String> record : records) {
					if (record.value().contains(mustContain)) {
						matched = record.value()
						break
					}
				}
			}
		} finally {
			consumer.close()
		}
		if (matched != null) {
			println "Found matching message in topic '${topic}'"
		} else {
			println "No message containing '${mustContain}' within ${timeoutMs} ms"
		}
		return matched
	}
}
