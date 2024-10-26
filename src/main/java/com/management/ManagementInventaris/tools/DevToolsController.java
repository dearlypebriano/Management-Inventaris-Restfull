package com.management.ManagementInventaris.tools;

import com.management.ManagementInventaris.filter.ToxicWordFilter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/devtools")
public class DevToolsController {

    @Autowired
    private DevToolsService devToolsService;

    @GetMapping(path = "/execute", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> executeCommand(@NotNull @RequestParam String command) {
        String filteredCommand = ToxicWordFilter.filterToxic(command.toLowerCase(), new String[]{}, "***");

        String[] parts = filteredCommand.split(" ", 2);
        String action = parts[0];
        Object result = null;

        switch (action.toUpperCase()) {
            case "PING":
            case "P":
            case "TEST":
                result = "PONG";
                break;
            case "WHAT":
                if (parts.length > 1) {
                    if (parts[1].equalsIgnoreCase("DB USED")) {
                        result = devToolsService.getDatabaseUsed();
                    } else if (parts[1].equalsIgnoreCase("FLYWAY STATUS")) {
                        result = devToolsService.checkFlywayStatus();
                    } else if (parts[1].equalsIgnoreCase("CACHE STATUS")) {
                        result = devToolsService.checkCacheStatus();
                    }
                }
                break;
            case "USE":
                if (parts.length > 1) {
                    devToolsService.useDatabase(parts[1]);
                    result = "Database set to " + parts[1];
                }
                break;
            case "GENERATE":
                if (parts.length > 1 && parts[1].equalsIgnoreCase("API KEYS")) {
                    result = devToolsService.generateNewApiKey();
                }
                break;
            case "GET":
                if (parts.length > 1 && parts[1].equalsIgnoreCase("ALL TABLES")) {
                    result = devToolsService.getAllTables();
                } else if (parts.length > 1 && parts[1].equalsIgnoreCase("ALL USERS")) {
                    String[] params = parts[1].split(" ");
                    if (params.length == 4) {
                        try {
                            int page = Integer.parseInt(params[2]);
                            int size = Integer.parseInt(params[3]);
                            result = devToolsService.getAllDataUser(page, size);
                        } catch (NumberFormatException ex) {
                            result = "Invalid page or size format.";
                        }
                    } else {
                        result = "Invalid command format. Use 'GET ALL USERS <page> <size>'";
                    }
                }
                break;
            case "LOCK":
                if (parts.length > 1) {
                    String[] lockParts = parts[1].split(" ", 2);
                    if (lockParts.length == 2 && lockParts[0].equalsIgnoreCase("ID")) {
                        try {
                            String userId = String.valueOf(Integer.parseInt(lockParts[1]));
                            result = devToolsService.lockAccount(userId, null);
                        } catch (NumberFormatException e) {
                            result = "Invalid user ID format.";
                        }
                    } else if (lockParts.length == 2 && lockParts[0].equalsIgnoreCase("EMAIL")) {
                        result = devToolsService.lockAccount(null, lockParts[1]);
                    } else {
                        result = "Invalid command format. Use 'LOCK ID <userId>' or 'LOCK EMAIL <email>'";
                    }
                }
                break;
            case "UNLOCK":
                if (parts.length > 1) {
                    String[] lockParts = parts[1].split(" ", 2);
                    if (lockParts.length == 2 && lockParts[0].equalsIgnoreCase("ID")) {
                        try {
                            Integer userId = Integer.parseInt(lockParts[1]);
                            result = devToolsService.unlockAccount(String.valueOf(userId), null);
                        } catch (NumberFormatException e) {
                            result = "Invalid user ID format.";
                        }
                    } else if (lockParts.length == 2 && lockParts[0].equalsIgnoreCase("EMAIL")) {
                        result = devToolsService.unlockAccount(null, lockParts[1]);
                    } else {
                        result = "Invalid command format. Use 'LOCK ID <userId>' or 'LOCK EMAIL <email>'";
                    }
                }
                break;
            default:
                result = "Unknown command";
                break;
        }
        if (Objects.equals(result, "Unknown command")) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        return ResponseEntity.ok(result);
    }
}