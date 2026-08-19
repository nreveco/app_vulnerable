package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import javax.servlet.http.HttpServletResponse;

@Controller
public class PageController {
    
    // XSS Fix: Proper output encoding
    @GetMapping("/profile")
    public String showProfile(@RequestParam String username, Model model, HttpServletResponse response) {
        // XSS Fix: HTML escape user input before rendering
        String safeUsername = HtmlUtils.htmlEscape(username);
        model.addAttribute("username", safeUsername);
        
        // XSS Fix: Set security headers
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        
        return "profile";
    }
    
    // XSS Fix: JSON response with proper content type
    @GetMapping("/api/user-data")
    @ResponseBody
    public String getUserData(@RequestParam String query, HttpServletResponse response) {
        // XSS Fix: Set proper content type for JSON
        response.setContentType("application/json; charset=UTF-8");
        
        // XSS Fix: Escape JSON data
        String safeQuery = query.replaceAll("[\"\\]", "\\$0");
        return "{\"query\": \"" + safeQuery + "\", \"status\": \"success\"}";
    }
    
    // XSS Fix: Input validation and sanitization
    @PostMapping("/comment")
    public String submitComment(@RequestParam String comment, Model model) {
        // XSS Fix: Validate and sanitize comment
        if (comment == null || comment.trim().isEmpty()) {
            model.addAttribute("error", "Comment cannot be empty");
            return "error";
        }
        
        // XSS Fix: Remove potentially dangerous HTML tags
        String sanitizedComment = sanitizeHtml(comment);
        String safeComment = HtmlUtils.htmlEscape(sanitizedComment);
        
        model.addAttribute("comment", safeComment);
        return "comment-success";
    }
    
    private String sanitizeHtml(String input) {
        // Remove script tags and other dangerous elements
        return input.replaceAll("(?i)<script[^>]*>.*?</script>", "")
                   .replaceAll("(?i)<iframe[^>]*>.*?</iframe>", "")
                   .replaceAll("(?i)<object[^>]*>.*?</object>", "")
                   .replaceAll("(?i)<embed[^>]*>.*?</embed>", "")
                   .replaceAll("(?i)javascript:", "")
                   .replaceAll("(?i)vbscript:", "")
                   .replaceAll("(?i)onload=", "")
                   .replaceAll("(?i)onerror=", "");
    }
}