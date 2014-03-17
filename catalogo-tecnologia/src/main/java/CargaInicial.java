import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.annotation.Priority;
import br.gov.frameworkdemoiselle.lifecycle.Startup;
import br.gov.frameworkdemoiselle.stereotype.BusinessController;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.serpro.catalogo.bussiness.FaseBC;
import br.gov.serpro.catalogo.entity.Analise;
import br.gov.serpro.catalogo.entity.Categoria;
import br.gov.serpro.catalogo.entity.Declinio;
import br.gov.serpro.catalogo.entity.Fabricante;
import br.gov.serpro.catalogo.entity.FaseEnum;
import br.gov.serpro.catalogo.entity.FaseProduto;
import br.gov.serpro.catalogo.entity.Fornecedor;
import br.gov.serpro.catalogo.entity.Grupo;
import br.gov.serpro.catalogo.entity.Internalizacao;
import br.gov.serpro.catalogo.entity.Licenciamento;
import br.gov.serpro.catalogo.entity.Observacao;
import br.gov.serpro.catalogo.entity.OrigemDemanda;
import br.gov.serpro.catalogo.entity.Perfil;
import br.gov.serpro.catalogo.entity.PlataformaTecnologica;
import br.gov.serpro.catalogo.entity.Produto;
import br.gov.serpro.catalogo.entity.Prospeccao;
import br.gov.serpro.catalogo.entity.Situacao;
import br.gov.serpro.catalogo.entity.Sustentacao;
import br.gov.serpro.catalogo.entity.Tecnologia;
import br.gov.serpro.catalogo.entity.User;
import br.gov.serpro.catalogo.persistence.AnaliseDAO;
import br.gov.serpro.catalogo.persistence.CategoriaDAO;
import br.gov.serpro.catalogo.persistence.DeclinioDAO;
import br.gov.serpro.catalogo.persistence.FabricanteDAO;
import br.gov.serpro.catalogo.persistence.FaseProdutoDAO;
import br.gov.serpro.catalogo.persistence.FornecedorDAO;
import br.gov.serpro.catalogo.persistence.GrupoDAO;
import br.gov.serpro.catalogo.persistence.InternalizacaoDAO;
import br.gov.serpro.catalogo.persistence.LicenciamentoDAO;
import br.gov.serpro.catalogo.persistence.ObservacaoDAO;
import br.gov.serpro.catalogo.persistence.OrigemDemandaDAO;
import br.gov.serpro.catalogo.persistence.PlataformaTecnologicaDAO;
import br.gov.serpro.catalogo.persistence.ProdutoDAO;
import br.gov.serpro.catalogo.persistence.ProspeccaoDAO;
import br.gov.serpro.catalogo.persistence.SustentacaoDAO;
import br.gov.serpro.catalogo.persistence.TecnologiaDAO;
import br.gov.serpro.catalogo.persistence.UserDAO;



@BusinessController
public class CargaInicial {
	
	@Inject
	private FaseBC faseBC;
	
//	@Inject
//	private AnaliseDAO analiseDAO;
//	
//	@Inject
//	private ProspeccaoDAO prospeccaoDAO;
//	
//	@Inject
//	private InternalizacaoDAO internalizacaoDAO;
//	
//	@Inject
//	private SustentacaoDAO sustentacaoDAO;
//	
//	@Inject
//	private DeclinioDAO declinioDAO;
	
	@Inject
	private ProdutoDAO produtoDAO;
	
	@Inject
	private LicenciamentoDAO licenciamentoDAO;
	
	@Inject
	private FabricanteDAO fabricanteDAO;
	
	@Inject
	private FornecedorDAO fornecedorDAO;
	
	@Inject
	private PlataformaTecnologicaDAO plataformaTecnologicaDAO;
	
	@Inject
	private FaseProdutoDAO faseProdutoDAO;
		
	@Inject
	private TecnologiaDAO tecnologiaDAO;
	
	@Inject
	private CategoriaDAO categoriaDAO;
	
	@Inject
	private UserDAO usuarioDAO;
	
	
	@Inject
	private GrupoDAO grupoDAO;
	
	
	@Inject 
	private OrigemDemandaDAO origemDemandaDAO;
	
	@Inject
	private ObservacaoDAO obsDAO;
	

	static User usuario1;
	User usuario2;
	
	@Startup @Priority(0)
	@Transactional
	public void criarUsuarios(){
		
		List<Perfil> perfis1 = new ArrayList<Perfil>();
		perfis1.add(Perfil.ANALISE);
		perfis1.add(Perfil.PROSPECCAO);
		Grupo grupo = new Grupo();
		grupo.setNome("Grupo 1");
		grupo.setDescricao("grupo 1");
		grupo.setPerfis(perfis1);
		
		grupoDAO.insert(grupo);
		
		usuario1 = new User();
		usuario1.setCPF("82546010549");
		usuario1.setName("Robson Saraiva Ximenes");
		usuario1.setEmail("robson.ximenes@serpro.gov.br");
		usuario1.setSetor("CETEC/CTSDR");
		usuario1.setGrupos(new ArrayList<Grupo>());
		usuario1.getGrupos().add(grupo);
		
		usuarioDAO.insert(usuario1);
		
//		usuario2 = new User();
//		usuario2.setCPF("Thiago de Lima Mariano");
//		usuario2.setEmail("thiago.mariano@serpro.gov.br");
//		usuario2.setGrupos(new ArrayList<Grupo>());
//		usuario2.getGrupos().add(grupo);
//		
//		usuarioDAO.insert(usuario2);
		
	}
	
	
	
