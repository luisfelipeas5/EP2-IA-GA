package br.com.ia.ga;
import java.util.Random;

import br.com.ia.Grafico_Dinamico;
import br.com.ia.Leitor_Arquivo_Entrada;
import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;

public class AlgoritmoGenetico {
	
	public static Matrix get_melhor_caminho(Matrix cidades,
											int tamanho_populacao_inicial, int numero_geracao_maximo, double diversidade_minima,
											int operador_selecao, int numero_candidatos_crossover, int quantidade_subpopulacao,
											double taxa_crossover, int tipo_crossover, boolean pais_sobrevivem, 
											double taxa_mutacao, int tipo_mutacao, int quantidade_cromossomo_nao_mutantes
											) {
		Matrix melhor_cromossomo=null;
		
		//matriz que armazena as distancias entre cada uma das cidades
		Matrix distancias=AlgoritmoGenetico.calcula_distancias(cidades);
		
		//Cada linha da matriz representa um cromossomo; cada elemento uma cidade
		Matrix populacao=AlgoritmoGenetico.gera_populacao_aleatoria(cidades.getRowDimension(), tamanho_populacao_inicial);
		
		//Funcao de fitness calculada para saber a diversidade inicial
		Matrix fitness_inicial=Fitness.calcula_fitness(populacao, distancias);
		
		//calcula-se a diversidade da populacao inicial
		double diversidade=avalia(fitness_inicial)[2];
		
		Grafico_Dinamico grafico_Dinamico = new Grafico_Dinamico("Melhor e média fitness", numero_geracao_maximo);
		
		for(int geracao_atual=0; geracao_atual<numero_geracao_maximo && diversidade>diversidade_minima;	geracao_atual++) {
			System.out.println("Geracao "+(geracao_atual+1));
			
			System.out.println("\t\tTamanho da populacao="+populacao.getRowDimension());
			
			System.out.print("\t\tCalculando fitness...");
			//Calcula o fitness da nova geracao
			Matrix fitness_populacao=Fitness.calcula_fitness(populacao, distancias);
			System.out.println("calculado!");
			
			System.out.println("\t\tSelecionando candidatos...");
			//Gera uma nova populacao com os canditados a crossover
			Matrix subpopulacao=Selecao.seleciona_melhores_individuos(populacao, fitness_populacao, quantidade_subpopulacao);
			Matrix populacao_selecionada=Selecao.seleciona_candidatos(populacao,fitness_populacao, operador_selecao, 
																	numero_candidatos_crossover);
			
			Matrix nova_populacao=new Matrix(0, populacao.getColumnDimension());
			nova_populacao=JamaUtils.rowAppend(nova_populacao, populacao_selecionada);
			nova_populacao=JamaUtils.rowAppend(nova_populacao, subpopulacao);
			System.out.println("\t\tselecionados!");
			
			System.out.print("\t\tAplicando crossover...");
			//Aplica o crossover na nova populacao gerada na selecao
			nova_populacao=Crossover.aplica_crossover(nova_populacao, taxa_crossover,tipo_crossover, pais_sobrevivem);
			System.out.println("aplicado!");
			
			//Aplica a mutacao na nova populacao gerada pelo crossover
			System.out.print("\t\tAplicando mutacao...");
			fitness_populacao=Fitness.calcula_fitness(nova_populacao, distancias);
			Matrix populacao_nao_mutante=Selecao.seleciona_melhores_individuos(nova_populacao, fitness_populacao, quantidade_cromossomo_nao_mutantes);
			
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
			
			grafico_Dinamico.adicionar_ponto(geracao_atual, medidas_avaliacao[0], geracao_atual, medidas_avaliacao[1]);
		}
		Matrix fitness_final = Fitness.calcula_fitness(populacao, distancias);
		melhor_cromossomo=Selecao.seleciona_melhores_individuos(populacao, fitness_final, 1);
		
		return melhor_cromossomo;
	}
	
	
	 /*
	  * Calcula a distancia de todas as cidades para todas as cidades
	  */
	public static Matrix calcula_distancias(Matrix cidades) {
		Matrix distancias=new Matrix(cidades.getRowDimension(), cidades.getRowDimension());
		
		for (int indice_cidade_x = 0; indice_cidade_x < cidades.getRowDimension(); indice_cidade_x++) {
			for (int indice_cidade_y = indice_cidade_x+1; indice_cidade_y < cidades.getRowDimension(); indice_cidade_y++) {
				//Armazena as coordenadas das cidades
				Matrix cidade_atual=JamaUtils.getrow(cidades, indice_cidade_x);
				Matrix cidade_proxima=JamaUtils.getrow(cidades, indice_cidade_y);
				//calcula a distancia entre as cidades
				double distancia=distancia_euclidiana(cidade_atual, cidade_proxima);
				//armazena no vetor distancia
				distancias.set(indice_cidade_x, indice_cidade_y, distancia);
				distancias.set(indice_cidade_y, indice_cidade_x, distancia);
			}
		}
		return distancias;
	}

