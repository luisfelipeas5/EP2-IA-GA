package br.com.ia.ga.threads;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;
import br.com.ia.ga.AlgoritmoGenetico;
import br.com.ia.ga.Fitness;
import br.com.ia.ga.Leitor_Arquivo_Entrada;
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
		
		int numero_threads=4;
		
		int tamanho_populacao_inicial=200;
		double diversidade_minima=0;
		int numero_geracao_maximo=10000;
		//Parametros de selecao
		int numero_candidatos_crossover=200; //Define-se quantos individuos no maximo tera a populacao de candidatos a crossover
		int quantidade_subpopulacao=10; //Quantidade dos melhores individuos que comporao a subpopulacao de candidatos
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
		int tipo_mutacao=0;
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
			
			//System.out.println("\t\tTamanho da populacao="+populacao.getRowDimension());
			
			//System.out.print("\t\tCalculando fitness...");
			//Calcula o fitness da nova geracao
			Matrix fitness_populacao=Fitness.fitness(populacao, distancias);
			//System.out.println("calculado!");
			
			//System.out.print("\t\tSelecionando candidatos...");
			//Gera uma nova populacao com os canditados a crossover
			Matrix subpopulacao=Selecao.seleciona_melhores_individuos(populacao, fitness_populacao, quantidade_subpopulacao);
			Matrix populacao_selecionada=Selecao.seleciona_candidatos(populacao,fitness_populacao, operador_selecao, 
																	numero_candidatos_crossover);
			
			Matrix nova_populacao=new Matrix(0, populacao.getColumnDimension());
			nova_populacao=JamaUtils.rowAppend(nova_populacao, populacao_selecionada);
			nova_populacao=JamaUtils.rowAppend(nova_populacao, subpopulacao);
			//System.out.println("selecionados!");
			
			//Aplica o crossover na nova populacao gerada na selecao
			//System.out.print("\t\tAplicando crossover...");
			//Declara a populacao que contera os cromossomos apos o crossover
			Matrix populacao_crossover=new Matrix(0, populacao.getColumnDimension());
			if(pais_sobrevivem) { //Caso opte por manter os pais na populacao,
				//Inclui cromossomos da populacao que passaram pela selecao, a nova populacao do crossover
				populacao_crossover=nova_populacao.getMatrix(0, nova_populacao.getRowDimension()-1, 0, nova_populacao.getColumnDimension()-1);
			}
			//Cria vetor com as novas threads que farao o crossover
			ThreadGroup crossover_threads_grupo=new ThreadGroup("CrossoverThreads");
			CrossoverThread[] crossover_threads=new CrossoverThread[numero_threads];
			//Define o numero de cromossomos que cada thread fara o crossover
			int numero_cromossomo_cada_thread=nova_populacao.getRowDimension()/numero_threads;
			///Criacao das threads
			for (int indice_nova_thread = 0; indice_nova_thread < numero_threads; indice_nova_thread++) {
				//Armazena os cromossomos que farao o crossover na thread em questao
				Matrix candidatos_crossover_thread=nova_populacao.getMatrix(numero_cromossomo_cada_thread*indice_nova_thread,
																	numero_cromossomo_cada_thread*indice_nova_thread+numero_cromossomo_cada_thread-1,
																	0, nova_populacao.getColumnDimension()-1);
				//Criar nova thread, passando a populacao que foi separada para ela		
				CrossoverThread nova_crossover_thread=new CrossoverThread(crossover_threads_grupo, candidatos_crossover_thread, taxa_crossover, tipo_crossover, false);
				nova_crossover_thread.start();
				//Adiciona thread ao vetor das threads do crossover para controle futuro
				crossover_threads[indice_nova_thread]=nova_crossover_thread;
			}
			//Esperar Threads de Crossover terminarem
			while(crossover_threads_grupo.activeCount()>0) {
				//System.out.println("\t\t\tEsperando threads");
			}
			//Adiciona as populacoes que cada uma das threads de crossover resultaram a populacao dos cromossomos que sofreram crossover
			for (int indice_thread = 0; indice_thread < crossover_threads.length; indice_thread++) {
				Matrix populacao_crossover_thread=crossover_threads[indice_thread].get_nova_populacao();
				populacao_crossover=JamaUtils.rowAppend(populacao_crossover, populacao_crossover_thread);
			}
			//Define a nova populacao como a populacao que tem os cromossomos resultantes do crossover
			nova_populacao=populacao_crossover;
			crossover_threads_grupo.destroy(); //Destroi grupo de threads do crossover
			//System.out.println("aplicado!");
			
			//Aplica a mutacao na nova populacao gerada pelo crossover
			//System.out.print("\t\tAplicando mutacao...");
			//Separa os n melhores individuos que nao sofreram mutacao. n=quantidade_individuos_nao_mutantes
			fitness_populacao=Fitness.fitness(nova_populacao, distancias);
			Matrix populacao_nao_mutante=Selecao.seleciona_melhores_individuos(nova_populacao, fitness_populacao, quantidade_individuos_nao_mutantes);
			//Declara a a matrix que armazenara os cromossomos mutantes
			Matrix populacao_mutante=new Matrix(0, populacao.getColumnDimension());
			//Cria vetor com as novas threads que faram as mutacoes
			ThreadGroup mutacao_threads_grupo=new ThreadGroup("MutacaoThreads");
			MutacaoThread[] mutacao_threads=new MutacaoThread[numero_threads];
			numero_cromossomo_cada_thread=nova_populacao.getRowDimension()/numero_threads;
			for (int indice_nova_thread = 0; indice_nova_thread < numero_threads; indice_nova_thread++) {
				//Separa a populacao para a thread realizar a mutacao
				Matrix populacao_mutante_thread=nova_populacao.getMatrix(numero_cromossomo_cada_thread*indice_nova_thread,
																	numero_cromossomo_cada_thread*indice_nova_thread+numero_cromossomo_cada_thread-1,
																	0, nova_populacao.getColumnDimension()-1);
				//Cria a thread de mutacao
				MutacaoThread nova_mutacao_thread=new MutacaoThread(mutacao_threads_grupo, populacao_mutante_thread, taxa_mutacao, tipo_mutacao);
				//Comeca a execuacao da thread de mutacao
				nova_mutacao_thread.start();
				//Adiciona a thread no vetor de threads para controle futuro
				mutacao_threads[indice_nova_thread]=nova_mutacao_thread;
			}
			//Esperar Threads de Mutacao terminarem
			while(mutacao_threads_grupo.activeCount()>0) {
				//System.out.println("\t\t\tEsperando threads");
			}
			//Adicionar novas populacoes da thread na populacao mutante
			for (int indice_thread = 0; indice_thread < crossover_threads.length; indice_thread++) {
				Matrix populacao_mutacao_thread=mutacao_threads[indice_thread].get_nova_populacao();
				populacao_mutante=JamaUtils.rowAppend(populacao_mutante, populacao_mutacao_thread);
			}
			mutacao_threads_grupo.destroy(); //Destroi grupo das threads de mutacao
			//Cria a nova populacao= (populacao_nao_mutante)+(populacao_mutante)
			nova_populacao=new Matrix(0, populacao.getColumnDimension());
			nova_populacao=JamaUtils.rowAppend(nova_populacao, populacao_nao_mutante);
			nova_populacao=JamaUtils.rowAppend(nova_populacao, populacao_mutante);
			//System.out.println("aplicada!");
			
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
