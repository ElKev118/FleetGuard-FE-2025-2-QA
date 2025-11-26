package com.FabricaEscuela.Feature1Back.service;

import com.FabricaEscuela.Feature1Back.DTO.AsignacionTurnoDTO;
import com.FabricaEscuela.Feature1Back.entity.*;
import com.FabricaEscuela.Feature1Back.mapper.AsignacionTurnoMapper;
import com.FabricaEscuela.Feature1Back.repository.AsignacionTurnoRepository;
import com.FabricaEscuela.Feature1Back.repository.ConductorRepository;
import com.FabricaEscuela.Feature1Back.repository.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AsignacionTurnoService - Pruebas Unitarias")
class AsignacionTurnoServiceTest {

    @Mock
    private AsignacionTurnoRepository asignacionTurnoRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private ConductorRepository conductorRepository;

    @Mock
    private AsignacionTurnoMapper asignacionTurnoMapper;

    @InjectMocks
    private AsignacionTurnoService asignacionTurnoService;

    private Turno turnoMock;
    private Conductor conductorMock;
    private AsignacionTurno asignacionMock;
    private AsignacionTurnoDTO asignacionDTO;

    @BeforeEach
    void setUp() {
        turnoMock = new Turno();
        turnoMock.setId(1L);
        turnoMock.setHoraInicio(LocalTime.of(8, 0));
        turnoMock.setHoraFin(LocalTime.of(16, 0));

        conductorMock = new Conductor();
        conductorMock.setId(1L);
        conductorMock.setNombreCompleto("Juan Perez");

        asignacionMock = new AsignacionTurno();
        asignacionMock.setId(1L);
        asignacionMock.setTurno(turnoMock);
        asignacionMock.setConductor(conductorMock);
        asignacionMock.setFechaInicio(LocalDate.now());
        asignacionMock.setFechaFin(LocalDate.now().plusDays(1));
        asignacionMock.setEstado(EstadoAsignacion.PROGRAMADA);

        asignacionDTO = new AsignacionTurnoDTO();
        asignacionDTO.setId(1L);
        asignacionDTO.setTurnoId(1L);
        asignacionDTO.setConductorId(1L);
        asignacionDTO.setFechaInicio(LocalDate.now());
        asignacionDTO.setFechaFin(LocalDate.now().plusDays(1));
        asignacionDTO.setEstado(EstadoAsignacion.PROGRAMADA);
    }

