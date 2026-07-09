package kafka

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint

import org.apache.kafka.clients.producer.KafkaProducer
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord


public class TestImport {
	
	static void test() {
		println KafkaProducer.class.getName()
	
		Properties props = new Properties()
		props.put("bootstrap.servers", "localhost:9092")
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
		
		KafkaProducer<String, String> producer = new KafkaProducer<>(props)
		producer.send(new ProducerRecord<>("test-topic", "Hello Kafka"))
		producer.close()
		
		println "Message sent!"
	}
}
