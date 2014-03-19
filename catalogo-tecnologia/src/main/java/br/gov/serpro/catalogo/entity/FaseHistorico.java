package br.gov.serpro.catalogo.entity;

import static javax.persistence.GenerationType.SEQUENCE;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.gov.frameworkdemoiselle.security.SecurityContext;
import br.gov.frameworkdemoiselle.util.Beans;

@Entity
public class FaseHistorico {
	
	public enum OPERACAO{CRIAR, ATUALIZAR, FINALIZAR, EXCLUIR}

	@Id
	@GeneratedValue(strategy = SEQUENCE)
	private Long id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	private Fase fase;
	
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	private User usuario;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	@Enumerated(EnumType.STRING)
	private OPERACAO operacao;
	
	private String descricao;
	
	@Transient
	private String horario;

	public FaseHistorico(){
		
	}
	
	public FaseHistorico(Fase fase, OPERACAO operacao) {
		this.usuario = (User)Beans.getReference(SecurityContext.class).getUser();
		this.fase = fase;
		this.operacao = operacao;
		this.data = new Date();
		this.descricao = gerarDescricao();
	}
	
	private String gerarDescricao(){
		StringBuffer sb = new StringBuffer();
		sb.append(getUsuario().getName()).append(" executou a ação: ").append(operacao.name());
		sb.append(" com a situação: ").append(fase.getSituacao().name());
		return sb.toString();
	}

	public String getHorario() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		return format.format(getData());
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Fase getFase() {
		return fase;
	}

	public void setFase(Fase fase) {
		this.fase = fase;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public OPERACAO getOperacao() {
		return operacao;
	}

	public void setOperacao(OPERACAO operacao) {
		this.operacao = operacao;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	
	
}
