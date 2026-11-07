<?xml version="1.0" encoding="UTF-8"?>
<TestSuiteEntity>
   <description>End-to-end order flow: create order via REST API, verify the Kafka event, read the order back</description>
   <name>TS_Order_Flow_E2E</name>
   <tag></tag>
   <isRerun>false</isRerun>
   <lastRunResult>PENDING</lastRunResult>
   <runMode>Sequential</runMode>
   <rerunFailTestCases>false</rerunFailTestCases>
   <testSuiteGuid>F5EC157F-2291-4865-9F93-E1E8BFB30142</testSuiteGuid>
   <testCaseLink>
      <guid>DEC04BE3-DA50-4612-BD50-492B15C778C5</guid>
      <name>Test Cases/API/TC_CreateOrder_POST</name>
      <isRun>true</isRun>
      <testCaseId>Test Cases/API/TC_CreateOrder_POST</testCaseId>
      <usingDataBindingAtTestSuiteLevel>false</usingDataBindingAtTestSuiteLevel>
      <dataColumns></dataColumns>
      <dataSettings></dataSettings>
   </testCaseLink>
   <testCaseLink>
      <guid>ED91E841-3182-40F2-A00D-EA991030B601</guid>
      <name>Test Cases/Test_Kafka_Listener</name>
      <isRun>true</isRun>
      <testCaseId>Test Cases/Test_Kafka_Listener</testCaseId>
      <usingDataBindingAtTestSuiteLevel>false</usingDataBindingAtTestSuiteLevel>
      <dataColumns></dataColumns>
      <dataSettings></dataSettings>
   </testCaseLink>
   <testCaseLink>
      <guid>806A0645-4EB2-45E2-8831-CC1EB415E64E</guid>
      <name>Test Cases/API/TC_GetOrder_GET</name>
      <isRun>true</isRun>
      <testCaseId>Test Cases/API/TC_GetOrder_GET</testCaseId>
      <usingDataBindingAtTestSuiteLevel>false</usingDataBindingAtTestSuiteLevel>
      <dataColumns></dataColumns>
      <dataSettings></dataSettings>
   </testCaseLink>
</TestSuiteEntity>
