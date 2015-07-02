package br.com.ia;

public class Testador_Grafico {
	public static void main(String[] args) {
		int numero_maximo_pontos=1000;
		Grafico_Dinamico grafico_Dinamico = new Grafico_Dinamico("Teste", numero_maximo_pontos);
		
		 for (int i = 0; i < numero_maximo_pontos; i++) {
			 grafico_Dinamico.adicionar_ponto(i, i+1, i, i+20);
			 try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
