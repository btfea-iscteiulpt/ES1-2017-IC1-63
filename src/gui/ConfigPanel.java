package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.*;

import utils.BuildBehavior;
import utils.RulesEvaluation;
import utils.TableFileBuildBehavior;

/**
 * Subclasse de JPanel usada nas configura��es autom�tica e manual.
 *
 */
public class ConfigPanel extends JPanel {
private boolean editable;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5973344758120539378L;

	private Map<String, Integer> regras = new HashMap<String, Integer>();
	private UserMenu menu;
	
	public Map<String, Integer> getRegras() {
		return regras;
	}
	
	/**
	 * Construtor da classe ConfigPanel.
	 * @param editable booleano que indica se a coluna de pesos poder� ser alterada.
	 */
	public ConfigPanel(boolean editable,UserMenu menu){
		if(menu==null)
			throw new IllegalArgumentException();
		this.menu=menu;
		this.editable=editable;
	}
	
	/**
	 * Constr�i e mostra no ecr� o cont�udo do painel, incluindo a tabela da configura��o que � preenchida
	 * com base no ficheiro dado nos argumentos.
	 * @param path Ficheiro a partir do qual se vai preencher a tabela de configura��o.
	 */
	public void content(File path){
		if(path==null)
			throw new IllegalArgumentException();
		BuildBehavior behavior= new TableFileBuildBehavior();
		JTable table = (JTable) behavior.init(path,editable);
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		table.setPreferredScrollableViewportSize(new Dimension(table.getPreferredSize().width,table.getRowHeight()*5));
		JScrollPane scroll = new JScrollPane(table);
		left.add(config(editable));
		left.add(scroll);
		JPanel pane = new JPanel();
		JLabel fp = new JLabel("FP = ");
		JLabel fn = new JLabel("FN = ");
		pane.add(fp);
		pane.add(fn);
		left.add(pane);
		this.add(left);
		JTextArea evaluation = new JTextArea("[Add evaluation]");
		this.add(evaluation);
		JButton button = new JButton("Test");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < table.getRowCount(); i++)
					regras.put((String) table.getValueAt(i, 0), (int) table.getValueAt(i, 1));
				fp.setText("FP = " + RulesEvaluation.FileScanner(menu.getEvaluationFile("ham.log"),ConfigPanel.this));
				fn.setText("FN = " + RulesEvaluation.FileScanner(menu.getEvaluationFile("spam.log"),ConfigPanel.this));
			//	updateRulesFile();
			}
		});
		this.add(button);
	}
	
	/**
	 * Cria label com o nome da configura��o escolhida.
	 * @param b True para configura��o manual. False para configura��o autom�tica.
	 * 
	 * @return
	 */
	private static JLabel config(boolean b){
		String s;
		if(b)
			s="Configura��o manual";
		else
			s="Configura��o autom�tica";
			return new JLabel(s);
		}
	
	private void updateRulesFile(){
		File f = menu.getEvaluationFile("rules.cf");
		PrintWriter p=null;
		try{
			p=new PrintWriter(f);
			for (String regra : regras.keySet()) {
				p.println(regra+" "+regras.get(regra));
				p.flush();
			}		
		}
		catch(FileNotFoundException e){
			System.out.println("ficheiro n�o encontrado.");
		}
		finally{
			p.close();
		}
	}
	
}