package br.com.ia;
import Jama.Matrix;
import br.com.ia.ga.AlgoritmoGenetico;
import br.com.ia.ga.threads.AlgoritmoGeneticoThreads;

public class Caixeiro {
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
		
		System.out.print("\nMelhor Caminho (Solucao) encontrado:");
		caminho.print(0, 0);
		
		long tempo_final = System.currentTimeMillis();
		System.out.print("Duracao="+((tempo_final-tempo_inicial)/1000)+"s");
		System.out.print("="+((tempo_final-tempo_inicial)/60000)+"min");
		System.out.print("="+((tempo_final-tempo_inicial)/3600000)+"h");
		
	}
}
