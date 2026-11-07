import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

import api.ApiKeywords
import com.kms.katalon.core.util.KeywordUtil
import kafka.KafkaKeywords

// Kafka roundtrip: produce the fixture payload, then consume it back.
// The full pretty-printed fixture string is used as the match needle so the
// consumer can only land on the message this test produced (the order service
// publishes compact JSON, which can never contain it).
String payload = ApiKeywords.readOrderFixture()

KafkaKeywords.sendMessage(GlobalVariable.KAFKA_BOOTSTRAP_SERVERS, GlobalVariable.ORDER_TOPIC, payload)

String received = KafkaKeywords.consumeUntilMatch(GlobalVariable.KAFKA_BOOTSTRAP_SERVERS, GlobalVariable.ORDER_TOPIC, 15000, payload)

assert received != null : "No message matching the produced payload found in topic ${GlobalVariable.ORDER_TOPIC}"

WS.verifyEqual(received, payload)
KeywordUtil.markPassed('Kafka produce/consume roundtrip verified')
