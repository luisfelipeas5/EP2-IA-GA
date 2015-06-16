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
		//operador_selecao=args[2];
		System.out.println("Taxa de crossover: "+taxa_crossover);
		System.out.println("Taxa de mutacao: "+taxa_mutacao);
		//System.out.println("Operador de selecao: "+operador_selecao);
		
		String nome_arquivo=args[3];
		Matrix populacao=Leitor_Arquivo_Entrada.lee_arquivo(nome_arquivo);
		System.out.println("Populacao inicial:");
		populacao.print(populacao.getColumnDimension(), 3);
	
		for(int geracao_atual=0; 
				geracao_atual<numero_geracao_limite && variabilidade<variabilidade_minima;
				geracao_atual++) {
			Matrix fitness_populacao=finess(populacao);
			Matrix nova_populacao=selecao(populacao,fitness_populacao);
			aplica_crossover(nova_populacao);
			aplica_mutacao(nova_populacao);
			variabilidade=avalia(nova_populacao);
		}
	}
}