	/*
	 * Diversidade calculada a partir da distancia media entre
	 * os valores passados em uma matriz de fitness
	 * Retorna um aray de double, onde a posicao:
	 * 0- melhor fitness
	 * 1- media dos fitness
	 * 2- diversidade
	 */
	public static double[] avalia(Matrix fitness) {
		double[] avaliacao=new double[3];
		
		double diversidade=0;
		
		//Melhor fitness
		double melhor_fitness=JamaUtils.getMax(fitness);
		double soma_fitness=0;
		//Comparar a media dos fitness e calcular a distancia com o maior fitness
		for (int indice_fitness = 0; indice_fitness < fitness.getRowDimension(); indice_fitness++) {
			soma_fitness+=fitness.get(indice_fitness, 0);
		}
		double media_fitness=soma_fitness/fitness.getRowDimension();
		diversidade = (melhor_fitness-media_fitness);
		
		avaliacao[0]=melhor_fitness;
		avaliacao[1]=media_fitness;
		avaliacao[2]=diversidade;
		return avaliacao;
	}

	/*
	 * As matrizes que representam a cidade devem ser matrizes linhas com o mesmo numero de colunas.
	 * O calculo da distancia euclidiana entre duas cidades eh feito:
	 * Dada as cidades P=(p1,p2,...pn) e Q=(q1,q2,...qn),
	 * d= ( (p1-q1)^2 + (p2-q2)^2 +...+ (pn-qn)^2 )^(1/(n)), sendo
	 * n=numero de dimensoes
	 */
	public static double distancia_euclidiana(Matrix cidade_atual,	Matrix cidade_proxima) {
		double distancia_euclidiana=0;
		int numero_dimensoes=cidade_atual.getColumnDimension();
		for (int dimensao = 0; dimensao < numero_dimensoes; dimensao++) {
			double p=cidade_atual.get(0, dimensao);
			double q=cidade_proxima.get(0, dimensao);
			
			distancia_euclidiana+= (p-q)*(p-q);
		}
		distancia_euclidiana=Math.pow(distancia_euclidiana, 1.0/numero_dimensoes );
		
		return distancia_euclidiana;
	}

	public static Matrix gera_populacao_aleatoria(int quant_cidades, int tamanho_populacao_inicial) {
		Matrix populacao=new Matrix( 0, quant_cidades);
		
		Random random=new Random();
		for(int indice_cromossomo=0; indice_cromossomo<tamanho_populacao_inicial; indice_cromossomo++) {
			//Se a cidade i for incluida no cromossomo, cidade_incluida_cromossomo[i]=1, se não igual a 0;
			int[] cidade_incluida_cromossomo=new int[quant_cidades];
			
			Matrix novo_cromossomo=new Matrix(1,0);
			for(int quant_cidades_incluidas=0; quant_cidades_incluidas<quant_cidades; quant_cidades_incluidas++) {
				int indice_cidade=(Math.abs(random.nextInt())%quant_cidades);
				int cidade=indice_cidade+1;//escolhe randomicamente uma cidade para incluir
				/*
				 * se essa cidade ja for incluida,
				 * busca sequencial para achar a proxima cidade que nao foi escolhida ainda
				 */
				while(cidade_incluida_cromossomo[indice_cidade]==1 ) {
					indice_cidade=(indice_cidade+1)%quant_cidades;
					cidade=indice_cidade+1;
				}
				Matrix cidade_matrix=new Matrix(1,1);
				cidade_matrix.set(0, 0, cidade);
				novo_cromossomo=JamaUtils.columnAppend(novo_cromossomo, cidade_matrix);
				cidade_incluida_cromossomo[indice_cidade]=1;
				
			}
			populacao=JamaUtils.rowAppend(populacao, novo_cromossomo);
		}
		return populacao;
	}
	
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
		
		int tamanho_populacao_inicial=200;
		double diversidade_minima=0;
		int numero_geracao_maximo=100;
		//Parametros de selecao
		int numero_candidatos_crossover=200; //Define-se quantos individuos no maximo tera a populacao de candidatos a crossover
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
		int tipo_mutacao=2;
		int quantidade_cromossomo_nao_mutantes=5;
		
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
		
		get_melhor_caminho(cidades,
				tamanho_populacao_inicial, numero_geracao_maximo, diversidade_minima,
				operador_selecao, numero_candidatos_crossover, quantidade_subpopulacao,
				taxa_crossover, tipo_crossover, pais_sobrevivem,
				taxa_mutacao, tipo_mutacao, quantidade_cromossomo_nao_mutantes);
	}
}
