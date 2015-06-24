package br.com.ia.ga.threads;
import br.com.ia.ga.Crossover;
import Jama.Matrix;

public class CrossoverThread extends Thread {
	private Matrix populacao;
	private double taxa_crossover;
	private int tipo_crossover;
	private boolean pais_sobrevivem;
	
	private Matrix nova_populacao;
	
	@Override
	public void run() {
		nova_populacao=Crossover.aplica_crossover(populacao, taxa_crossover, tipo_crossover, pais_sobrevivem);
	}
	
	public CrossoverThread(ThreadGroup grupo_thread, Matrix populacao, double taxa_crossover, int tipo_crossover, boolean pais_sobrevivem) {
		super(grupo_thread, "crossover thread");		
		this.populacao=populacao;
		this.taxa_crossover=taxa_crossover;
		this.tipo_crossover=tipo_crossover;
		this.pais_sobrevivem=pais_sobrevivem;
		
		this.nova_populacao=null;
	}
	
	public Matrix get_nova_populacao() {
		return nova_populacao;
	}
}
