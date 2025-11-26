package com.FabricaEscuela.Feature1Back;

import com.FabricaEscuela.Feature1Back.entity.Rol;
import com.FabricaEscuela.Feature1Back.entity.Usuario;
import com.FabricaEscuela.Feature1Back.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class Feature1BackApplication {

	public static void main(String[] args) {
		SpringApplication.run(Feature1BackApplication.class, args);
	}

	
}
