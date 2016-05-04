
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.math3.special.Gamma;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Bayes extends JFrame implements ActionListener{
	
	//Combobox for Standardfordeling, Betafordeling og Normalfordeling
	//Noen jtextfields for prior, lik, posterior. De hører til standardfordeling
	//Noen checkboxes for velge selv/nøytral, noen jtextfields for a og b verdi, en graf og noen verdier for E[X], Var[X] og O[X] (sigma). Dette er for Betafordeling
	//Normalfordeling er variansanalyse. Man velger tre checkboxes og får ut et prosent i et jtextfield(?)
	
	
	//Lister String
	String[] fordelingsListe = {"-- Velg fordelingsmåte --", "Standardfordeling", "Betafordeling"};
	String[] nøytralListe = {"-- Velg prior --", "Haldane's", "Jeffreys'", "Bayes"};
	
	//Lister int
	double[] xPoints = new double[1001];
	double[] yPoints = new double[1001];

	//Primitiver
	int antRad = 0;
	double k = 0;
	double kt = 0;
	double lt = 0;
	double var = 0;
	double forvent = 0;
	double sigma = 0;
	double sum = 0;
	double prior = 0.0;
	double like = 0.0;
	double pab = 0.0;
	double pabh = 0.0;
	
	//JButtons
	JButton regnUt = new JButton("Regn ut");
	JButton addRad = new JButton("Legg til rad");
	JButton fjernRad = new JButton("Fjern en rad");
	JButton slettTabell = new JButton("Slett tabell");
	
	//JTextFields
	JTextField kf = new JTextField();
	JTextField lf = new JTextField();
	
	//Comboboxes
	JComboBox fordeling = new JComboBox(fordelingsListe);
	JComboBox nøytral = new JComboBox(nøytralListe);
	
	//JRadioButton
	JRadioButton vs = new JRadioButton("Velg Selv");
	JRadioButton nøy = new JRadioButton("Nøytral");
	
	//Buttongroup
	ButtonGroup beta = new ButtonGroup();
	
	//JLabels
	JLabel kLabel = new JLabel("k");
	JLabel lLabel = new JLabel("l");
	JLabel ex = new JLabel("E[x] = ");
	JLabel varx = new JLabel("Var[x] =");
	JLabel ox = new JLabel("Sigma(x) = ");
	JLabel forSide = new JLabel("Statistikk prosjekt - Kalkulator");
	
	//Setter opp kolonner og rader i JTable	
	String kolonneNavn[] = {"Prior", "Likelihood", "Posterior"};
	
	//JTable og Scrollpane
	DefaultTableModel dfmodel = new DefaultTableModel(kolonneNavn, antRad);
	JTable standardTable = new JTable(dfmodel);
	JScrollPane standardScroll = new JScrollPane(standardTable);
	
	//Setter opp beta grafen
	final XYSeries series = new XYSeries("Fordelingen");
	final XYSeriesCollection data = new XYSeriesCollection(series);
	final JFreeChart chart = ChartFactory.createXYLineChart("Fordeling", "X", "Y", data, PlotOrientation.VERTICAL, true, true, false);
	final ChartPanel chartPanel = new ChartPanel(chart);
	
	//Height og Width på skjermen
	Toolkit tk = Toolkit.getDefaultToolkit();
	Dimension d = tk.getScreenSize();
	int w = d.width;
	int h = d.height;
			
	public Bayes(){
		//ButtonGroup
		ButtonGroup beta = new ButtonGroup();
		beta.add(vs);
		beta.add(nøy);
		
		//Gjør JTextFields uneditable
		kf.setEditable(false);
		lf.setEditable(false);
		
		//Editer labels
		forSide.setFont(new Font("Serif", Font.BOLD, 30));
		
		//Adder
		this.add(fordeling);
		this.add(vs);
		this.add(nøy);
		this.add(kf);
		this.add(lf);
		this.add(ex);
		this.add(varx);
		this.add(ox);
		this.add(kLabel);
		this.add(lLabel);
		this.add(regnUt);
		this.add(chartPanel);
		this.add(nøytral);
		this.add(standardScroll);
		this.add(addRad);
		this.add(fjernRad);
		this.add(slettTabell);
		this.add(forSide);
		
		
		//Setter location relativ til skjermstørrelse
		fordeling.setLocation((int) (w/13.66), (int) (h/7.68));
		vs.setLocation((int) (w/6.83), (int) (h/3.84));
		nøy.setLocation((int) (w/6.83), (int) (h/3.41333));
		kf.setLocation((int) (w/2.732), (int) (h/7.68));
		lf.setLocation((int) (w/2.44), (int) (h/7.68));
		ex.setLocation((int) (w/2.732), (int) (h/1.449));
		varx.setLocation((int) (w/1.95), (int) (h/1.449));
		ox.setLocation((int) (w/1.51778), (int) (h/1.449));
		kLabel.setLocation((int) (w/2.627), (int) (h/10.24));
		lLabel.setLocation((int) (w/2.355), (int) (h/10.24));
		regnUt.setLocation((int) (w/13.66), (int) (h/1.92));
		chartPanel.setLocation((int) (w/3.903), (int) (h/5.12));
		nøytral.setLocation((int) (w/6.83), (int) (h/3.072));
		standardScroll.setLocation((int) (w/3.415), (int) (h/7.68));
		addRad.setLocation((int) (w/1.4379), (int) (h/7.68));
		fjernRad.setLocation((int) (w/1.4379), (int) (h/5.12));
		slettTabell.setLocation((int) (w/1.4379), (int) (h/3.84));
		forSide.setLocation((int) (w/2.7), (int) (h/1000-50));
		
		//Setter size relativ til skjermstørrelse
		fordeling.setSize((int) (w/7.66), (int) (h/30.72));
		vs.setSize((int) (w/13.66), (int) (h/30.72));
		nøy.setSize((int) (w/13.66), (int) (h/30.72));
		kf.setSize((int) (w/27.32), (int) (h/30.72));
		lf.setSize((int) (w/27.32), (int) (h/30.72));
		ex.setSize((int) (w/6.83), (int) (h/30.72));
		varx.setSize((int) (w/6.83), (int) (h/30.72));
		ox.setSize((int) (w/6.83), (int) (h/30.72));
		kLabel.setSize((int) (w/13.66), (int) (h/30.72));
		lLabel.setSize((int) (w/13.66), (int) (h/30.72));
		regnUt.setSize((int) (w/13.66), (int) (h/30.72));
		chartPanel.setSize(new java.awt.Dimension((int) (w/1.7075), (int) (h/2.1943)));
		nøytral.setSize((int) (w/10.928), (int) (h/30.72));
		standardScroll.setSize((int) (w/2.732), (int) (h/3.84));
		addRad.setSize((int) (w/6.83), (int) (h/30.72));
		fjernRad.setSize((int) (w/6.83), (int) (h/30.72));
		slettTabell.setSize((int) (w/6.83), (int) (h/30.72));
		forSide.setSize(800, 300);
		
		//Legger til ActionListeners
		fordeling.addActionListener(this);
		regnUt.addActionListener(this);
		vs.addActionListener(this);
		nøy.addActionListener(this);
		nøytral.addActionListener(this);
		addRad.addActionListener(this);
		fjernRad.addActionListener(this);
		slettTabell.addActionListener(this);
		
		
		//Setup
		this.setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setVisible(true);
		usynlig();
		forSide.setVisible(true);
	}
	
	//Setter alt usynlig
	public void usynlig() {
		vs.setVisible(false);
		nøy.setVisible(false);
		kf.setVisible(false);
		lf.setVisible(false);
		ex.setVisible(false);
		varx.setVisible(false);
		ox.setVisible(false);
		kLabel.setVisible(false);
		lLabel.setVisible(false);
		regnUt.setVisible(false);
		chartPanel.setVisible(false);
		nøytral.setVisible(false);
		standardScroll.setVisible(false);
		addRad.setVisible(false);
		fjernRad.setVisible(false);
		slettTabell.setVisible(false);
		forSide.setVisible(false);
	}
			
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
		//Sjekker om det er Standardfordeling
		if(fordeling.getSelectedItem().equals("Standardfordeling")){
			
			//Setter visible og invisible
			usynlig();
			regnUt.setVisible(true);
			standardScroll.setVisible(true);
			addRad.setVisible(true);
			fjernRad.setVisible(true);
			slettTabell.setVisible(true);
			
			//Hvis noen trykker på "addRad" så blir det lagt til en rad
			if(arg0.getSource().equals(addRad)){
				dfmodel.addRow(new Object[]{"", "", ""});	
				antRad += 1;
			}
			//Hvis noen trykker "fjernRad" så fjernes en rad
			if(arg0.getSource().equals(fjernRad)){
				dfmodel.removeRow(standardTable.getSelectedRow());
				antRad -= 1;
			}
			//Tømmer hele tabellen om noen trykker på "slettTabell"
			if(arg0.getSource().equals(slettTabell)){
				for(int i = 0 ; i < antRad ; i++){
					dfmodel.removeRow(0);
				}
				antRad = 0;
			}
			
			
			//Sjekker om noen trykket på Regn ut
			if(arg0.getSource().equals(regnUt)){
				standardTable.clearSelection();
				pabh = 0.0;
				standardTable.editCellAt(-1, -1);
				
				//Regner ut P(B)
				for(int i = 0 ; i < antRad ; i++){
					prior = Double.parseDouble(dfmodel.getValueAt(i, 0).toString());
					like = Double.parseDouble(dfmodel.getValueAt(i, 1).toString());
					pab = prior * like;
					pabh += pab;
					
				}
				//Regner ut P(B|A)
				for(int i = 0 ; i < antRad ; i++){
					prior = Double.parseDouble(dfmodel.getValueAt(i, 0).toString());
					like = Double.parseDouble(dfmodel.getValueAt(i, 1).toString());
					pab = prior * like;
					dfmodel.setValueAt(pab/pabh, i, 2);
				}
				
			}
			
		}
		
		//Sjekker om det er betafordeling
		if(fordeling.getSelectedItem().equals("Betafordeling")){
			
			//Setter visible og invisible
			usynlig();
			vs.setVisible(true);
			nøy.setVisible(true);
			kf.setVisible(true);
			lf.setVisible(true);
			ex.setVisible(true);
			varx.setVisible(true);
			ox.setVisible(true);
			kLabel.setVisible(true);
			lLabel.setVisible(true);
			regnUt.setVisible(true);
			chartPanel.setVisible(true);
			
			//Sjekker om 'velg selv' radio button er valgt
			if(vs.isSelected() == true){
				kf.setEditable(true);
				lf.setEditable(true);
				nøytral.setVisible(false);
				nøytral.setSelectedItem("-- Velg prior --");
			}
			//Sjekker om 'nøytral' radio button er valgt
			if(nøy.isSelected() == true){
				kf.setEditable(false);
				lf.setEditable(false);
				kf.setText("");
				lf.setText("");
				nøytral.setVisible(true);
				
			}
			
			//Sjekker om de trykket på Regn ut
			if(arg0.getSource().equals(regnUt)){
				
				//Clearer grafen
				series.clear();
				
				//Sjekker om velg selv er satt
				if(vs.isSelected()){
					//Gjør k og l til tekst
					nøytral.setSelectedItem("-- Velg verdi --");
					kt = Double.parseDouble(kf.getText());
					lt = Double.parseDouble(lf.getText());
				}
				
				//Sjekker hvilken ComboBox som er valgt av 0, 1/2 og 1 og setter verdier
				if(nøytral.getSelectedItem().equals("Haldane's")){
					kt = 0.0;
					lt = 0.0;
				}
				if(nøytral.getSelectedItem().equals("Jeffreys'")){
					kt = 0.5;
					lt = 0.5;
				}
				if(nøytral.getSelectedItem().equals("Bayes")){
					kt = 1.0;
					lt = 1.0;
				}
				
				//Regner ut varians
				var = ((kt*lt)/(kt+lt)*(kt+lt)*(kt+lt+1));
				varx.setText(String.valueOf("Var[x] = " + var));
				
				//Regner ut sigma
				sigma = Math.sqrt(var);
				ox.setText(String.valueOf("Sigma(x) = " + sigma));
				
				//Regner ut forventning
				forvent = (kt/(kt+lt));
				ex.setText(String.valueOf("E[x] = " + forvent));
				
				//Regner ut k
				k = Gamma.gamma((kt+lt))/(Gamma.gamma(kt)*Gamma.gamma(lt));
				
				//Xpunkter for betagrafen
				for(int i = 1 ; i < 1000 ; i++){
					xPoints[i] = (double) i/1000.0;
				}
				//Ypunkter for betagrafen
				for(int i = 1 ; i < 1000 ; i++){
					yPoints[i] = k*(Math.pow(xPoints[i], kt - 1.0)*Math.pow(1.0 - xPoints[i], lt - 1.0));
				}
				
				//Legger til alle punktene i betagrafen
				for(int i = 1 ; i < 1000 ; i++){
					series.add(xPoints[i], yPoints[i]);
				}		        		        
			}
		}
		
	}


}
