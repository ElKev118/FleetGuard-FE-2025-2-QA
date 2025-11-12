package com.FabricaEscuela.Feature1Back.service;

import com.FabricaEscuela.Feature1Back.DTO.ConductorDTO;
import com.FabricaEscuela.Feature1Back.DTO.UsuarioDTO;
import com.FabricaEscuela.Feature1Back.entity.Conductor;
import com.FabricaEscuela.Feature1Back.entity.Rol;
import com.FabricaEscuela.Feature1Back.entity.Usuario;
import com.FabricaEscuela.Feature1Back.mapper.ConductorMapper;
import com.FabricaEscuela.Feature1Back.repository.ConductorRepository;
import com.FabricaEscuela.Feature1Back.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ConductorService
 * Patrón AAA: Arrange (preparar), Act (actuar), Assert (verificar)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConductorService - Pruebas Unitarias")
class ConductorServiceTest {

    @Mock
    private ConductorRepository conductorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConductorMapper conductorMapper;

    @InjectMocks
    private ConductorService conductorService;

    private Conductor conductorMock;
    private ConductorDTO conductorDTOMock;
    private Usuario usuarioMock;
    private UsuarioDTO usuarioDTOMock;

    @BeforeEach
    void setUp() {
        // Arrange común
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setCorreo("juan.perez@example.com");
        usuarioMock.setPassword("encodedPassword");
        usuarioMock.setRol(Rol.CONDUCTOR);

        usuarioDTOMock = new UsuarioDTO();
        usuarioDTOMock.setId(1L);
        usuarioDTOMock.setCorreo("juan.perez@example.com");
        usuarioDTOMock.setRol(Rol.CONDUCTOR);

        conductorMock = new Conductor();
        conductorMock.setId(1L);
        conductorMock.setNombreCompleto("Juan Pérez");
        conductorMock.setTelefono("3001234567");
        conductorMock.setLicencia("A2-123456");
        conductorMock.setUsuario(usuarioMock);

        conductorDTOMock = new ConductorDTO();
        conductorDTOMock.setId(1L);
        conductorDTOMock.setNombreCompleto("Juan Pérez");
        conductorDTOMock.setTelefono("3001234567");
        conductorDTOMock.setLicencia("A2-123456");
        conductorDTOMock.setUsuario(usuarioMock);
    }

    @Test
    @DisplayName("Obtener todos los conductores - Lista con elementos")
    void testGetAllConductores_ConElementos() {
        // Arrange
        List<Conductor> conductores = Arrays.asList(conductorMock);
        
        when(conductorRepository.findAll()).thenReturn(conductores);
        when(conductorMapper.toDTO(conductorMock)).thenReturn(conductorDTOMock);

        // Act
        List<ConductorDTO> resultado = conductorService.getAllConductores();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombreCompleto());
        verify(conductorRepository, times(1)).findAll();
        verify(conductorMapper, times(1)).toDTO(conductorMock);
    }

    @Test
    @DisplayName("Obtener todos los conductores - Lista vacía")
    void testGetAllConductores_ListaVacia() {
        // Arrange
        when(conductorRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<ConductorDTO> resultado = conductorService.getAllConductores();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(conductorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Obtener conductor por ID - Conductor existente")
    void testGetConductorById_Existente() {
        // Arrange
        Long id = 1L;
        when(conductorRepository.findById(id)).thenReturn(Optional.of(conductorMock));
        when(conductorMapper.toDTO(conductorMock)).thenReturn(conductorDTOMock);

        // Act
        ConductorDTO resultado = conductorService.getConductorById(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Juan Pérez", resultado.getNombreCompleto());
        verify(conductorRepository, times(1)).findById(id);
        verify(conductorMapper, times(1)).toDTO(conductorMock);
    }

    @Test
    @DisplayName("Obtener conductor por ID - Conductor no existente")
    void testGetConductorById_NoExistente() {
        // Arrange
        Long id = 999L;
        when(conductorRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            conductorService.getConductorById(id);
        });
        
        assertEquals("Conductor no encontrado", exception.getMessage());
        verify(conductorRepository, times(1)).findById(id);
        verify(conductorMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Crear conductor - Exitoso")
    void testCreateConductor_Exitoso() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(conductorMapper.toEntity(conductorDTOMock)).thenReturn(conductorMock);
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductorMock);
        when(conductorMapper.toDTO(conductorMock)).thenReturn(conductorDTOMock);

        // Act
        ConductorDTO resultado = conductorService.createConductor(conductorDTOMock);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getNombreCompleto());
        assertEquals("A2-123456", resultado.getLicencia());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(conductorMapper, times(1)).toEntity(conductorDTOMock);
        verify(conductorRepository, times(1)).save(any(Conductor.class));
        verify(conductorMapper, times(1)).toDTO(conductorMock);
    }

    @Test
    @DisplayName("Actualizar conductor - Conductor existente")
    void testUpdateConductor_Existente() {
        // Arrange
        Long id = 1L;
        ConductorDTO conductorActualizadoDTO = new ConductorDTO();
        conductorActualizadoDTO.setNombreCompleto("Juan Carlos Pérez");
        conductorActualizadoDTO.setTelefono("3009876543");
        conductorActualizadoDTO.setLicencia("B1-789012");

        Conductor conductorActualizado = new Conductor();
        conductorActualizado.setId(id);
        conductorActualizado.setNombreCompleto("Juan Carlos Pérez");
        conductorActualizado.setTelefono("3009876543");
        conductorActualizado.setLicencia("B1-789012");

        when(conductorRepository.findById(id)).thenReturn(Optional.of(conductorMock));
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductorActualizado);
        when(conductorMapper.toDTO(conductorActualizado)).thenReturn(conductorActualizadoDTO);

        // Act
        ConductorDTO resultado = conductorService.updateConductor(id, conductorActualizadoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan Carlos Pérez", resultado.getNombreCompleto());
        verify(conductorRepository, times(1)).findById(id);
        verify(conductorRepository, times(1)).save(any(Conductor.class));
    }

    @Test
    @DisplayName("Actualizar conductor - Conductor no existente")
    void testUpdateConductor_NoExistente() {
        // Arrange
        Long id = 999L;
        when(conductorRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            conductorService.updateConductor(id, conductorDTOMock);
        });
        
        assertEquals("Conductor no encontrado", exception.getMessage());
        verify(conductorRepository, times(1)).findById(id);
        verify(conductorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar conductor - Exitoso")
    void testDeleteConductor_Exitoso() {
        // Arrange
        Long id = 1L;
        when(conductorRepository.existsById(id)).thenReturn(true);
        doNothing().when(conductorRepository).deleteById(id);

        // Act
        conductorService.deleteConductor(id);

        // Assert
        verify(conductorRepository, times(1)).existsById(id);
        verify(conductorRepository, times(1)).deleteById(id);
    }
}
