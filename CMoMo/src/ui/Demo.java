package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import dataStructure.SNode;

import java.awt.Color;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.awt.Component;
import javax.swing.border.BevelBorder;


public class Demo extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1817653052246317985L;
	
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private JPanel welcome;
	private JPanel treeTab;
	private JScrollPane scrollPane;
	
	private JTree tree;

	/**
	 * Launch the application.
	 */
	public void show(SNode root) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Demo frame = new Demo(root);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Demo(SNode root) {
		/**
		 * code I add to add a tree
		 */
	    tree = new JTree(root);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 615, 441);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		welcome = new JPanel();
		tabbedPane.addTab("welcome tab", null, welcome, null);
		
		treeTab = new JPanel();
		tabbedPane.addTab("document tab", null, treeTab, null);
		treeTab.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane(tree);
		scrollPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		treeTab.add(scrollPane);
		

	}
   
}
