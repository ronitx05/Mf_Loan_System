package org.ncu.mf_loan_system.service;

import org.ncu.mf_loan_system.entities.Client;
import org.ncu.mf_loan_system.exception.ResourceNotFoundException;
import org.ncu.mf_loan_system.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Override
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }

    @Override
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Client updateClient(Long id, Client clientDetails) {
        Client client = getClientById(id);
        client.setName(clientDetails.getName());
        client.setEmail(clientDetails.getEmail());
        client.setPhone(clientDetails.getPhone());
        return clientRepository.save(client);
    }

    @Override
    public void deleteClient(Long id) {
        Client client = getClientById(id);
        clientRepository.delete(client);
    }
}