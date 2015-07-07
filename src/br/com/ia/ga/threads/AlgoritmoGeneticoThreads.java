package br.com.ia.ga.threads;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;
import br.com.ia.Grafico_Dinamico;
import br.com.ia.Manipulador_Arquivo_Entrada;
import br.com.ia.ga.AlgoritmoGenetico;
import br.com.ia.ga.Fitness;
import br.com.ia.ga.Selecao;

public class AlgoritmoGeneticoThreads extends AlgoritmoGenetico{
	public static Matrix get_melhor_caminho(Matrix cidades,
												int tamanho_populacao_inicial, int numero_geracao_maximo,
												double diversidade_minima, int operador_selecao,
												int numero_candidatos_crossover, int quantidade_subpopulacao,
												double taxa_crossover, int tipo_crossover, boolean pais_sobrevivem,
												double taxa_mutacao, int tipo_mutacao,
												int quantidade_cromossomo_nao_mutantes,
												int geracao_populacao_aleatoria, int tamanho_populacao_aleatoria, 
												int numero_threads) {
		//Matriz que armazena o melhor cromossomo adquirido em cada uma das épocas
		Matrix melhores_cromossomos=new Matrix(0,cidades.getRowDimension());
		
		//matriz que armazena as distancias entre cada uma das cidades
		Matrix distancias=calcula_distancias(cidades);
		
		//Cada linha da matriz representa um cromossomo; cada elemento uma cidade
		Matrix populacao=gera_populacao_aleatoria(cidades.getRowDimension(), tamanho_populacao_inicial);
		
		//Funcao de fitness calculada para saber a diversidade inicial
		Matrix fitness_inicial=Fitness.calcula_fitness(populacao, distancias);
		
		//calcula-se a diversidade da populacao inicial
		double diversidade=avalia(fitness_inicial)[2];
		
		//Para definir o titulo do grafico
		String selecao="";
		if(operador_selecao==0) selecao="Roleta";
		else if(operador_selecao==1) selecao="Torneio";
		
		String crossover="";
		if(tipo_crossover==0) crossover="OX";
		else if(tipo_crossover==1) crossover="Baseado em posicao";
		else if(tipo_crossover==2) crossover="Baseado em ordem";
		
		String mutacao="";
		if(tipo_mutacao==0) mutacao="Simples";
		else if(tipo_mutacao==1) mutacao="Alternativa";
		else if(tipo_mutacao==2) mutacao="Inversivel";
		//Titulo do grafico
		String titulo_grafico="resultados/GA_"+numero_geracao_maximo+"_s"+selecao+"_c"+crossover+"_m"+mutacao;
		//Grafico para mostrar a evolucao do melhor fitness e do fitness medio
		Grafico_Dinamico grafico_Dinamico = new Grafico_Dinamico("Melhor e média fitness "+titulo_grafico, numero_geracao_maximo);
		
		for(int geracao_atual=0; geracao_atual<numero_geracao_maximo && diversidade>diversidade_minima;	geracao_atual++) {
			System.out.println("Geracao "+(geracao_atual+1));
			
			/*
			 * Inclui populacao aleatoria e tamanho n a cada m geracoes
			 * n=tamanho_populacao_aleatoria
			 * m=geracao_populacao_aleatoria
			 */
			if(geracao_atual%geracao_populacao_aleatoria==0 && geracao_atual!=0) {
				Matrix populaca_aleatoria=gera_populacao_aleatoria(populacao.getColumnDimension(), tamanho_populacao_aleatoria);
				populacao=JamaUtils.rowAppend(populacao, populaca_aleatoria);
			}
			
			//System.out.println("\t\tTamanho da populacao="+populacao.getRowDimension());
			
			//System.out.print("\t\tCalculando fitness...");
			//Calcula o fitness da nova geracao
			Matrix fitness_populacao=Fitness.calcula_fitness(populacao, distancias);
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
			fitness_populacao=Fitness.calcula_fitness(nova_populacao, distancias);
			Matrix populacao_nao_mutante=Selecao.seleciona_melhores_individuos(nova_populacao, fitness_populacao, quantidade_cromossomo_nao_mutantes);
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
			
			grafico_Dinamico.adicionar_ponto(geracao_atual, medidas_avaliacao[0],
					geracao_atual, medidas_avaliacao[1]);
			
			Matrix fitness_final = Fitness.calcula_fitness(populacao, distancias);
			Matrix melhor_cromossomo=Selecao.seleciona_melhores_individuos(populacao, fitness_final, 1);
			melhores_cromossomos=JamaUtils.rowAppend(melhores_cromossomos, melhor_cromossomo);
		}
		return melhores_cromossomos;
	}
	
	public static void main(String[] args) {
		/*
		 *PARAMETROS:
		 *	- Taxa de crossover
		 *	- Taxa de mutacao
		 *	- Operador de selecao
		 */
		double taxa_crossover=Double.parseDouble(args[0]);
		double taxa_mutacao=Double.parseDouble(args[1]);
		int operador_selecao=Integer.parseInt(args[2]);
		String nome_arquivo=args[3];
		
		long tempo_inicial = System.currentTimeMillis();
		
		int numero_threads=4;
		
		int tamanho_populacao_inicial=200;
		double diversidade_minima=0;
		int numero_geracao_maximo=100;
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
		 *  0: mutacao simples
		 *  1: mutacao alternativa
		 *  2: variacao da mutacao inversivivel
		 *  3: mutacao inversivivel #nao implementada
		 */
		int tipo_mutacao=1;
		int quantidade_cromossomo_nao_mutantes=1;
		
		//parametros para populacao aleatoria
		int tamanho_populacao_aleatoria=0;
		int geracao_populacao_aleatoria=Integer.MAX_VALUE;
		
		System.out.println("Parametros iniciais:");
		System.out.println("\tTaxa de crossover="+taxa_crossover);
		System.out.println("\tTaxa de mutacao="+taxa_mutacao);
		System.out.println("\tOperador de selecao="+operador_selecao);
		System.out.println("\tTamanho da Populacao inicial="+tamanho_populacao_inicial);
		System.out.println("\tDiversidade minima="+diversidade_minima);
		System.out.println("\tNumero maximo de geracoes="+numero_geracao_maximo);
		System.out.println();
		
		//matriz de cada uma das cidades que irao compor o cromossomo
		Matrix cidades=Manipulador_Arquivo_Entrada.lee_arquivo(nome_arquivo);
		
		Matrix melhor_cromossomo=get_melhor_caminho(cidades,
													tamanho_populacao_inicial, numero_geracao_maximo, diversidade_minima,
													operador_selecao, numero_candidatos_crossover, quantidade_subpopulacao,
													taxa_crossover, tipo_crossover, pais_sobrevivem,
													taxa_mutacao, tipo_mutacao, quantidade_cromossomo_nao_mutantes,
													geracao_populacao_aleatoria, tamanho_populacao_aleatoria,
													numero_threads);
		
		System.out.print("\nMelhor Caminho (Solucao) encontrado:");
		melhor_cromossomo.print(0, 0);
		
		long tempo_final = System.currentTimeMillis();
		System.out.print("Duracao="+((tempo_final-tempo_inicial)/1000)+"s");
		System.out.print("="+((tempo_final-tempo_inicial)/60000)+"min");
		System.out.print("="+((tempo_final-tempo_inicial)/3600000)+"h");
	}
}
