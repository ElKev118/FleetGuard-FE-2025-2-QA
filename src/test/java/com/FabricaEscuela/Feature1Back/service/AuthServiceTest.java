package com.FabricaEscuela.Feature1Back.service;

import com.FabricaEscuela.Feature1Back.config.JwtUtil;
import com.FabricaEscuela.Feature1Back.entity.CodigoVerificacion;
import com.FabricaEscuela.Feature1Back.entity.Rol;
import com.FabricaEscuela.Feature1Back.entity.Usuario;
import com.FabricaEscuela.Feature1Back.repository.CodigoVerificacionRepository;
import com.FabricaEscuela.Feature1Back.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para AuthService
 * Patrón AAA: Arrange (preparar), Act (actuar), Assert (verificar)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Pruebas Unitarias")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CodigoVerificacionRepository codigoVerificacionRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioMock;
    private CodigoVerificacion codigoMock;

    @BeforeEach
    void setUp() {
        // Arrange común para todas las pruebas
        // Inyectar el valor de codeExpiration (tiempo de expiración en milisegundos)
        ReflectionTestUtils.setField(authService, "codeExpiration", 300000L); // 5 minutos
        
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setCorreo("test@example.com");
        usuarioMock.setPassword("encodedPassword");
        usuarioMock.setRol(Rol.CONDUCTOR);

        codigoMock = new CodigoVerificacion();
        codigoMock.setId(1L);
        codigoMock.setCodigo("123456");
        codigoMock.setUsuario(usuarioMock);
        codigoMock.setFechaExpiracion(LocalDateTime.now().plusMinutes(5));
        codigoMock.setUsado(false);
    }

    @Test
    @DisplayName("Login exitoso - Debe generar código de verificación y enviarlo por correo")
    void testLogin_Exitoso() {
        // Arrange
        String correo = "test@example.com";
        String password = "password123";
        
        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches(password, usuarioMock.getPassword())).thenReturn(true);
        when(codigoVerificacionRepository.save(any(CodigoVerificacion.class))).thenReturn(codigoMock);
        doNothing().when(emailService).enviarCodigoVerificacion(anyString(), anyString());

        // Act
        String resultado = authService.login(correo, password);

        // Assert
        assertNotNull(resultado);
        assertEquals(6, resultado.length());
        verify(usuarioRepository, times(1)).findByCorreo(correo);
        verify(passwordEncoder, times(1)).matches(password, usuarioMock.getPassword());
        verify(codigoVerificacionRepository, times(1)).save(any(CodigoVerificacion.class));
        verify(emailService, times(1)).enviarCodigoVerificacion(eq(correo), anyString());
    }

    @Test
    @DisplayName("Login fallido - Usuario no encontrado")
    void testLogin_UsuarioNoEncontrado() {
        // Arrange
        String correo = "noexiste@example.com";
        String password = "password123";
        
        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(correo, password);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioRepository, times(1)).findByCorreo(correo);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Login fallido - Contraseña incorrecta")
    void testLogin_PasswordIncorrecto() {
        // Arrange
        String correo = "test@example.com";
        String password = "passwordIncorrecto";
        
        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches(password, usuarioMock.getPassword())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(correo, password);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioRepository, times(1)).findByCorreo(correo);
        verify(passwordEncoder, times(1)).matches(password, usuarioMock.getPassword());
        verify(codigoVerificacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Verificar código - Exitoso")
    void testVerificarCodigo_Exitoso() {
        // Arrange
        String correo = "test@example.com";
        String codigo = "123456";
        String tokenEsperado = "jwt.token.test";
        
        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.of(usuarioMock));
        when(codigoVerificacionRepository.findByCodigoAndUsuarioAndUsadoFalse(codigo, usuarioMock))
                .thenReturn(Optional.of(codigoMock));
        when(jwtUtil.generateToken(correo, usuarioMock.getRol().name())).thenReturn(tokenEsperado);
        when(codigoVerificacionRepository.save(any(CodigoVerificacion.class))).thenReturn(codigoMock);

        // Act
        String token = authService.verificarCodigo(correo, codigo);

        // Assert
        assertNotNull(token);
        assertEquals(tokenEsperado, token);
        assertTrue(codigoMock.isUsado());
        verify(usuarioRepository, times(1)).findByCorreo(correo);
        verify(codigoVerificacionRepository, times(1))
                .findByCodigoAndUsuarioAndUsadoFalse(codigo, usuarioMock);
        verify(jwtUtil, times(1)).generateToken(correo, usuarioMock.getRol().name());
    }

    @Test
    @DisplayName("Verificar código - Código inválido")
    void testVerificarCodigo_CodigoInvalido() {
        // Arrange
        String correo = "test@example.com";
        String codigo = "999999";
        
        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.of(usuarioMock));
        when(codigoVerificacionRepository.findByCodigoAndUsuarioAndUsadoFalse(codigo, usuarioMock))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.verificarCodigo(correo, codigo);
        });

        assertEquals("Código inválido o ya usado", exception.getMessage());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("Verificar código - Código expirado")
    void testVerificarCodigo_CodigoExpirado() {
        // Arrange
        String correo = "test@example.com";
        String codigo = "123456";
        codigoMock.setFechaExpiracion(LocalDateTime.now().minusMinutes(5));
        
        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.of(usuarioMock));
        when(codigoVerificacionRepository.findByCodigoAndUsuarioAndUsadoFalse(codigo, usuarioMock))
                .thenReturn(Optional.of(codigoMock));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.verificarCodigo(correo, codigo);
        });

        assertEquals("El código ha expirado", exception.getMessage());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
}