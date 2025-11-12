package com.FabricaEscuela.Feature1Back.service;

import com.FabricaEscuela.Feature1Back.DTO.CrearTurnoRequest;
import com.FabricaEscuela.Feature1Back.DTO.TurnoDTO;
import com.FabricaEscuela.Feature1Back.entity.*;
import com.FabricaEscuela.Feature1Back.mapper.TurnoMapper;
import com.FabricaEscuela.Feature1Back.repository.AsignacionTurnoRepository;
import com.FabricaEscuela.Feature1Back.repository.RutaRepository;
import com.FabricaEscuela.Feature1Back.repository.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para TurnoService
 * Patrón AAA: Arrange (preparar), Act (actuar), Assert (verificar)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TurnoService - Pruebas Unitarias")
class TurnoServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private RutaRepository rutaRepository;

    @Mock
    private AsignacionTurnoRepository asignacionTurnoRepository;

    @Mock
    private TurnoMapper turnoMapper;

    @InjectMocks
    private TurnoService turnoService;

    private Turno turnoMock;
    private TurnoDTO turnoDTOMock;
    private Ruta rutaMock;
    private CrearTurnoRequest crearTurnoRequest;

    @BeforeEach
    void setUp() {
        // Arrange común
        rutaMock = new Ruta();
        rutaMock.setId(1L);
        rutaMock.setNombre("Ruta Norte 105");

        turnoMock = new Turno();
        turnoMock.setId(1L);
        turnoMock.setRuta(rutaMock);
        turnoMock.setDiaSemana(DayOfWeek.MONDAY);
        turnoMock.setHoraInicio(LocalTime.of(6, 0));
        turnoMock.setHoraFin(LocalTime.of(13, 30));
        turnoMock.setDuracionHoras(7);
        turnoMock.setNumeroSemana(1);
        turnoMock.setEstado(EstadoTurno.ACTIVO);

        turnoDTOMock = new TurnoDTO();
        turnoDTOMock.setId(1L);
        turnoDTOMock.setRutaId(1L);
        turnoDTOMock.setDiaSemana(DayOfWeek.MONDAY);
        turnoDTOMock.setHoraInicio(LocalTime.of(6, 0));
        turnoDTOMock.setHoraFin(LocalTime.of(13, 30));
        turnoDTOMock.setDuracionHoras(7);
        turnoDTOMock.setNumeroSemana(1);
        turnoDTOMock.setEstado(EstadoTurno.ACTIVO);

        crearTurnoRequest = new CrearTurnoRequest();
        crearTurnoRequest.setRutaId(1L);
        crearTurnoRequest.setDiaSemana(DayOfWeek.MONDAY);
        crearTurnoRequest.setHoraInicio(LocalTime.of(6, 0));
        crearTurnoRequest.setHoraFin(LocalTime.of(13, 30));
        crearTurnoRequest.setNumeroSemana(1);
    }

    @Test
    @DisplayName("Crear turno - Exitoso")
    void testCrearTurno_Exitoso() {
        // Arrange
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaMock));
        when(turnoRepository.save(any(Turno.class))).thenReturn(turnoMock);
        when(turnoMapper.toDTO(turnoMock)).thenReturn(turnoDTOMock);

        // Act
        TurnoDTO resultado = turnoService.crearTurno(crearTurnoRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals(DayOfWeek.MONDAY, resultado.getDiaSemana());
        assertEquals(7, resultado.getDuracionHoras());
        verify(rutaRepository, times(1)).findById(1L);
        verify(turnoRepository, times(1)).save(any(Turno.class));
        verify(turnoMapper, times(1)).toDTO(turnoMock);
    }

    @Test
    @DisplayName("Crear turno - Ruta no encontrada")
    void testCrearTurno_RutaNoEncontrada() {
        // Arrange
        when(rutaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            turnoService.crearTurno(crearTurnoRequest);
        });

        assertEquals("Ruta no encontrada", exception.getMessage());
        verify(rutaRepository, times(1)).findById(1L);
        verify(turnoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear turno - Duración excede 8 horas")
    void testCrearTurno_DuracionExcede8Horas() {
        // Arrange
        crearTurnoRequest.setHoraInicio(LocalTime.of(6, 0));
        crearTurnoRequest.setHoraFin(LocalTime.of(23, 0)); // 17 horas
        
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaMock));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            turnoService.crearTurno(crearTurnoRequest);
        });

        assertEquals("El turno no puede exceder las 8 horas", exception.getMessage());
        verify(turnoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener todos los turnos - Lista con elementos")
    void testObtenerTodosTurnos_ConElementos() {
        // Arrange
        List<Turno> turnos = Arrays.asList(turnoMock);
        when(turnoRepository.findAll()).thenReturn(turnos);

        // Act
        List<TurnoDTO> resultado = turnoService.obtenerTodosTurnos();

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(turnoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Obtener turnos por ruta - Ruta existente")
    void testObtenerTurnosPorRuta_Existente() {
        // Arrange
        Long rutaId = 1L;
        List<Turno> turnos = Arrays.asList(turnoMock);
        
        when(rutaRepository.findById(rutaId)).thenReturn(Optional.of(rutaMock));
        when(turnoRepository.findByRuta(rutaMock)).thenReturn(turnos);

        // Act
        List<TurnoDTO> resultado = turnoService.obtenerTurnosPorRuta(rutaId);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(rutaRepository, times(1)).findById(rutaId);
        verify(turnoRepository, times(1)).findByRuta(rutaMock);
    }

    @Test
    @DisplayName("Obtener turnos por ruta - Ruta no encontrada")
    void testObtenerTurnosPorRuta_RutaNoEncontrada() {
        // Arrange
        Long rutaId = 999L;
        when(rutaRepository.findById(rutaId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            turnoService.obtenerTurnosPorRuta(rutaId);
        });

        assertEquals("Ruta no encontrada", exception.getMessage());
        verify(turnoRepository, never()).findByRuta(any());
    }

    @Test
    @DisplayName("Obtener turno por ID - Turno existente")
    void testObtenerTurnoPorId_Existente() {
        // Arrange
        Long id = 1L;
        when(turnoRepository.findById(id)).thenReturn(Optional.of(turnoMock));
        when(turnoMapper.toDTO(turnoMock)).thenReturn(turnoDTOMock);
        when(asignacionTurnoRepository.findAsignacionActivaEnFecha(any(), any()))
                .thenReturn(Optional.empty());

        // Act
        TurnoDTO resultado = turnoService.obtenerTurnoPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals(DayOfWeek.MONDAY, resultado.getDiaSemana());
        verify(turnoRepository, times(1)).findById(id);
        verify(turnoMapper, times(1)).toDTO(turnoMock);
    }

    @Test
    @DisplayName("Obtener turno por ID - Turno no encontrado")
    void testObtenerTurnoPorId_NoEncontrado() {
        // Arrange
        Long id = 999L;
        when(turnoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            turnoService.obtenerTurnoPorId(id);
        });

        assertEquals("Turno no encontrado", exception.getMessage());
        verify(turnoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Validar duración de turno - Menor a 8 horas")
    void testValidarDuracionTurno_Valida() {
        // Arrange
        crearTurnoRequest.setHoraInicio(LocalTime.of(6, 0));
        crearTurnoRequest.setHoraFin(LocalTime.of(13, 30)); // 7.5 horas
        
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaMock));
        when(turnoRepository.save(any(Turno.class))).thenReturn(turnoMock);
        when(turnoMapper.toDTO(turnoMock)).thenReturn(turnoDTOMock);

        // Act
        TurnoDTO resultado = turnoService.crearTurno(crearTurnoRequest);

        // Assert
        assertNotNull(resultado);
        verify(turnoRepository, times(1)).save(any(Turno.class));
    }
}