	@Startup @Priority(99)
	@Transactional
	public void criarAnalises(){
		
		OrigemDemanda origem = origemDemandaDAO.findAll().get(0);
		
		Analise a = new Analise();		
		a.setCodigoReferencia("R173");
		a.setDataRealizacao(new Date());
		a.setDemandanteUnidade("CETEC/CTSDR");
		a.setDemandanteRepresentante(usuario1);
		a.setObjetivo("Precise de uma ferramenta que facilite a geração de lero-lero para piadas");
		a.setUnidadeGestora("CTSDR");
		a.setOrigemReferencia(origem);
		a.setSituacao(Situacao.APROVADO);
		a.setSituacaoJustificativa("Me parece ser bem plausível. Devemos selecionar algumas tecnologias para isso.");
		a.setProximaFase(FaseEnum.PROSPECCAO);
		a.setProximaFaseLider(usuario1);
		a.setProximaFaseUnidadeGestora(usuario1.getSetor());
		a.setProximaFaseJustificativa("Precisamos que este estudo seja prospectado com no mínimo 3 tecnologias.");
		
		
		a = (Analise)faseBC.salvar(a);	
		
		Observacao obs = new Observacao();
		obs.setData(new Date());
		obs.setUsuario(usuario1);
		obs.setFase(a);
		obs.setObservacao("Apenas observando como ficou legal.");
		
		obsDAO.insert(obs);
		
		
		Prospeccao p = (Prospeccao)faseBC.finalizarFase(a);		
		p.setCodigoReferencia("R174");
		p.setOrigemReferencia(origem);
		p.setObjetivo("Selecionar os tipos de bolas de futsal na copa do mundo.");		
		p.setDataRealizacao(new Date());
		p.setSituacao(Situacao.APROVADO);
		p.setSituacaoJustificativa("Aprovei pois a prospeccao foi bem sucedida.");
		p.setTestes("Os testes realizados foram feitos na regional Salvador em laboratório isolado. Foram averiguados testes de blablablabla. Maiores detalhes sobre os testes realizados podem ser encontrados em Arquivo1.odt.");
		p.setConclusao("A prospecção foi bem sucedida em que, pelos testes realizados, o Ubuntu 12 foi selecionado para ser internalizado.");
		p.setProximaFase(FaseEnum.INTERNALIZACAO);
		p.setProximaFaseLider(usuario1);
		p.setProximaFaseUnidadeGestora(usuario1.getSetor());
		p.setProximaFaseJustificativa("Precisamos que este estudo seja prospectado com no mínimo 3 tecnologias.");		
		
		p = (Prospeccao)faseBC.salvar(p);
		
		List<Produto> produtos = produtoDAO.findAll();		
		for (Produto produto : produtos) {
			FaseProduto fp = new FaseProduto();
			fp.setFase(p);
			fp.setProduto(produto);
			faseProdutoDAO.insert(fp);
		}
		
		Internalizacao i = (Internalizacao)faseBC.finalizarFase(p);
		i.setCodigoReferencia("R175");
		i.setOrigemReferencia(origem);
		i.setObjetivo("Fornecer as bolas que já foram prospectadas com seus devidos condicionamentos");
		i.setDataRealizacao(new Date());
		i.setSituacao(Situacao.REPROVADO);	
		i.setRestricao(true);
		i.setRestricaoQuemPode("Apenas robgol pode usar este tipo de bola");
		i.setRestricaoJustificativa("Ele é o único com habilidade suficiente para isso.");		
		i.setAquisicaoNecessaria(0);
		i.setCapacitacao(false);		
		i.setAnaliseDeRiscos(false);
		i = (Internalizacao)faseBC.salvar(i);
				

		
		a = new Analise();		
		a.setCodigoReferencia("????????");
		a.setDataRealizacao(new Date());
		a.setDemandanteUnidade("CETEC/CTSDR");
		a.setDemandanteRepresentante(usuario1);
		a.setObjetivo("Nonono nononon ononononon ononononon onononono non");
		a.setUnidadeGestora("CTSDR");
		a.setOrigemReferencia(origem);
		a.setSituacao(Situacao.APROVADO);
		a.setSituacaoJustificativa("nonononono nonononono nononononon onononon on");		
		a = (Analise)faseBC.salvar(a);
		
		a = new Analise();		
		a.setCodigoReferencia("????????");
		a.setDataRealizacao(new Date());
		a.setDemandanteUnidade("CETEC/CTSDR");
		a.setDemandanteRepresentante(usuario1);
		a.setObjetivo("Nonono nononon ononononon ononononon onononono non");
		a.setUnidadeGestora("CTSDR");
		a.setOrigemReferencia(origem);
		a.setSituacao(Situacao.REPROVADO);
		a.setSituacaoJustificativa("nonononono nonononono nononononon onononon on");		
		a = (Analise)faseBC.salvar(a);
		
		a = new Analise();		
		a.setCodigoReferencia("????????");
		a.setDataRealizacao(new Date());
		a.setDemandanteUnidade("CETEC/CTSDR");
		a.setDemandanteRepresentante(usuario1);
		a.setObjetivo("Nonono nononon ononononon ononononon onononono non");
		a.setUnidadeGestora("CTSDR");
		a.setOrigemReferencia(origem);
		a.setSituacao(Situacao.RASCUNHO);		
		a = (Analise)faseBC.salvar(a);
		
	}
	
	
	@Startup @Priority(1)
	@Transactional
	public void criarOutras(){
				
		Licenciamento l = new Licenciamento();
		l.setNome("GPL");
		l.setDescricao("GPL");
		licenciamentoDAO.insert(l);
		
		l = new Licenciamento();
		l.setNome("LGPL");
		l.setDescricao("LGPL");
		licenciamentoDAO.insert(l);
		
		Fabricante fabricante = new Fabricante();
		fabricante.setNome("Arno");
		fabricante.setRepresentante("Thiago");
		fabricante.setTelefone("(75)9128-0410");
		fabricante.setEmail("mariano.thiago@gmail.com");
		fabricanteDAO.insert(fabricante);
		
		Fornecedor fornecedor = new Fornecedor();
		fornecedor.setNome("Mozilla");
		fornecedor.setRepresentante("Thiago");
		fornecedor.setTelefone("(75)9128-0410");
		fornecedor.setEmail("mariano.thiago@gmail.com");
		fornecedorDAO.insert(fornecedor);
		
		PlataformaTecnologica pl = new PlataformaTecnologica();
		pl.setNome("Ubuntu 12");
		pl.setDescricao("Plataforma 1");
		plataformaTecnologicaDAO.insert(pl);
		
		pl = new PlataformaTecnologica();
		pl.setNome("Windows 97");
		pl.setDescricao("Plataforma 2");
		plataformaTecnologicaDAO.insert(pl);
		
		pl = new PlataformaTecnologica();
		pl.setNome("MacOSX");
		pl.setDescricao("Plataforma 3");
		plataformaTecnologicaDAO.insert(pl);
		
		List<PlataformaTecnologica> listaPlataformas = new ArrayList<PlataformaTecnologica>();
		listaPlataformas = plataformaTecnologicaDAO.findAll();

		Produto produto = new Produto();
		produto.setDescricao("Melhor produto educativo dos ultimos 20 anos.");
		produto.setLicenciamento(l);
		produto.setFabricante(fabricante);
		produto.setFornecedor(null);
		produto.setPlataformasSuportadas(listaPlataformas);
		produto.setNome("Havaianas de Pau");
		produto.setLegado(false);
		produto.setVersao("2.0");
		produto.setRelease("2.0");
		produtoDAO.insert(produto);		
		
		produto = new Produto();
		produto.setDescricao("Maior trocador de porcaria via telefones modernos que tem internê");
		produto.setFabricante(fabricante);
		produto.setFornecedor(null);
		produto.setNome("Uotsap");
		produto.setLicenciamento(l);
		produto.setLegado(false);
		produto.setVersao("2.0");
		produto.setRelease("2.0");
		produtoDAO.insert(produto);
		
		
		Tecnologia tecnologia = new Tecnologia();
		tecnologia.setNome("Software");
		tecnologia.setDescricao("tecn de software");
		tecnologiaDAO.insert(tecnologia);
		
		
		Categoria categoria = new Categoria();
		categoria.setNome("Liguagem de programação");
		categoria.setDescricao("Categoriax");
		categoria.setTecnologia(tecnologia);
		categoriaDAO.insert(categoria);
		//produto.getCategorias().add(categoria);
		
		categoria = new Categoria();
		categoria.setNome("Banco de dados");
		categoria.setDescricao("Banco de dados");
		categoria.setTecnologia(tecnologia);
		categoriaDAO.insert(categoria);
		
		//produto.getCategorias().add(categoria);
		//produtoDAO.update(produto);
		
		OrigemDemanda origemDemanda = new OrigemDemanda();
		origemDemanda.setNome("SPECX");
		origemDemanda.setDescricao("SPECX...");
		origemDemandaDAO.insert(origemDemanda);
		
		origemDemanda = new OrigemDemanda();
		origemDemanda.setNome("ALM");
		origemDemanda.setDescricao("ALM...");
		origemDemandaDAO.insert(origemDemanda);
		
		origemDemanda = new OrigemDemanda();
		origemDemanda.setNome("Demandas RFB");
		origemDemanda.setDescricao("Demandas RFB...");
		origemDemandaDAO.insert(origemDemanda);
		
		origemDemanda = new OrigemDemanda();
		origemDemanda.setNome("Solicita");
		origemDemanda.setDescricao("Solicita...");
		origemDemandaDAO.insert(origemDemanda);
	}

	
	
}
