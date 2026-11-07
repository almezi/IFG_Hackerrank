package api

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.RestRequestObjectBuilder
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

public class ApiKeywords {

	/**
	 * Reads the canonical order fixture used across the tests.
	 */
	@Keyword
	static String readOrderFixture() {
		String path = RunConfiguration.getProjectDir() + '/Include/fixtures/order_data.json'
		return new File(path).text
	}

	/**
	 * POSTs an order to the order service.
	 */
	@Keyword
	static ResponseObject createOrder(String requestBody) {
		RequestObject request = new RestRequestObjectBuilder()
				.withRestUrl("${GlobalVariable.BASE_URL}/api/create")
				.withRestRequestMethod('POST')
				.withHttpHeaders([
					new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json')
				])
				.withTextBody(requestBody)
				.build()
		return WS.sendRequest(request)
	}

	/**
	 * GETs an order by id from the order service.
	 */
	@Keyword
	static ResponseObject getOrder(String orderId) {
		RequestObject request = new RestRequestObjectBuilder()
				.withRestUrl("${GlobalVariable.BASE_URL}/api/get-order?id=${orderId}")
				.withRestRequestMethod('GET')
				.build()
		return WS.sendRequest(request)
	}
}
