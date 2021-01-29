# classwork 59

1. Microservice Analyzer
   - service LogsAnalyzerService - collects logs from app-binding-name:exceptions-out-0 and sends it ot StreamBridge
1. LogsAnalyzerTests
   - creates a LogDto with NO_EXCEPTION and sends it, then checks thant consumer is empty
   - creates a LogD witch AUTHENTICATION_EXCEPTION and sends it, then checks thant consumer has message
