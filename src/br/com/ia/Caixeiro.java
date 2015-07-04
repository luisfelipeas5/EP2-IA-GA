package br.com.ia;
import Jama.Matrix;
import br.com.ia.ga.AlgoritmoGenetico;
import br.com.ia.ga.Fitness;
import br.com.ia.ga.threads.AlgoritmoGeneticoThreads;

public class Caixeiro {
	public static void main(String[] args) {
		/*
		 *PARAMETROS via Linha de comando:
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
		
		int tamanho_populacao_inicial=100;
		double diversidade_minima=0;
		int numero_geracao_maximo=100000;
		//Parametros de selecao
		int numero_candidatos_crossover=tamanho_populacao_inicial; //Define-se quantos individuos no maximo tera a populacao de candidatos a crossover
		int quantidade_subpopulacao=10; //Quantidade dos melhores individuos que comporao a subpopulacao de candidatos
		//Parametros de crossover
		/*
		 * Tipos de Crossover
		 * 	0: crossover OX
		 * 	1: baseado em posicao
		 * 	2: baseado em ordem
		 */
		int tipo_crossover=1;
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
		int quantidade_cromossomo_nao_mutantes=5;
		//parametros para populacao aleatoria
		int tamanho_populacao_aleatoria=0;
		int geracao_populacao_aleatoria=Integer.MAX_VALUE;
		
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
		
		System.out.println("Parametros iniciais:");
		System.out.println("\tTaxa de crossover="+taxa_crossover);
		System.out.println("\tTaxa de mutacao="+taxa_mutacao);
		System.out.println("\tOperador de selecao="+selecao);
		System.out.println("\tOperador de crossover="+crossover);
		System.out.println("\tOperador de mutacao="+mutacao);
		System.out.println("\tTamanho da Populacao inicial="+tamanho_populacao_inicial);
		System.out.println("\tDiversidade minima="+diversidade_minima);
		System.out.println("\tNumero maximo de geracoes="+numero_geracao_maximo);
		System.out.println();
		
		//matriz de cada uma das cidades que irao compor o cromossomo
		Matrix cidades=Manipulador_Arquivo_Entrada.lee_arquivo(nome_arquivo);
		
		Matrix caminho=null;
		if(numero_threads==0) {
			caminho=AlgoritmoGenetico.get_melhor_caminho(cidades,
															tamanho_populacao_inicial, numero_geracao_maximo, diversidade_minima,
															operador_selecao, numero_candidatos_crossover, quantidade_subpopulacao,
															taxa_crossover, tipo_crossover, pais_sobrevivem, 
															taxa_mutacao, tipo_mutacao, quantidade_cromossomo_nao_mutantes
															);
		}else {
			caminho=AlgoritmoGeneticoThreads.get_melhor_caminho(cidades,
																	tamanho_populacao_inicial, numero_geracao_maximo, diversidade_minima,
																	operador_selecao, numero_candidatos_crossover, quantidade_subpopulacao,
																	taxa_crossover, tipo_crossover, pais_sobrevivem, 
																	taxa_mutacao, tipo_mutacao, quantidade_cromossomo_nao_mutantes,
																	geracao_populacao_aleatoria, tamanho_populacao_aleatoria,
																	numero_threads
																);
		}
		
		System.out.println("Parametros iniciais:");
		System.out.println("\tTaxa de crossover="+taxa_crossover);
		System.out.println("\tTaxa de mutacao="+taxa_mutacao);
		System.out.println("\tOperador de selecao="+selecao);
		System.out.println("\tOperador de crossover="+crossover);
		System.out.println("\tOperador de mutacao="+mutacao);
		System.out.println("\tTamanho da Populacao inicial="+tamanho_populacao_inicial);
		System.out.println("\tDiversidade minima="+diversidade_minima);
		System.out.println("\tNumero maximo de geracoes="+numero_geracao_maximo);
		System.out.println();
		
		System.out.print("\nMelhor Caminho (Solucao) encontrado:");
		caminho.print(0, 0);
		
		long tempo_final = System.currentTimeMillis();
		System.out.print("Duracao="+((tempo_final-tempo_inicial)/1000)+"s");
		System.out.print("="+((tempo_final-tempo_inicial)/60000)+"min");
		System.out.print("="+((tempo_final-tempo_inicial)/3600000)+"h");
		
		//Nome do arquivo de saida
		String nome_arquivo_saida="resultados/GA_"+numero_geracao_maximo+"_s"+selecao+"_c"+crossover+"_m"+mutacao+".txt";
		
		int tamanho_populacao_corrente=numero_candidatos_crossover+quantidade_cromossomo_nao_mutantes+quantidade_subpopulacao;
		if(pais_sobrevivem) {
			tamanho_populacao_corrente+=numero_candidatos_crossover;
		}
		
		String arquitetura="Parametros iniciais:"+
							"\n\tTaxa de crossover="+taxa_crossover+
							"\n\tTaxa de mutacao="+taxa_mutacao+
							"\n\tOperador de selecao="+selecao+
							"\n\tOperador de crossover="+crossover+
							"\n\tOperador de mutacao="+mutacao+
							"\n\tTamanho da Populacao inicial="+tamanho_populacao_inicial+
							"\n\tTamanho populacao corrente="+(tamanho_populacao_corrente)+
							"\n\tDiversidade minima="+diversidade_minima+
							"\n\tNumero maximo de geracoes="+numero_geracao_maximo+
							"\n\tDuracao="+((tempo_final-tempo_inicial)/1000)+"s"+
							"="+((tempo_final-tempo_inicial)/60000)+"min"+
							"="+((tempo_final-tempo_inicial)/3600000)+"h"+
							"\nMelhor Caminho:\n";
		for (int indice_cidade = 0; indice_cidade < caminho.getColumnDimension(); indice_cidade++) {
			arquitetura=arquitetura+" "+(int)(caminho.get(0, indice_cidade));
		}
		String fitness= Fitness.calcula_fitness(caminho, AlgoritmoGenetico.calcula_distancias(cidades)).get(0, 0)+"";
		arquitetura=arquitetura+"\n\tFitness="+fitness;
		Manipulador_Arquivo_Entrada.escreve_arquivo(nome_arquivo_saida, arquitetura);
	}
}
