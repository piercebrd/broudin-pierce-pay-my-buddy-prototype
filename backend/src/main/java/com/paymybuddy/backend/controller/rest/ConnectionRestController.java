package com.paymybuddy.backend.controller.rest;

import com.paymybuddy.backend.entity.Connection;
import com.paymybuddy.backend.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
public class ConnectionRestController {

    @Autowired
    private ConnectionService connectionService;

    @GetMapping
    public List<Connection> getConnections(@RequestBody Connection connection) {
        return connectionService.getConnectionsForUser(connection.getUser());
    }

    @PostMapping
    public String createConnection(@RequestBody Connection connection) {
        if (connectionService.isAlreadyConnected(connection.getUser(), connection.getFriend())) {
            return "Already connected.";
        }
        connectionService.save(connection);
        return "Connection created.";
    }

    @DeleteMapping("/{id}")
    public String deleteConnection(@PathVariable Long id) {
        connectionService.delete(id);
        return "Connection deleted.";
    }
}
