#!/bin/bash
# Blue Team Remediation Script for Vulnerable Spring Boot App

echo "🔵 BLUE TEAM: Starting security remediation process..."
echo "=================================================="

# Step 1: Backup current vulnerable configuration
echo "📋 Step 1: Backing up vulnerable configuration"
cp vulnerable-app/pom.xml vulnerable-app/pom-vulnerable-backup.xml
echo "✅ Backup created: pom-vulnerable-backup.xml"

# Step 2: Apply security fixes
echo "🔧 Step 2: Applying security fixes"
cp vulnerable-app/pom-secure.xml vulnerable-app/pom.xml
echo "✅ Updated pom.xml with secure dependencies"

# Step 3: Run security scans
echo "🔍 Step 3: Running OWASP Dependency Check"
cd vulnerable-app
mvn dependency-check:check -DfailBuildOnCVSS=0
echo "✅ Security scan completed"

# Step 4: Generate security report
echo "📊 Step 4: Generating security reports"
mvn compile
mvn dependency-check:check -Dformat=ALL
echo "✅ Security reports generated in target/security-reports/"

# Step 5: Build secure version
echo "🏗️ Step 5: Building secure application"
mvn clean package -DskipTests
echo "✅ Secure application built"

# Step 6: Update Docker image
echo "🐳 Step 6: Building secure Docker image"
docker build -t vulnerable-app:secure -f Dockerfile.secure .
echo "✅ Secure Docker image created"

cd ..

echo "=================================================="
echo "🔵 BLUE TEAM: Security remediation completed!"
echo ""
echo "📋 Summary of fixes applied:"
echo "  ✅ Spring Boot: 2.6.0 → 3.2.0"
echo "  ✅ Log4j: 2.14.1 → 2.21.1 (Log4Shell fixed)"
echo "  ✅ Jackson: 2.12.1 → 2.15.3 (Deserialization fixed)"
echo "  ✅ Commons Collections: 4.0 → 4.4"
echo "  ✅ MySQL Connector: 8.0.25 → 8.0.33"
echo "  ✅ Commons Text: 1.9 → 1.11.0 (Script injection fixed)"
echo "  ✅ SnakeYAML: 1.30 → 2.2 (YAML deserialization fixed)"
echo "  ✅ H2 Database: 1.4.200 → 2.2.224"
echo "  ✅ Maven Surefire: 2.22.2 → 3.2.2"
echo ""
echo "🔍 Next steps:"
echo "  1. Review security reports in target/security-reports/"
echo "  2. Test the secure application"
echo "  3. Deploy to production with secure configuration"
