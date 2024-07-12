package com.forohub.foro;
<!DOCTYPE html>
		<html>
		<head>
		<title>Foro</title>
		<script>
		async function obtenerTopicos() {
		const response = await fetch('/topicos');
		const topicos = await response.json();
		let topicosHtml = '';
		topicos.forEach(topico => {
		topicosHtml += `<li>${topico.titulo}</li>`;
		});
		document.getElementById('lista-topicos').innerHTML = topicosHtml;
		}

		async function crearTopico() {
		const titulo = document.getElementById('titulo').value;
		const contenido = document.getElementById('contenido').value;
		const response = await fetch('/topicos', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ titulo, contenido })
		});
		if (response.ok) {
		obtenerTopicos();
		}
		}

		document.addEventListener('DOMContentLoaded', () => {
		obtenerTopicos();
		});
		</script>
		</head>
		<body>
		<h1>Foro</h1>
		<ul id="lista-topicos"></ul>
		<h2>Crear Tópico</h2>
		<input type="text" id="titulo" placeholder="Título">
		<textarea id="contenido" placeholder="Contenido"></textarea>
		<button onclick="crearTopico()">Crear</button>
		</body>
		</html>

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.persistence.*;

@Entity
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String password;

	// getters y setters
}
import javax.persistence.*;
		import java.util.List;

@Entity
public class Topico {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String titulo;
	private String contenido;

	@OneToMany(mappedBy = "topico")
	private List<Respuesta> respuestas;

	// getters y setters
}
import javax.persistence.*;

@Entity
public class Respuesta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String contenido;

	@ManyToOne
	@JoinColumn(name = "topico_id")
	private Topico topico;

	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	// getters y setters
}
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Usuario findByUsername(String username);
}
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
}
import org.springframework.data.jpa.repository.JpaRepository;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
	List<Respuesta> findByTopicoId(Long topicoId);
}

@SpringBootApplication
public class ForoApplication {
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

	@RestController
	@RequestMapping("/usuarios")
	public class UsuarioController {
		@Autowired
		private UsuarioRepository usuarioRepository;

		@PostMapping("/registro")
		public Usuario registrar(@RequestBody Usuario usuario) {
			return usuarioRepository.save(usuario);
		}

		@PostMapping("/login")
		public Usuario login(@RequestBody Usuario usuario) {
			Usuario user = usuarioRepository.findByUsername(usuario.getUsername());
			if (user != null && user.getPassword().equals(usuario.getPassword())) {
				return user;
			}
			throw new RuntimeException("Credenciales inválidas");
		}
	}
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

	@RestController
	@RequestMapping("/topicos")
	public class TopicoController {
		@Autowired
		private TopicoRepository topicoRepository;

		@GetMapping
		public List<Topico> listarTopicos() {
			return topicoRepository.findAll();
		}

		@GetMapping("/{id}")
		public Topico verTopico(@PathVariable Long id) {
			return topicoRepository.findById(id).orElseThrow(() -> new RuntimeException("Tópico no encontrado"));
		}

		@PostMapping
		public Topico crearTopico(@RequestBody Topico topico) {
			return topicoRepository.save(topico);
		}
	}
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

	@RestController
	@RequestMapping("/respuestas")
	public class RespuestaController {
		@Autowired
		private RespuestaRepository respuestaRepository;

		@GetMapping("/topico/{topicoId}")
		public List<Respuesta> listarRespuestasPorTopico(@PathVariable Long topicoId) {
			return respuestaRepository.findByTopicoId(topicoId);
		}

		@PostMapping
		public Respuesta crearRespuesta(@RequestBody Respuesta respuesta) {
			return respuestaRepository.save(respuesta);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(ForoApplication.class, args);
	}

}
