package org.ncu.mf_loan_system.controller;

import org.ncu.mf_loan_system.entities.Client;
import org.ncu.mf_loan_system.entities.Loan;
import org.ncu.mf_loan_system.service.ClientService;
import org.ncu.mf_loan_system.service.LoanService;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
//@PreAuthorize("isAuthenticated()") // Base security for all endpoints
public class ClientController {

    private final ClientService clientService;
    private final LoanService loanService;

    public ClientController(ClientService clientService, LoanService loanService) {
        this.clientService = clientService;
        this.loanService = loanService;
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'MANAGER', 'STAFF')")
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'MANAGER', 'STAFF')")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping("/{clientId}/loans")
    //    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'MANAGER')")
    public ResponseEntity<List<Loan>> getClientLoans(@PathVariable Long clientId) {
        return ResponseEntity.ok(loanService.getLoansByClientId(clientId));
    }

    @PostMapping
//    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'MANAGER')")
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        return ResponseEntity.status(201).body(clientService.createClient(client));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'MANAGER')")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        return ResponseEntity.ok(clientService.updateClient(id, client));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('MANAGER')") // Only managers can delete clients
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}