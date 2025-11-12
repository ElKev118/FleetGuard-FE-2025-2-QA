package com.FabricaEscuela.Feature1Back.service;

import com.FabricaEscuela.Feature1Back.DTO.UsuarioDTO;
import com.FabricaEscuela.Feature1Back.entity.Rol;
import com.FabricaEscuela.Feature1Back.entity.Usuario;
import com.FabricaEscuela.Feature1Back.mapper.UsuarioMapper;
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
 * Pruebas unitarias para UsuarioService
 * Patrón AAA: Arrange (preparar), Act (actuar), Assert (verificar)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - Pruebas Unitarias")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioMock;
    private UsuarioDTO usuarioDTOMock;

    @BeforeEach
    void setUp() {
        // Arrange común
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setCorreo("admin@example.com");
        usuarioMock.setPassword("encodedPassword");
        usuarioMock.setRol(Rol.ADMIN);

        usuarioDTOMock = new UsuarioDTO();
        usuarioDTOMock.setId(1L);
        usuarioDTOMock.setCorreo("admin@example.com");
        usuarioDTOMock.setRol(Rol.ADMIN);
    }

    @Test
    @DisplayName("Obtener todos los usuarios - Lista con elementos")
    void testGetAllUsuarios_ConElementos() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuarioMock);
        when(usuarioRepository.findAll()).thenReturn(usuarios);
        when(usuarioMapper.toDTO(usuarioMock)).thenReturn(usuarioDTOMock);

        // Act
        List<UsuarioDTO> resultado = usuarioService.getAllUsuarios();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("admin@example.com", resultado.get(0).getCorreo());
        verify(usuarioRepository, times(1)).findAll();
        verify(usuarioMapper, times(1)).toDTO(usuarioMock);
    }

    @Test
    @DisplayName("Obtener todos los usuarios - Lista vacía")
    void testGetAllUsuarios_ListaVacia() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<UsuarioDTO> resultado = usuarioService.getAllUsuarios();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Obtener usuario por ID - Usuario existente")
    void testGetUsuarioById_Existente() {
        // Arrange
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioMock));
        when(usuarioMapper.toDTO(usuarioMock)).thenReturn(usuarioDTOMock);

        // Act
        UsuarioDTO resultado = usuarioService.getUsuarioById(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("admin@example.com", resultado.getCorreo());
        assertEquals(Rol.ADMIN, resultado.getRol());
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioMapper, times(1)).toDTO(usuarioMock);
    }

    @Test
    @DisplayName("Obtener usuario por ID - Usuario no encontrado")
    void testGetUsuarioById_NoEncontrado() {
        // Arrange
        Long id = 999L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.getUsuarioById(id);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Crear usuario - Exitoso")
    void testCreateUsuario_Exitoso() {
        // Arrange
        when(usuarioMapper.toEntity(usuarioDTOMock)).thenReturn(usuarioMock);
        when(usuarioRepository.save(usuarioMock)).thenReturn(usuarioMock);
        when(usuarioMapper.toDTO(usuarioMock)).thenReturn(usuarioDTOMock);

        // Act
        UsuarioDTO resultado = usuarioService.createUsuario(usuarioDTOMock);

        // Assert
        assertNotNull(resultado);
        assertEquals("admin@example.com", resultado.getCorreo());
        assertEquals(Rol.ADMIN, resultado.getRol());
        verify(usuarioMapper, times(1)).toEntity(usuarioDTOMock);
        verify(usuarioRepository, times(1)).save(usuarioMock);
        verify(usuarioMapper, times(1)).toDTO(usuarioMock);
    }

    @Test
    @DisplayName("Actualizar usuario - Usuario existente")
    void testUpdateUsuario_Existente() {
        // Arrange
        Long id = 1L;
        UsuarioDTO usuarioActualizadoDTO = new UsuarioDTO();
        usuarioActualizadoDTO.setCorreo("admin.nuevo@example.com");
        usuarioActualizadoDTO.setRol(Rol.CONDUCTOR);

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(id);
        usuarioActualizado.setCorreo("admin.nuevo@example.com");
        usuarioActualizado.setRol(Rol.CONDUCTOR);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizado);
        when(usuarioMapper.toDTO(usuarioActualizado)).thenReturn(usuarioActualizadoDTO);

        // Act
        UsuarioDTO resultado = usuarioService.updateUsuario(id, usuarioActualizadoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("admin.nuevo@example.com", resultado.getCorreo());
        assertEquals(Rol.CONDUCTOR, resultado.getRol());
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Actualizar usuario - Usuario no encontrado")
    void testUpdateUsuario_NoEncontrado() {
        // Arrange
        Long id = 999L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.updateUsuario(id, usuarioDTOMock);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar usuario - Usuario existente")
    void testDeleteUsuario_Existente() {
        // Arrange
        Long id = 1L;
        when(usuarioRepository.existsById(id)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(id);

        // Act
        usuarioService.deleteUsuario(id);

        // Assert
        verify(usuarioRepository, times(1)).existsById(id);
        verify(usuarioRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Eliminar usuario - Usuario no encontrado")
    void testDeleteUsuario_NoEncontrado() {
        // Arrange
        Long id = 999L;
        when(usuarioRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.deleteUsuario(id);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).existsById(id);
        verify(usuarioRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Crear usuario con rol de conductor")
    void testCreateUsuario_RolConductor() {
        // Arrange
        UsuarioDTO conductorDTO = new UsuarioDTO();
        conductorDTO.setCorreo("conductor@example.com");
        conductorDTO.setRol(Rol.CONDUCTOR);

        Usuario conductor = new Usuario();
        conductor.setCorreo("conductor@example.com");
        conductor.setRol(Rol.CONDUCTOR);

        when(usuarioMapper.toEntity(conductorDTO)).thenReturn(conductor);
        when(usuarioRepository.save(conductor)).thenReturn(conductor);
        when(usuarioMapper.toDTO(conductor)).thenReturn(conductorDTO);

        // Act
        UsuarioDTO resultado = usuarioService.createUsuario(conductorDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(Rol.CONDUCTOR, resultado.getRol());
        verify(usuarioRepository, times(1)).save(conductor);
    }
}
