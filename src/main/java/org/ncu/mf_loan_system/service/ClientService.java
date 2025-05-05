package org.ncu.mf_loan_system.service;

import org.ncu.mf_loan_system.entities.Client;
import java.util.List;

public interface ClientService {
    List<Client> getAllClients();
    Client getClientById(Long Id);
    Client createClient(Client client);
    Client updateClient(Long id, Client client);
    void deleteClient(Long id);

}