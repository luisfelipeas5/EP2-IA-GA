package br.com.ia.ga;
import java.util.Random;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;


public class Mutacao {

	public static Matrix aplica_mutacao(Matrix populacao,double taxa_mutacao, int tipo_mutacao) {
		Matrix nova_populacao=new Matrix(0, populacao.getColumnDimension());
		
		if(tipo_mutacao==0) {
			Matrix populacao_mutante=mutacao_inversiva(populacao, taxa_mutacao);
			nova_populacao=JamaUtils.rowAppend(nova_populacao, populacao_mutante);
		}else if(tipo_mutacao==1) {
			Matrix populacao_mutante=mutacao_alternativa( populacao, taxa_mutacao );
			nova_populacao=JamaUtils.rowAppend(nova_populacao, populacao_mutante);
		}
		return nova_populacao;
	}

	/*
	 * A mutacao inversiva acontece da seguinte forma:
	 * -iteracao sobre todos os genes do cromossomo:
	 * 		- gera-se um numero aleatorio
	 * 		- se esse numero aleatorio eh menor do que a taxa de mutacao:
	 * 			- o gene troca de lugar como o gene da frente
	 * 		-se nao, proximo gene eh testado
	 */
	private static Matrix mutacao_inversiva(Matrix populacao, double taxa_mutacao) {
		Matrix populacao_mutante=new Matrix(0,populacao.getColumnDimension());
		Random random=new Random();
		//Itera sobre os cromossomos que podem sofrem mutacao
		for (int indice_cromossomo = 0; indice_cromossomo < populacao.getRowDimension(); indice_cromossomo++) {
			//Armazena o cromossomo que pode sofrer mutacao
			Matrix cromossomo_mutante = JamaUtils.getrow(populacao, indice_cromossomo);
			//itera sobre todos os genes do cromossomos
			for (int indice_gene = 0; indice_gene < cromossomo_mutante.getColumnDimension(); indice_gene++) {
				double doubleAleatorio=random.nextDouble(); // gera numero aleatorio
				//Testa se o gene deve sofrer mutacao
				if (doubleAleatorio<taxa_mutacao) {
					double cidade=cromossomo_mutante.get(0, indice_gene);
					int indice_proximo_gene=(indice_gene+1)%cromossomo_mutante.getColumnDimension();
					double cidade_proxima=cromossomo_mutante.get(0, indice_proximo_gene);
					//gene troca de lugar com o proximo gene na sequencia do cromossomo
					cromossomo_mutante.set(0, indice_proximo_gene, cidade);
					cromossomo_mutante.set(0, indice_gene, cidade_proxima);
				}
			}
			populacao_mutante=JamaUtils.rowAppend(populacao_mutante, cromossomo_mutante);
		}
		return populacao_mutante;
	}

	private static Matrix mutacao_alternativa(Matrix populacao, double taxa_mutacao) {
		Matrix nova_populacao=new Matrix(0, populacao.getColumnDimension());
		
		//Iterar sobre todos os cromossomos que sofrerao mutacao
		//No caso, toda a populacao
		Random random=new Random();
		for (int indice_cromossomo = 0; indice_cromossomo < populacao.getRowDimension(); indice_cromossomo++) {
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
