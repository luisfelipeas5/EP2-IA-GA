import java.util.Random;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;


public class Mutacao {

	public static Matrix aplica_mutacao(Matrix populacao,double taxa_mutacao, 
										int quantidade_individuos_mutantes, int tipo_mutacao) {
		Matrix nova_populacao=new Matrix(0, populacao.getColumnDimension());
		int quantidade_individuos_nao_mutantes=populacao.getRowDimension()-quantidade_individuos_mutantes;
		for (int indice_cromossomo_nao_mutante = 0; indice_cromossomo_nao_mutante < quantidade_individuos_nao_mutantes; indice_cromossomo_nao_mutante++) {
			Matrix cromossomo_nao_mutante=JamaUtils.getrow(populacao, indice_cromossomo_nao_mutante);
			nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_nao_mutante);
		}
		
		if(tipo_mutacao==0) {
			Matrix populacao_mutante=mutacao_inversiva( populacao, quantidade_individuos_mutantes, taxa_mutacao );
			nova_populacao=JamaUtils.rowAppend(nova_populacao, populacao_mutante);
		}
		return nova_populacao;
	}

	private static Matrix mutacao_inversiva(Matrix populacao, int quantidade_individuos_mutantes,double taxa_mutacao) {
		Matrix nova_populacao=new Matrix(0, populacao.getColumnDimension());
		
		//Iterar sobre todos os cromossomos que sofrerao mutacao
		//No caso, toda a populacao
		Random random=new Random();
		int quantidade_individuos_nao_mutantes=populacao.getRowDimension()-quantidade_individuos_mutantes;
		for (int indice_cromossomo = quantidade_individuos_nao_mutantes; indice_cromossomo < populacao.getRowDimension(); indice_cromossomo++) {
			Matrix cromossomo= JamaUtils.getrow(populacao, indice_cromossomo);
			
			//Definir a partir da taxa de mutacao, qual trecho do cromossomo sofrera a mutacao inversiva
			int intAleatorio=random.nextInt();
			//Indice de corte inicial
			int indice_gene_corte_inicio= Math.abs(intAleatorio%cromossomo.getColumnDimension());
			//Indice de corte final
			int indice_gene_corte_final=indice_gene_corte_inicio+(int)(cromossomo.getColumnDimension()*taxa_mutacao);
			indice_gene_corte_final=Math.abs(indice_gene_corte_final%cromossomo.getColumnDimension());
			
			//System.out.println("indice_gene_corte_inicio="+indice_gene_corte_inicio);
			//System.out.println("indice_gene_corte_final="+indice_gene_corte_final);
			
			//Criar vetor auxiliar para facilitar na inversao dos genes.
			//Esse contera o trecho do cromossomo que sofrera mutacao
			int tamanho_cromossomo_mutante=(int)(cromossomo.getColumnDimension()*taxa_mutacao);
			Matrix cromossomo_parte_mutante=new Matrix(1, tamanho_cromossomo_mutante);
			int coluna_vazia_cromossomo_mutante=0;
			//Armazena os genes que sofreram mutacao
			for (int indice_gene_mutante = indice_gene_corte_inicio; coluna_vazia_cromossomo_mutante<tamanho_cromossomo_mutante; indice_gene_mutante++) {
				indice_gene_mutante=indice_gene_mutante%cromossomo.getColumnDimension();
				double gene=cromossomo.get(0,indice_gene_mutante);
				cromossomo_parte_mutante.set(0, coluna_vazia_cromossomo_mutante, gene);
				coluna_vazia_cromossomo_mutante++;
			}
			//System.out.println("genes mutantes:");
			//cromossomo_parte_mutante.print(0, 0);
			
			//Aplica de fato a mutacao, invertendo as posicoes do trecho do cromossomo
			//que sofre mutacao
			int coluna_cromossomo_mutante=indice_gene_corte_inicio;
			for (int indice_gene_mutante_inverso = cromossomo_parte_mutante.getColumnDimension()-1;
					indice_gene_mutante_inverso >=0;
					indice_gene_mutante_inverso--) {
				double gene_mutante=cromossomo_parte_mutante.get(0, indice_gene_mutante_inverso);
				cromossomo.set(0, coluna_cromossomo_mutante, gene_mutante);
				coluna_cromossomo_mutante=(coluna_cromossomo_mutante+1)%cromossomo.getColumnDimension();
			}
			//System.out.print("novo cromossomo:");
			//cromossomo.print(0, 0);
			
			//Adiciona o cromossomo a nova populacao
			nova_populacao = JamaUtils.rowAppend(nova_populacao, cromossomo);
		}
		//System.out.print("Nova populacao");
		//nova_populacao.print(0, 0);
		return nova_populacao;
	}
	
}
