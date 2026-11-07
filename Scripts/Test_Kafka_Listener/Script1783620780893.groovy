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
import groovy.json.JsonSlurper
import kafka.KafkaKeywords

// 1. Create the order via the API — the service publishes the order event
String requestBody = ApiKeywords.readOrderFixture()
def expected = new JsonSlurper().parseText(requestBody)

def response = ApiKeywords.createOrder(requestBody)
WS.verifyResponseStatusCode(response, 200)

// 2. Consume the order event from the Kafka topic
String event = KafkaKeywords.consumeUntilMatch(GlobalVariable.ORDER_TOPIC, 15000, expected.order_id as String)

assert event != null : "No order event for ${expected.order_id} found in topic ${GlobalVariable.ORDER_TOPIC}"

// 3. The event content must match the created order
def orderEvent = new JsonSlurper().parseText(event)
WS.verifyEqual(orderEvent.order_id, expected.order_id)
WS.verifyEqual(orderEvent.customer_name, expected.customer_name)
WS.verifyEqual(orderEvent.total_price, expected.total_price)
WS.verifyEqual(orderEvent.status, expected.status)
