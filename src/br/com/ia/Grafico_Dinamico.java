package br.com.ia;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import aw.gui.chart.Chart2D;
import aw.gui.chart.ITrace2D;
import aw.gui.chart.Trace2DLtd;

public class Grafico_Dinamico {
	ITrace2D trace1;
	ITrace2D trace2;
	public Grafico_Dinamico(String titulo, int numero_maximo_valores) {
		// Create a chart:  
	    Chart2D chart = new Chart2D();
	    // Create an ITrace: 
	    // Note that dynamic charts need limited amount of values!!! 
	    trace1 = new Trace2DLtd(numero_maximo_valores); 
	    trace1.setColor(Color.RED);
	    
	    trace2 = new Trace2DLtd(numero_maximo_valores); 
	    trace2.setColor(Color.BLUE);
	 
	    // Add the trace to the chart:
	    chart.addTrace(trace1);
	    chart.addTrace(trace2);
	    
	    // Make it visible:
	    // Create a frame. 
	    JFrame frame = new JFrame(titulo);
	    // add the chart to the frame: 
	    frame.getContentPane().add(chart);
	    frame.setSize(400,300);
	    // Enable the termination button [cross on the upper right edge]: 
	    frame.addWindowListener(
	        new WindowAdapter(){
	          public void windowClosing(WindowEvent e){
	              System.exit(0);
	          }
	        }
	      );
	    frame.setVisible(true);
	}
	public void adicionar_ponto(double coordenada_x1, double coordenada_y1,
			double coordenada_x2, double coordenada_y2 ) {
		trace1.addPoint(coordenada_x1, coordenada_y1);
		trace2.addPoint(coordenada_x2, coordenada_y2);
	}
}