    @Test
    @DisplayName("Asignar conductor a turno - Exitoso")
    void testAsignarConductorATurno_Exitoso() {
        // Arrange
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turnoMock));
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorMock));
        when(asignacionTurnoRepository.conductorTieneAsignacionEnFecha(any(Conductor.class), any(LocalDate.class)))
                .thenReturn(false);
        when(asignacionTurnoRepository.save(any(AsignacionTurno.class))).thenReturn(asignacionMock);
        when(asignacionTurnoMapper.toDTO(any(AsignacionTurno.class))).thenReturn(asignacionDTO);

        // Act
        AsignacionTurnoDTO resultado = asignacionTurnoService.asignarConductorATurno(asignacionDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoAsignacion.PROGRAMADA, resultado.getEstado());
        verify(turnoRepository).findById(1L);
        verify(conductorRepository).findById(1L);
        verify(asignacionTurnoRepository).save(any(AsignacionTurno.class));
    }

    @Test
    @DisplayName("Asignar conductor a turno - Turno no encontrado")
    void testAsignarConductorATurno_TurnoNoEncontrado() {
        // Arrange
        when(turnoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            asignacionTurnoService.asignarConductorATurno(asignacionDTO);
        });
        assertEquals("Turno no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Asignar conductor a turno - Conductor no encontrado")
    void testAsignarConductorATurno_ConductorNoEncontrado() {
        // Arrange
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turnoMock));
        when(conductorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            asignacionTurnoService.asignarConductorATurno(asignacionDTO);
        });
        assertEquals("Conductor no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Asignar conductor a turno - Conductor ocupado")
    void testAsignarConductorATurno_ConductorOcupado() {
        // Arrange
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turnoMock));
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorMock));
        when(asignacionTurnoRepository.conductorTieneAsignacionEnFecha(any(Conductor.class), any(LocalDate.class)))
                .thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            asignacionTurnoService.asignarConductorATurno(asignacionDTO);
        });
        assertEquals("El conductor ya tiene un turno asignado en esa fecha", exception.getMessage());
    }

    @Test
    @DisplayName("Obtener todas las asignaciones")
    void testObtenerTodasAsignaciones() {
        // Arrange
        when(asignacionTurnoRepository.findAll()).thenReturn(Arrays.asList(asignacionMock));
        when(asignacionTurnoMapper.toDTO(asignacionMock)).thenReturn(asignacionDTO);

        // Act
        List<AsignacionTurnoDTO> resultado = asignacionTurnoService.obtenerTodasAsignaciones();

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(asignacionTurnoRepository).findAll();
    }

    @Test
    @DisplayName("Obtener asignaciones por conductor - Exitoso")
    void testObtenerAsignacionesPorConductor_Exitoso() {
        // Arrange
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorMock));
        when(asignacionTurnoRepository.findByConductor(conductorMock)).thenReturn(Arrays.asList(asignacionMock));
        when(asignacionTurnoMapper.toDTO(asignacionMock)).thenReturn(asignacionDTO);

        // Act
        List<AsignacionTurnoDTO> resultado = asignacionTurnoService.obtenerAsignacionesPorConductor(1L);

        // Assert
        assertFalse(resultado.isEmpty());
        verify(conductorRepository).findById(1L);
        verify(asignacionTurnoRepository).findByConductor(conductorMock);
    }

    @Test
    @DisplayName("Obtener asignaciones por conductor - Conductor no encontrado")
    void testObtenerAsignacionesPorConductor_NoEncontrado() {
        // Arrange
        when(conductorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            asignacionTurnoService.obtenerAsignacionesPorConductor(1L);
        });
        assertEquals("Conductor no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Obtener asignaciones activas")
    void testObtenerAsignacionesActivas() {
        // Arrange
        when(asignacionTurnoRepository.findByEstado(EstadoAsignacion.EN_CURSO)).thenReturn(Arrays.asList(asignacionMock));
        when(asignacionTurnoMapper.toDTO(asignacionMock)).thenReturn(asignacionDTO);

        // Act
        List<AsignacionTurnoDTO> resultado = asignacionTurnoService.obtenerAsignacionesActivas();

        // Assert
        assertFalse(resultado.isEmpty());
        verify(asignacionTurnoRepository).findByEstado(EstadoAsignacion.EN_CURSO);
    }

    @Test
    @DisplayName("Iniciar turno - Exitoso")
    void testIniciarTurno_Exitoso() {
        // Arrange
        asignacionMock.setEstado(EstadoAsignacion.PROGRAMADA);
        when(asignacionTurnoRepository.findById(1L)).thenReturn(Optional.of(asignacionMock));
        when(asignacionTurnoRepository.save(any(AsignacionTurno.class))).thenReturn(asignacionMock);
        when(asignacionTurnoMapper.toDTO(asignacionMock)).thenReturn(asignacionDTO);

        // Act
        AsignacionTurnoDTO resultado = asignacionTurnoService.iniciarTurno(1L);

        // Assert
        assertNotNull(resultado);
        verify(asignacionTurnoRepository).save(asignacionMock);
        assertEquals(EstadoAsignacion.EN_CURSO, asignacionMock.getEstado());
        assertNotNull(asignacionMock.getHoraInicioReal());
    }

    @Test
    @DisplayName("Iniciar turno - Estado incorrecto")
    void testIniciarTurno_EstadoIncorrecto() {
        // Arrange
        asignacionMock.setEstado(EstadoAsignacion.FINALIZADA);
        when(asignacionTurnoRepository.findById(1L)).thenReturn(Optional.of(asignacionMock));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            asignacionTurnoService.iniciarTurno(1L);
        });
        assertEquals("Solo se pueden iniciar turnos en estado PROGRAMADA", exception.getMessage());
    }

    @Test
    @DisplayName("Finalizar turno - Exitoso")
    void testFinalizarTurno_Exitoso() {
        // Arrange
        asignacionMock.setEstado(EstadoAsignacion.EN_CURSO);
        when(asignacionTurnoRepository.findById(1L)).thenReturn(Optional.of(asignacionMock));
        when(asignacionTurnoRepository.save(any(AsignacionTurno.class))).thenReturn(asignacionMock);
        when(asignacionTurnoMapper.toDTO(asignacionMock)).thenReturn(asignacionDTO);

        // Act
        AsignacionTurnoDTO resultado = asignacionTurnoService.finalizarTurno(1L);

        // Assert
        assertNotNull(resultado);
        verify(asignacionTurnoRepository).save(asignacionMock);
        assertEquals(EstadoAsignacion.FINALIZADA, asignacionMock.getEstado());
        assertNotNull(asignacionMock.getHoraFinReal());
    }

    @Test
    @DisplayName("Finalizar turno - Estado incorrecto")
    void testFinalizarTurno_EstadoIncorrecto() {
        // Arrange
        asignacionMock.setEstado(EstadoAsignacion.PROGRAMADA);
        when(asignacionTurnoRepository.findById(1L)).thenReturn(Optional.of(asignacionMock));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            asignacionTurnoService.finalizarTurno(1L);
        });
        assertEquals("Solo se pueden finalizar turnos en estado EN_CURSO", exception.getMessage());
    }

    @Test
    @DisplayName("Cancelar asignación - Exitoso")
    void testCancelarAsignacion_Exitoso() {
        // Arrange
        asignacionMock.setEstado(EstadoAsignacion.PROGRAMADA);
        when(asignacionTurnoRepository.findById(1L)).thenReturn(Optional.of(asignacionMock));

        // Act
        asignacionTurnoService.cancelarAsignacion(1L);

        // Assert
        verify(asignacionTurnoRepository).save(asignacionMock);
        assertEquals(EstadoAsignacion.CANCELADA, asignacionMock.getEstado());
    }

    @Test
    @DisplayName("Cancelar asignación - Ya finalizada")
    void testCancelarAsignacion_Finalizada() {
        // Arrange
        asignacionMock.setEstado(EstadoAsignacion.FINALIZADA);
        when(asignacionTurnoRepository.findById(1L)).thenReturn(Optional.of(asignacionMock));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            asignacionTurnoService.cancelarAsignacion(1L);
        });
        assertEquals("No se puede cancelar una asignación finalizada", exception.getMessage());
    }
}
