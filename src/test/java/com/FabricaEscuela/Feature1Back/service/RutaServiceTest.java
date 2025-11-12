package com.FabricaEscuela.Feature1Back.service;

import com.FabricaEscuela.Feature1Back.DTO.RutaDTO;
import com.FabricaEscuela.Feature1Back.entity.Ruta;
import com.FabricaEscuela.Feature1Back.mapper.RutaMapper;
import com.FabricaEscuela.Feature1Back.repository.RutaRepository;
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
 * Pruebas unitarias para RutaService
 * Patrón AAA: Arrange (preparar), Act (actuar), Assert (verificar)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RutaService - Pruebas Unitarias")
class RutaServiceTest {

    @Mock
    private RutaRepository rutaRepository;

    @Mock
    private RutaMapper rutaMapper;

    @InjectMocks
    private RutaService rutaService;

    private Ruta rutaMock;
    private RutaDTO rutaDTOMock;

    @BeforeEach
    void setUp() {
        // Arrange común
        rutaMock = new Ruta();
        rutaMock.setId(1L);
        rutaMock.setNombre("Ruta Norte 105");
        rutaMock.setOrigen("Terminal Norte");
        rutaMock.setDestino("Centro Comercial");
        rutaMock.setDuracionEnMinutos(45);

        rutaDTOMock = new RutaDTO();
        rutaDTOMock.setId(1L);
        rutaDTOMock.setNombre("Ruta Norte 105");
        rutaDTOMock.setOrigen("Terminal Norte");
        rutaDTOMock.setDestino("Centro Comercial");
        rutaDTOMock.setDuracionEnMinutos(45);
    }

    @Test
    @DisplayName("Crear ruta - Exitoso")
    void testCreateRuta_Exitoso() {
        // Arrange
        when(rutaMapper.toEntity(rutaDTOMock)).thenReturn(rutaMock);
        when(rutaRepository.save(rutaMock)).thenReturn(rutaMock);
        when(rutaMapper.toDTO(rutaMock)).thenReturn(rutaDTOMock);

        // Act
        RutaDTO resultado = rutaService.createRuta(rutaDTOMock);

        // Assert
        assertNotNull(resultado);
        assertEquals("Ruta Norte 105", resultado.getNombre());
        assertEquals("Terminal Norte", resultado.getOrigen());
        assertEquals(45, resultado.getDuracionEnMinutos());
        verify(rutaMapper, times(1)).toEntity(rutaDTOMock);
        verify(rutaRepository, times(1)).save(rutaMock);
        verify(rutaMapper, times(1)).toDTO(rutaMock);
    }

    @Test
    @DisplayName("Obtener todas las rutas - Lista con elementos")
    void testGetAllRutas_ConElementos() {
        // Arrange
        List<Ruta> rutas = Arrays.asList(rutaMock);
        List<RutaDTO> rutasDTO = Arrays.asList(rutaDTOMock);
        
        when(rutaRepository.findAll()).thenReturn(rutas);
        when(rutaMapper.toDTOList(rutas)).thenReturn(rutasDTO);

        // Act
        List<RutaDTO> resultado = rutaService.getAllRutas();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Ruta Norte 105", resultado.get(0).getNombre());
        verify(rutaRepository, times(1)).findAll();
        verify(rutaMapper, times(1)).toDTOList(rutas);
    }

    @Test
    @DisplayName("Obtener todas las rutas - Lista vacía")
    void testGetAllRutas_ListaVacia() {
        // Arrange
        List<Ruta> rutasVacias = Arrays.asList();
        List<RutaDTO> rutasDTOVacias = Arrays.asList();
        
        when(rutaRepository.findAll()).thenReturn(rutasVacias);
        when(rutaMapper.toDTOList(rutasVacias)).thenReturn(rutasDTOVacias);

        // Act
        List<RutaDTO> resultado = rutaService.getAllRutas();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(rutaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Obtener ruta por ID - Ruta existente")
    void testGetRutaById_Existente() {
        // Arrange
        Long id = 1L;
        when(rutaRepository.findById(id)).thenReturn(Optional.of(rutaMock));
        when(rutaMapper.toDTO(rutaMock)).thenReturn(rutaDTOMock);

        // Act
        Optional<RutaDTO> resultado = rutaService.getRutaById(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        assertEquals("Ruta Norte 105", resultado.get().getNombre());
        verify(rutaRepository, times(1)).findById(id);
        verify(rutaMapper, times(1)).toDTO(rutaMock);
    }

    @Test
    @DisplayName("Obtener ruta por ID - Ruta no existente")
    void testGetRutaById_NoExistente() {
        // Arrange
        Long id = 999L;
        when(rutaRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<RutaDTO> resultado = rutaService.getRutaById(id);

        // Assert
        assertFalse(resultado.isPresent());
        verify(rutaRepository, times(1)).findById(id);
        verify(rutaMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Eliminar ruta - Ruta existente")
    void testDeleteRuta_Existente() {
        // Arrange
        Long id = 1L;
        when(rutaRepository.existsById(id)).thenReturn(true);
        doNothing().when(rutaRepository).deleteById(id);

        // Act
        boolean resultado = rutaService.deleteRuta(id);

        // Assert
        assertTrue(resultado);
        verify(rutaRepository, times(1)).existsById(id);
        verify(rutaRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Eliminar ruta - Ruta no existente")
    void testDeleteRuta_NoExistente() {
        // Arrange
        Long id = 999L;
        when(rutaRepository.existsById(id)).thenReturn(false);

        // Act
        boolean resultado = rutaService.deleteRuta(id);

        // Assert
        assertFalse(resultado);
        verify(rutaRepository, times(1)).existsById(id);
        verify(rutaRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Crear ruta con código único")
    void testCreateRuta_CodigoUnico() {
        // Arrange
        RutaDTO nuevaRutaDTO = new RutaDTO();
        nuevaRutaDTO.setNombre("Ruta Sur 200");
        nuevaRutaDTO.setOrigen("Terminal Sur");
        nuevaRutaDTO.setDestino("Universidad");
        nuevaRutaDTO.setDuracionEnMinutos(60);

        Ruta nuevaRuta = new Ruta();
        nuevaRuta.setNombre("Ruta Sur 200");
        nuevaRuta.setOrigen("Terminal Sur");
        nuevaRuta.setDestino("Universidad");

        when(rutaMapper.toEntity(nuevaRutaDTO)).thenReturn(nuevaRuta);
        when(rutaRepository.save(nuevaRuta)).thenReturn(nuevaRuta);
        when(rutaMapper.toDTO(nuevaRuta)).thenReturn(nuevaRutaDTO);

        // Act
        RutaDTO resultado = rutaService.createRuta(nuevaRutaDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("Ruta Sur 200", resultado.getNombre());
        verify(rutaRepository, times(1)).save(nuevaRuta);
    }

    @Test
    @DisplayName("Obtener ruta por ID con todos los detalles")
    void testGetRutaById_ConDetalles() {
        // Arrange
        Long id = 1L;
        when(rutaRepository.findById(id)).thenReturn(Optional.of(rutaMock));
        when(rutaMapper.toDTO(rutaMock)).thenReturn(rutaDTOMock);

        // Act
        Optional<RutaDTO> resultado = rutaService.getRutaById(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Ruta Norte 105", resultado.get().getNombre());
        assertEquals("Terminal Norte", resultado.get().getOrigen());
        assertEquals("Centro Comercial", resultado.get().getDestino());
        assertEquals(45, resultado.get().getDuracionEnMinutos());
    }
}
