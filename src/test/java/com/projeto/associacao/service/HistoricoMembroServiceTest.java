package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projeto.associacao.dto.historicoMembro.HistoricoMembroRequest;
import com.projeto.associacao.dto.membro.MembroResponse;
import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.HistoricoMembro;
import com.projeto.associacao.model.Membro;
import com.projeto.associacao.model.TipoEvento;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.HistoricoMembroRepository;
import com.projeto.associacao.repository.MembroRepository;
import com.projeto.associacao.repository.TipoEventoRepository;
import com.projeto.associacao.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class HistoricoMembroServiceTest {

	@Mock
	private HistoricoMembroRepository repository;

	@Mock
	private MembroRepository membroRepository;

	@Mock
	private TipoEventoRepository tipoEventoRepository;

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private MembroService membroService;

	@Mock
	private UsuarioService usuarioService;

	@InjectMocks
	private HistoricoMembroService service;

	private HistoricoMembroRequest requestValido() {
		HistoricoMembroRequest request = new HistoricoMembroRequest();
		request.setIdMembro(1);
		request.setIdTipoEvento(2);
		request.setDescricao("Pagou a mensalidade");
		return request;
	}

	@Test
	void cadastrarUsaUsuarioAutenticadoComoRegistro() throws BusinessRuleException {
		Usuario usuarioLogado = new Usuario();
		usuarioLogado.setId(5);
		usuarioLogado.setLogin("fulano");

		when(membroRepository.findById(1)).thenReturn(new Membro());
		when(tipoEventoRepository.findById(2)).thenReturn(new TipoEvento());
		when(usuarioRepository.findByLogin("fulano")).thenReturn(usuarioLogado);
		when(repository.save(any(HistoricoMembro.class))).thenAnswer(inv -> inv.getArgument(0));
		when(membroService.converterParaResponse(any())).thenReturn(new MembroResponse());

		UsuarioResponse usuarioResponse = new UsuarioResponse();
		usuarioResponse.setId(5);
		when(usuarioService.converterParaResponse(usuarioLogado)).thenReturn(usuarioResponse);

		var resultado = service.cadastrar(requestValido(), "fulano");

		assertEquals(5, resultado.getUsuarioRegistro().getId());
	}

	@Test
	void cadastrarRejeitaMembroInexistente() {
		when(usuarioRepository.findByLogin("fulano")).thenReturn(new Usuario());
		when(membroRepository.findById(1)).thenReturn(null);
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(requestValido(), "fulano"));
	}

	@Test
	void cadastrarRejeitaTipoEventoInexistente() {
		when(membroRepository.findById(1)).thenReturn(new Membro());
		when(tipoEventoRepository.findById(2)).thenReturn(null);
		when(usuarioRepository.findByLogin("fulano")).thenReturn(new Usuario());
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(requestValido(), "fulano"));
	}

	@Test
	void selecionarPorMembroRejeitaMembroInexistente() {
		when(membroRepository.findById(99)).thenReturn(null);
		assertThrows(BusinessRuleException.class, () -> service.selecionarPorMembro(99));
	}
}
