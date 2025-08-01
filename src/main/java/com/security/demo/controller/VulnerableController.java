package main.java.com.security.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VulnerableController {
    
    private static final Logger logger = LogManager.getLogger(VulnerableController.class);
    
    // VULNERABLE: Log4Shell - CVE-2021-44228
    @PostMapping("/log")
    public String logMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        // VULNERABLE: Direct logging of user input can trigger Log4Shell
        logger.info("User message: {}", message);
        return "Message logged successfully";
    }
    
    // VULNERABLE: Deserialization - CVE-2020-36518
    @PostMapping("/deserialize")
    public String deserializeJson(@RequestBody String jsonData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // VULNERABLE: Unsafe deserialization
            mapper.enableDefaultTyping();
            Object obj = mapper.readValue(jsonData, Object.class);
            return "Deserialized: " + obj.toString();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
    
    // VULNERABLE: SQL Injection
    @GetMapping("/user/{id}")
    public String getUser(@PathVariable String id) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
            Statement stmt = conn.createStatement();
            // VULNERABLE: SQL Injection
            String sql = "SELECT * FROM users WHERE id = " + id;
            stmt.executeQuery(sql);
            return "User data for ID: " + id;
        } catch (Exception e) {
            return "Database error: " + e.getMessage();
        }
    }
    
    // VULNERABLE: Command Injection via Commons Text
    @PostMapping("/template")
    public String processTemplate(@RequestBody Map<String, String> request) {
        String template = request.get("template");
        Map<String, String> values = new HashMap<>();
        values.put("user", "demo");
        
        // VULNERABLE: CVE-2022-42889 - Script injection in Commons Text
        StringSubstitutor substitutor = StringSubstitutor.createInterpolator();
        String result = substitutor.replace(template);
        
        return "Processed template: " + result;
    }
    
    // VULNERABLE: YAML Deserialization
    @PostMapping("/yaml")
    public String parseYaml(@RequestBody String yamlData) {
        try {
            Yaml yaml = new Yaml();
            // VULNERABLE: CVE-2022-1471 - Unsafe YAML deserialization
            Object obj = yaml.load(yamlData);
            return "Parsed YAML: " + obj.toString();
        } catch (Exception e) {
            return "YAML parsing error: " + e.getMessage();
        }
    }
    
    // VULNERABLE: Information Disclosure
    @GetMapping("/debug")
    public Map<String, Object> debugInfo(HttpServletRequest request) {
        Map<String, Object> debugData = new HashMap<>();
        
        // VULNERABLE: Exposing sensitive information
        debugData.put("headers", request.getHeaderNames());
        debugData.put("remoteAddr", request.getRemoteAddr());
        debugData.put("systemProperties", System.getProperties());
        debugData.put("environmentVariables", System.getenv());
        
        return debugData;
    }
    
    // VULNERABLE: Path Traversal
    @GetMapping("/file")
    public String readFile(@RequestParam String filename) {
        try {
            // VULNERABLE: Path traversal attack
            java.nio.file.Path path = java.nio.file.Paths.get("./uploads/" + filename);
            byte[] content = java.nio.file.Files.readAllBytes(path);
            return new String(content);
        } catch (Exception e) {
            return "File read error: " + e.getMessage();
        }
    }
}
