package com.kartingrm.service;

import com.kartingrm.entity.Client;
import com.kartingrm.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock ClientRepository repo;
    @InjectMocks ClientService svc;

    private Client c;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        c = new Client(1L, "Nombre", "e@x.com", null,
                LocalDate.of(2000,1,1), 2, LocalDateTime.now());
    }

    @Test
    void getExistente() {
        when(repo.findById(1L)).thenReturn(Optional.of(c));
        assertThat(svc.get(1L)).isSameAs(c);
    }

    @Test
    void getNoExistente_lanza() {
        when(repo.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> svc.get(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cliente no existe");
    }

    @Test
    void incrementVisits_y_getTotalVisitsThisMonth() {
        svc.incrementVisits(c);
        // c.totalVisits era 2, tras increment pasa a 3
        assertThat(c.getTotalVisits()).isEqualTo(3);
        verify(repo).save(c);
        // getTotalVisitsThisMonth delega en c.getTotalVisits()
        assertThat(svc.getTotalVisitsThisMonth(c)).isEqualTo(3);
    }
}
