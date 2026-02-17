package com.manolinho.application.usecase;

import com.manolinho.domain.model.Tienda;
import com.manolinho.domain.repository.TiendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetApplicablePriceUseCaseTest {

    @Mock
    private TiendaRepository tiendaRepository;

    @InjectMocks
    private GetApplicablePriceUseCase getApplicablePriceUseCase;

    @Test
    void deberiaRetornarTarifaAplicable() {
        LocalDateTime fechaAplicacion = LocalDateTime.parse("2026-01-15T10:00:00");
        when(tiendaRepository.findApplicablePrice(fechaAplicacion, 35455L, 1L))
                .thenReturn(Optional.of(new Tienda(
                        9L,
                        1L,
                        LocalDateTime.parse("2026-01-01T00:00:00"),
                        LocalDateTime.parse("2026-06-30T23:59:59"),
                        2,
                        35455L,
                        1,
                        new BigDecimal("30.50"),
                        "EUR"
                )));

        Optional<Tienda> result = getApplicablePriceUseCase.execute(fechaAplicacion, 35455L, 1L);

        assertTrue(result.isPresent());
    }
}
