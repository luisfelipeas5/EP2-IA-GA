import java.util.Random;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;

public class AlgoritmoGenetico {
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
		int numero_geracao_maximo=10000;
		int tipo_crossover=0;
		boolean incluir_pais_nova_populacao=true;
		
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
		Matrix populacao=gera_populacao_inicial(cidades.getRowDimension(), tamanho_populacao_inicial);
		
		//Funcao de fitness calculada para saber a diversidade inicial
		Matrix fitness_inicial=fitness(populacao, distancias);
		
		//calcula-se a diversidade da populacao inicial
		double diversidade=avalia(fitness_inicial)[2];
		
		for(int geracao_atual=0; geracao_atual<numero_geracao_maximo && diversidade>diversidade_minima;	geracao_atual++) {
			System.out.print("\t\tCalculando fitness...");
			//Calcula o fitness da nova geracao
			Matrix fitness_populacao=fitness(populacao, distancias);
			System.out.println("calculado!");
			
			System.out.print("\t\tSelecionando candidatos...");
			//Gera uma nova populacao com os canditados a crossover
			Matrix nova_populacao=Selecao.seleciona_candidatos(populacao,fitness_populacao, operador_selecao);
			System.out.println("selecionados!");
			
			System.out.print("\t\tAplicando crossover...");
			//Aplica o crossover na nova populacao gerada na selecao
			nova_populacao=Crossover.aplica_crossover(nova_populacao, taxa_crossover,tipo_crossover, incluir_pais_nova_populacao);
			System.out.println("aplicado!");
			//Aplica a mutacao na nova populacao gerada pelo crossover 
			//nova_populacao=Mutacao.aplica_mutacao(nova_populacao, taxa_mutacao, tipo_mutacao);
			
			//Define a nova populacao depois da mutacao como a populacao definitiva
			populacao=nova_populacao;
			
			//Armazena os valores de melhor fitness, media dos fitness e a diversidade dos individuos
			double[] medidas_avaliacao = avalia(fitness_populacao); 
			diversidade=medidas_avaliacao[2];
			
			System.out.println("Geracao "+geracao_atual);
			System.out.print("\tmelhor fitness="+medidas_avaliacao[0]+
							" media="+medidas_avaliacao[1]+
							" diversidade="+medidas_avaliacao[2]+"\n");
		}
		
	}
	 /*
	  * Calcula a distancia de todas as cidades para todas as cidades
	  */
	private static Matrix calcula_distancias(Matrix cidades) {
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
	private static double[] avalia(Matrix fitness) {
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

	private static Matrix fitness(Matrix populacao, Matrix distancias_entre_cidades) {
		Matrix fitness = new Matrix(populacao.getRowDimension(), 1);
		//calcular o fitness de cada um dos cromossomos da populacao
		for (int indice_cromossomo = 0; indice_cromossomo < populacao.getRowDimension(); indice_cromossomo++) {
			//Cromossomo do calculo do fitness
			Matrix cromossomo=JamaUtils.getrow(populacao, indice_cromossomo);
			double distancia_total=0;
			//Somar as distancias entre as cidades
			for (int indice_cidade_cromossomo = 0; indice_cidade_cromossomo < cromossomo.getColumnDimension(); indice_cidade_cromossomo++) {
				int indice_cidade=(((int)cromossomo.get(0, indice_cidade_cromossomo))-1);
				int indice_cidade_proxima=(((int)cromossomo.get(0, (indice_cidade_cromossomo+1)%cromossomo.getColumnDimension() ))-1);
				
				double distancia_da_proxima_cidade= distancias_entre_cidades.get(indice_cidade, indice_cidade_proxima);
				distancia_total+=distancia_da_proxima_cidade;
			}
			
			//Adicionar fitness desse cromossomo na matriz de fitness
			/*
			 * Problema de maximizacao de fitness e minimazacao da distancia,
			 * portanto: quanto maior distancia percorrida, menor o fitness
			 */
			double fitness_cromossomo= 1/distancia_total;
			
			fitness.set(indice_cromossomo, 0, fitness_cromossomo);
		}
		return fitness;
	}
	
	/*
	 * As matrizes que representam a cidade devem ser matrizes linhas com o mesmo numero de colunas.
	 * O calculo da distancia euclidiana entre duas cidades eh feito:
	 * Dada as cidades P=(p1,p2,...pn) e Q=(q1,q2,...qn),
	 * d= ( (p1-q1)^2 + (p2-q2)^2 +...+ (pn-qn)^2 )^(1/(n)), sendo
	 * n=numero de dimensoes
	 */
	private static double distancia_euclidiana(Matrix cidade_atual,	Matrix cidade_proxima) {
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

	private static Matrix gera_populacao_inicial(int quant_cidades, int tamanho_populacao_inicial) {
		Matrix populacao=new Matrix( tamanho_populacao_inicial, quant_cidades);
		
		Random random=new Random();
		for(int indice_cromossomo=0; indice_cromossomo<populacao.getRowDimension(); indice_cromossomo++) {
			//Se a cidade i for incluida no cromossomo, cidade_incluida_cromossomo[i]=1, se não igual a 0;
			int[] cidade_incluida_cromossomo=new int[quant_cidades];
			int quantidade_cidades_incluidas=0;
			
			for(int quant_cidades_incluidas=0; quant_cidades_incluidas<quant_cidades; quant_cidades_incluidas++) {
				int cidade=(Math.abs(random.nextInt())%quant_cidades);//escolhe randomicamente uma cidade para incluir
				/*
				 * se essa cidade ja for incluida,
				 * busca sequencial para achar a proxima cidade que nao foi escolhida ainda
				 */
				while(cidade_incluida_cromossomo[cidade]==1 ) {
					cidade=((cidade+1)%quant_cidades);
				}
				populacao.set(indice_cromossomo, quantidade_cidades_incluidas, cidade+1);
				cidade_incluida_cromossomo[cidade]=1;
				
				quantidade_cidades_incluidas++;
			}
		}
		return populacao;
	}
}
