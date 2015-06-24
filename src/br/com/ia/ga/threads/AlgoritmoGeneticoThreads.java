package br.com.ia.ga.threads;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;
import br.com.ia.ga.AlgoritmoGenetico;
import br.com.ia.ga.Fitness;
import br.com.ia.ga.Leitor_Arquivo_Entrada;
import br.com.ia.ga.Mutacao;
import br.com.ia.ga.Selecao;

public class AlgoritmoGeneticoThreads extends AlgoritmoGenetico{
	public static void main(String[] args) {
		/*
		 *PARÂMETROS:
		 *	- Taxa de crossover
		 *	- Taxa de mutação
		 *	- Operador de seleção
		 */
		double taxa_crossover=Double.parseDouble(args[0]);
		double taxa_mutacao=Double.parseDouble(args[1]);
		int operador_selecao=Integer.parseInt(args[2]);
		String nome_arquivo=args[3];
		
		long tempo_inicial = System.currentTimeMillis();
		
		int numero_threads=1;
		
		int tamanho_populacao_inicial=1000;
		double diversidade_minima=0;
		int numero_geracao_maximo=10000;
		//Parametros de selecao
		int numero_candidatos_crossover=1000; //Define-se quantos individuos no maximo tera a populacao de candidatos a crossover
		int quantidade_subpopulacao=20; //Quantidade dos melhores individuos que comporao a subpopulacao de candidatos
		//Parametros de crossover
		/*
		 * Tipos de Crossover
		 * 	0: crossover OX
		 */
		int tipo_crossover=0;
		boolean pais_sobrevivem=true;
		//Parametros da Mutacao
		/*
		 * Tipos de Mutação
		 *  0: mutacao inversiva
		 */
		int tipo_mutacao=1;
		int quantidade_individuos_nao_mutantes=5;
		
		System.out.println("Parametros iniciais:");
		System.out.println("\tTaxa de crossover="+taxa_crossover);
		System.out.println("\tTaxa de mutacao="+taxa_mutacao);
		System.out.println("\tOperador de selecao="+operador_selecao);
		System.out.println("\tTamanho da Populacao inicial="+tamanho_populacao_inicial);
		System.out.println("\tDiversidade minima="+diversidade_minima);
		System.out.println("\tNumero maximo de geracoes="+numero_geracao_maximo);
		System.out.println();
		
		//matriz de cada uma das cidades que irao compor o cromossomo
		Matrix cidades=Leitor_Arquivo_Entrada.lee_arquivo(nome_arquivo);
		
		//matriz que armazena as distancias entre cada uma das cidades
		Matrix distancias=calcula_distancias(cidades);
		
		//Cada linha da matriz representa um cromossomo; cada elemento uma cidade
		Matrix populacao=gera_populacao_aleatoria(cidades.getRowDimension(), tamanho_populacao_inicial);
		
		//Funcao de fitness calculada para saber a diversidade inicial
		Matrix fitness_inicial=Fitness.fitness(populacao, distancias);
		
		//calcula-se a diversidade da populacao inicial
		double diversidade=avalia(fitness_inicial)[2];
		
		for(int geracao_atual=0; geracao_atual<numero_geracao_maximo && diversidade>diversidade_minima;	geracao_atual++) {
			System.out.println("Geracao "+(geracao_atual+1));
			
			System.out.println("\t\tTamanho da populacao="+populacao.getRowDimension());
			
			System.out.print("\t\tCalculando fitness...");
			//Calcula o fitness da nova geracao
			Matrix fitness_populacao=Fitness.fitness(populacao, distancias);
			System.out.println("calculado!");
			
			System.out.print("\t\tSelecionando candidatos...");
			//Gera uma nova populacao com os canditados a crossover
			Matrix subpopulacao=Selecao.seleciona_melhores_individuos(populacao, fitness_populacao, quantidade_subpopulacao);
			Matrix populacao_selecionada=Selecao.seleciona_candidatos(populacao,fitness_populacao, operador_selecao, 
																	numero_candidatos_crossover);
			
			Matrix nova_populacao=new Matrix(0, populacao.getColumnDimension());
			nova_populacao=JamaUtils.rowAppend(nova_populacao, populacao_selecionada);
			nova_populacao=JamaUtils.rowAppend(nova_populacao, subpopulacao);
			System.out.println("selecionados!");
			
			System.out.print("\t\tAplicando crossover...");
			//Aplica o crossover na nova populacao gerada na selecao
			Matrix populacao_crossover=new Matrix(0, populacao.getColumnDimension());
			if(pais_sobrevivem) {
				populacao_crossover=nova_populacao.getMatrix(0, nova_populacao.getRowDimension()-1, 0, nova_populacao.getColumnDimension()-1);
			}
			//Cria vetor com as novas threads que faram o crossover
			ThreadGroup crossover_threads_grupo=new ThreadGroup("CrossoverThreads");
			CrossoverThread[] crossover_threads=new CrossoverThread[numero_threads];
			int numero_cromossomo_cada_thread=nova_populacao.getRowDimension()/numero_threads;
			for (int indice_nova_thread = 0; indice_nova_thread < numero_threads; indice_nova_thread++) {
				Matrix candidatos_crossover_thread=nova_populacao.getMatrix(numero_cromossomo_cada_thread*indice_nova_thread,
																	numero_cromossomo_cada_thread*indice_nova_thread+numero_cromossomo_cada_thread-1,
																	0, nova_populacao.getColumnDimension()-1);
						
				CrossoverThread nova_crossover_thread=new CrossoverThread(crossover_threads_grupo, candidatos_crossover_thread, taxa_crossover, tipo_crossover, false);
				nova_crossover_thread.start();
				
				crossover_threads[indice_nova_thread]=nova_crossover_thread;
			}
			
			//Esperar Threads de Crossover terminarem
			while(crossover_threads_grupo.activeCount()>0) {
				//System.out.println("\t\t\tEsperando threads");
			}
			
			for (int indice_thread = 0; indice_thread < crossover_threads.length; indice_thread++) {
				Matrix populacao_crossover_thread=crossover_threads[indice_thread].get_nova_populacao();
				populacao_crossover=JamaUtils.rowAppend(populacao_crossover, populacao_crossover_thread);
			}
			nova_populacao=populacao_crossover;
			crossover_threads_grupo.destroy();
			System.out.println("aplicado!");
			
			//Aplica a mutacao na nova populacao gerada pelo crossover
			System.out.print("\t\tAplicando mutacao...");
			fitness_populacao=Fitness.fitness(nova_populacao, distancias);
			Matrix populacao_nao_mutante=Selecao.seleciona_melhores_individuos(nova_populacao, fitness_populacao, quantidade_individuos_nao_mutantes);
			
			Matrix populacao_mutante=nova_populacao.getMatrix(0, nova_populacao.getRowDimension()-1, 0, nova_populacao.getColumnDimension()-1);
			populacao_mutante=Mutacao.aplica_mutacao(populacao_mutante, taxa_mutacao,tipo_mutacao);
			
			nova_populacao=new Matrix(0, populacao.getColumnDimension());
			nova_populacao=JamaUtils.rowAppend(nova_populacao, populacao_nao_mutante);
			nova_populacao=JamaUtils.rowAppend(nova_populacao, populacao_mutante);
			System.out.println("aplicada!");
			
			//Define a nova populacao depois da mutacao como a populacao definitiva
			populacao=nova_populacao;
			
			//Armazena os valores de melhor fitness, media dos fitness e a diversidade dos individuos
			double[] medidas_avaliacao = avalia(fitness_populacao); 
			diversidade=medidas_avaliacao[2];
			
			System.out.print("\tmelhor fitness="+medidas_avaliacao[0]+
							" media="+medidas_avaliacao[1]+
							" diversidade="+medidas_avaliacao[2]+"\n");
		}
		long tempo_final = System.currentTimeMillis();
		System.out.println("Duracao="+(tempo_final-tempo_inicial)/1000);
		
	}
}
