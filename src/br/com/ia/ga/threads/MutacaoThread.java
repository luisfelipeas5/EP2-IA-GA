package br.com.ia.ga.threads;

import Jama.Matrix;
import br.com.ia.ga.Mutacao;

public class MutacaoThread extends Thread {
	private int tipo_mutacao;
	private double taxa_mutacao;
	private Matrix populacao;
	private Matrix nova_populacao;

	@Override
	public void run() {
		nova_populacao = Mutacao.aplica_mutacao(populacao, taxa_mutacao, tipo_mutacao);
	}
	
	public MutacaoThread(ThreadGroup mutacao_threads_grupo, Matrix populacao, double taxa_mutacao, int tipo_mutacao) {
		super(mutacao_threads_grupo, "muacao thread");
		this.taxa_mutacao=taxa_mutacao;
		this.populacao=populacao;
		this.tipo_mutacao=tipo_mutacao;
		
		this.nova_populacao=null;
	}
	
	public Matrix get_nova_populacao() {
		return nova_populacao;
	}
}
