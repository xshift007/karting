package com.kartingrm.service;

import com.kartingrm.entity.Client;
import com.kartingrm.repository.ClientRepository;
import org.springframework.stereotype.Service;
import java.time.YearMonth;

@Service
public class ClientService {

    private final ClientRepository repo;
    public ClientService(ClientRepository repo){ this.repo = repo; }

    public Client get(Long id){
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no existe"));
    }

    public int getTotalVisitsThisMonth(Client c){
        // Por ahora usamos totalVisits como aproximación
        return c.getTotalVisits();
        // Si quisieras llevar visitas por mes, crea tabla VISIT o agrega campo YearMonth.
    }

    public void incrementVisits(Client c){
        c.setTotalVisits(c.getTotalVisits()+1);
        repo.save(c);
    }
}
