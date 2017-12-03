package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.undo.UndoManager;

import core.Lexer;
import core.Syntaxer;
import dataStructure.DFA;
import dataStructure.ANode;
import dataStructure.SNode;

import java.awt.Color;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

import java.awt.Component;
import javax.swing.border.BevelBorder;

import core.Info;
import dataStructure.*;

public class Demo extends JFrame {

	private static final long serialVersionUID = 1817653052246317985L;

	public static  Lexer lexer;
	public static Syntaxer syntaxer;
	
	//Analysis Panel
	private static JTabbedPane analyPanel;
	
	//Menu Bar
	private static JMenuBar MenuBar=new JMenuBar();
	
	private static JMenu FileMenu;
	private static JMenu EditMenu;
	private static JMenu RunMenu;
	private static JMenu HelpMenu;
	
	//Menu List
	private static JMenuItem newItem;
	private static JMenuItem openItem;
	private static JMenuItem saveItem;
	private static JMenuItem exitItem;
	private static JMenuItem allItem;
	private static JMenuItem copyItem;
	private static JMenuItem cutItem;
	private static JMenuItem pasteItem;
	private static JMenuItem deleteItem;
	private static JMenuItem undoItem;
	private static JMenuItem redoItem;
	private static JMenuItem lexItem;
	private static JMenuItem parItem;
	private static JMenuItem semItem;
	private static JMenuItem runItem;
	private static JMenuItem aboutItem;
	
	//Tool Bar
	private static JToolBar ToolBar=new JToolBar();
	
	//Label Panel for tool bar
	private static JPanel LabelPanel=new JPanel();
	
	//Menu Tool Button
	 private static JButton lexButton;
	 private static JButton parButton;
	 private static JButton semButton;
	 private static JButton runButton;

	 //Label in tool bar
	 private static JLabel lexLabel=new JLabel();
	 private static JLabel parLabel=new JLabel();
	 private static JLabel semLabel=new JLabel();
	 private static JLabel runLabel=new JLabel();
	
	//Text editing Panel
	private static CloseableTabbedPane editPanel;
	
	//Console and Error Panel
	private static JTabbedPane ConsolePanel=new JTabbedPane();
	
	private static JTextPane consoleArea=new JTextPane();
	private static JTextPane errorArea=new JTextPane();
	
	private static HashMap<JScrollPane,TextEditor>map=new HashMap<JScrollPane,TextEditor>();
	
	//Undo Manager
	private static UndoManager undo=new UndoManager();
	private UndoableEditListener undoHandler=new UndoHandler();
	
	//FileDialog for save and open documents
	private static FileDialog SaveDialog;
	private static FileDialog OpenDialog;
	
	//Scroll Panel
	private static JScrollPane ScrollPanel;
	
	private Font font = new Font("Courier New", Font.PLAIN, 15);
	private Font labelfont = new Font("Arial",  Font.BOLD, 13);
	private Font consolefont=new Font("Arial", Font.PLAIN, 14);
	
	private JTree tree;

	//TODO: semantics
	
	//Panel to show lexical result
	private JTabbedPane lexerPane;
	
	private static StyledDocument document;
	
	//File Filter
	FileFilter filter=new FileFilter(){
		
		@Override
		public boolean accept(File pathname) {
		   String s=pathname.getName().toLowerCase();
		   
		   if(s.endsWith(".cmm")) {
			   return true;
		   }
			return false;
		}
	};
	
	public void create(String fileName) {
		if(fileName==null) {
			fileName=JOptionPane.showInputDialog("请输入文件名：")+".cmm";
			if(fileName==null||fileName.equals("")) {
				JOptionPane.showMessageDialog(null, "文件名不能为空！");
				return;
			}
		}
		
		fileName+=".cmm";
		TextEditor editor = new TextEditor();
	    editor.setFont(font);
	    JScrollPane scrollPane = new JScrollPane(editor);
	    TextLineNo tln = new TextLineNo(editor);
	    scrollPane.setRowHeaderView(tln);

	    editor.getDocument().addUndoableEditListener(undoHandler);
	    map.put(scrollPane, editor);
	    editPanel.add(scrollPane, fileName);
	    editPanel.setSelectedIndex(editPanel.getTabCount() - 1);
	}
	
	public void open() {
		String fileName=null;
		boolean isOpened=false;
		StringBuilder sb=new StringBuilder();
		File file=null;
		String s=null;
		OpenDialog.setVisible(true);
	
		if(OpenDialog.getFile()!=null) {
			try {
				file=new File(OpenDialog.getDirectory(),OpenDialog.getFile());
				fileName=file.getName();
				FileReader fr;
				fr = new FileReader(file);
				BufferedReader br=new BufferedReader(fr);
				while((s=br.readLine())!=null) {
					sb.append(s+'\n');
				}
				br.close();
				fr.close();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(int i=0;i<editPanel.getComponentCount();i++) {
				if(editPanel.getTitleAt(i).equals(fileName)){
					isOpened=true;
					JOptionPane.showMessageDialog(null, "此文件已经打开！");
					editPanel.setSelectedIndex(i);
				}
			}
			if(!isOpened) {
				create(fileName);
				editPanel.setTitleAt(editPanel.getComponentCount() - 1, fileName);
                map.get(editPanel.getSelectedComponent()).setText(sb.toString());
			}
			
		}
	}
	
	public void save() {
		TextEditor editor=map.get(editPanel.getSelectedComponent());
		if(editor.getText()!=null) {
			SaveDialog.setVisible(true);
			if(SaveDialog.getFile()!=null) {
				try {
					File file=new File(SaveDialog.getDirectory(),SaveDialog.getFile());
					FileWriter fw=new FileWriter(file);
					fw.write(editor.getText());
					fw.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			JOptionPane.showMessageDialog(null, "保存文件不能为空！");
		}
	}

	public void all() {
		TextEditor editor=map.get(editPanel.getSelectedComponent());
		editor.selectAll();
	}

	public void copy() {
		TextEditor editor=map.get(editPanel.getSelectedComponent());
		editor.copy();
	}
	
	public void cut() {
		TextEditor editor=map.get(editPanel.getSelectedComponent());
		editor.cut();
	}

	public void paste() {
		TextEditor editor=map.get(editPanel.getSelectedComponent());
		editor.paste();
	}
	
	public void delete() {
		TextEditor editor=map.get(editPanel.getSelectedComponent());
		editor.replaceSelection("");;
	}
	
	public void undo() {
		if(undo.canUndo()) {
			try {
				undo.undo();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void redo() {
		if(undo.canRedo()) {
			try {
				undo.redo();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Output lexical analysis result
	 */
	public void lex() {
		
	}
	
	/**
	 * Output syntax analysis result
	 */
	public void par() {
		tree.setCellRenderer(new DefaultTreeCellRenderer());
		tree.setShowsRootHandles(true);
		tree.setFont(font);
		analyPanel.setComponentAt(1, new JScrollPane(tree));
		analyPanel.setSelectedIndex(1);
		//TODO:error list for syntax analysis
		ConsolePanel.setSelectedIndex(1);
	}
	
	/**
	 * Output semantics analysis result
	 */
	public void sem() {
		 
	}
	
	public void run() {
		
	}
	
    class UndoHandler implements UndoableEditListener {
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            undo.addEdit(e.getEdit());
        }
    }
    
	/**
	 * Launch the application.
	 */
	public void show(SNode root) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Demo frame = new Demo(root,"CMM解释器");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	public void show(ANode root) {
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
	public Demo(SNode root,String title) {
		super();
		/**
		 * code I add to add a tree
		 */
	    tree = new JTree(root);
	    
	    setTitle(title);
	    setJMenuBar(MenuBar);
	    isResizable();
		
	    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	    FileMenu=new JMenu("文件");
		EditMenu=new JMenu("编辑");
		RunMenu=new JMenu("运行");
		HelpMenu=new JMenu("帮助");
		
		//add menu to menu bar
		MenuBar.add(FileMenu);
		MenuBar.add(EditMenu);
		MenuBar.add(RunMenu);
		MenuBar.add(HelpMenu);
		
		/**
		 * add Item to FileMenu
		 */
		newItem=new JMenuItem("新建",new ImageIcon(getClass().getResource("image/new.png")));
		openItem=new JMenuItem("打开",new ImageIcon(getClass().getResource("image/open.png")));
		saveItem=new JMenuItem("保存",new ImageIcon(getClass().getResource("image/save.png")));
		exitItem=new JMenuItem("退出",new ImageIcon(getClass().getResource("image/quit.png")));
		
		//add shortcuts
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK));
	
		FileMenu.add(newItem);
		FileMenu.add(openItem);
		FileMenu.add(saveItem);
		FileMenu.addSeparator();
		FileMenu.add(exitItem);
		
		/**
		 * add item to EditMenu
		 */
		deleteItem=new JMenuItem("删除",new ImageIcon(getClass().getResource("image/delete.png")));
		allItem=new JMenuItem("全选",new ImageIcon(getClass().getResource("image/all.png")));
		copyItem=new JMenuItem("拷贝",new ImageIcon(getClass().getResource("image/copy.png")));
		cutItem=new JMenuItem("剪切",new ImageIcon(getClass().getResource("image/cut.png")));
		pasteItem=new JMenuItem("黏贴",new ImageIcon(getClass().getResource("image/paste.png")));
		undoItem=new JMenuItem("撤销",new ImageIcon(getClass().getResource("image/undo.png")));
		redoItem=new JMenuItem("重做",new ImageIcon(getClass().getResource("image/redo.png")));
		
		//add shortcuts
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.CTRL_MASK));
		allItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
		copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK));
		cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK));
		pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK));
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,ActionEvent.CTRL_MASK));
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,ActionEvent.CTRL_MASK));
		
		EditMenu.add(undoItem);
		EditMenu.add(redoItem);
		EditMenu.addSeparator();
		EditMenu.add(cutItem);	
		EditMenu.add(copyItem);
		EditMenu.add(pasteItem);
		EditMenu.addSeparator();
		EditMenu.add(deleteItem);
		EditMenu.add(allItem);
		
		/**
		 * add item to RunMenu
		 */
		lexItem=new JMenuItem("词法分析",new ImageIcon(getClass().getResource("image/lexer.png")));
		parItem=new JMenuItem("语法分析",new ImageIcon(getClass().getResource("image/paxer.png")));
		semItem=new JMenuItem("语义分析",new ImageIcon(getClass().getResource("image/sematics.png")));
		runItem=new JMenuItem("运行",new ImageIcon(getClass().getResource("image/run.png")));
		
		lexItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,ActionEvent.CTRL_MASK));
		parItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.CTRL_MASK));
		semItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
		runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK));
		
		RunMenu.add(lexItem);
		RunMenu.add(parItem);
		RunMenu.add(semItem);
		RunMenu.addSeparator();
		RunMenu.add(runItem);
		
		/**
		 * add item to HelpMenu
		 */
		aboutItem=new JMenuItem("关于",new ImageIcon(getClass().getResource("image/about.png")));
		
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK));
		
		HelpMenu.add(aboutItem);
		
		JPanel toolPanel=new JPanel(null);
		toolPanel.setBackground(getBackground());
		
		//Menu Tool
		 lexButton = new JButton(new ImageIcon(getClass().getResource("image/lexer.png")));
	     lexButton.setToolTipText("词法分析");
	     parButton = new JButton(new ImageIcon(getClass().getResource("image/paxer.png")));
	     parButton.setToolTipText("语法分析");
	     semButton = new JButton(new ImageIcon(getClass().getResource("image/sematics.png")));
	     semButton.setToolTipText("语义分析");
	     runButton = new JButton(new ImageIcon(getClass().getResource("image/run.png")));
	     runButton.setToolTipText("运行");
	     ToolBar.setFloatable(false);
	     ToolBar.add(lexButton);
	     ToolBar.add(parButton);
	     ToolBar.add(semButton);
	     ToolBar.add(runButton);
	     
	     //label in menu tool
	     lexLabel.setText("词法分析");
	     parLabel.setText("语法分析");
	     semLabel.setText("语义分析");
	     runLabel.setText("运行");
	     LabelPanel.add(lexLabel,BorderLayout.CENTER);
	     LabelPanel.add(parLabel,BorderLayout.CENTER);
	     LabelPanel.add(semLabel,BorderLayout.CENTER);
	     LabelPanel.add(runLabel,BorderLayout.CENTER);
	     
	     toolPanel.add(ToolBar);
	     toolPanel.add(LabelPanel);
	     ToolBar.setBounds(0, 0, 1240, 50);
	     ToolBar.setPreferredSize(getPreferredSize());
	     LabelPanel.setBounds(0, 50, 1240, 20);
	     LabelPanel.setPreferredSize(getPreferredSize());
	     add(toolPanel);
	     
	     //Edit Area
	     editPanel=new CloseableTabbedPane();
	     editPanel.setFont(font);
	     
	     TextEditor editor=new TextEditor();
	     JScrollPane scrollPane=new JScrollPane(editor);
	     editor.setFont(font);
	     TextLineNo lineNo=new TextLineNo(editor);
	     scrollPane.setRowHeaderView(lineNo);
	   
	     editor.getDocument().addUndoableEditListener(undoHandler);
	     map.put(scrollPane, editor);
	     editPanel.add(scrollPane,"test.cmm");
	     JPanel Edit=new JPanel(null);
	     Edit.setBackground(getBackground());
	     Edit.setForeground(new Color(238, 238, 238));
	   
	     
	     JLabel editLabel=new JLabel("文本编辑区");
	     JPanel editLabelPanel=new JPanel(new BorderLayout());
	     editLabel.setFont(labelfont);
	     editLabelPanel.add(editLabel,BorderLayout.WEST);
	     editLabelPanel.setBackground(new Color(253, 245, 230));
	     
	     // File save and open dialog
	     SaveDialog= new FileDialog(this, "保存文件", FileDialog.SAVE);
	     SaveDialog.setVisible(false);
	     OpenDialog = new FileDialog(this, "打开文件", FileDialog.LOAD);
	     OpenDialog .setVisible(false);
	     SaveDialog.addWindowListener(new WindowAdapter() {
	     public void windowClosing(WindowEvent e) {
	    	 SaveDialog.setVisible(false);
	          }
	     });
	     OpenDialog.addWindowListener(new WindowAdapter() {
	    	 public void windowClosing(WindowEvent e) {
	    		 OpenDialog.setVisible(false);
	            }
	      });
	     
	     
	     //Console Area
	     consoleArea.setEditable(false);
	     errorArea.setEditable(false);
	     consoleArea.setFont(font);
	     errorArea.setFont(consolefont);
	     ConsolePanel = new JTabbedPane();
	     ConsolePanel.setFont(consolefont);
	     ConsolePanel.add(new JScrollPane(consoleArea), "Console");
	     ConsolePanel.add(new JScrollPane(errorArea), "Errors");
	     
	     Edit.add(editLabelPanel);
	     Edit.add(editPanel);
	     Edit.add(ConsolePanel);
	     editLabelPanel.setBounds(0,0,900,20);
	     editPanel.setBounds(0,20,900,450);
	     ConsolePanel.setBounds(0, 470, 900,180);
	     add(editPanel);
	     
	     //lexical result output
	     //TODO:
	     
	    /**
	     * add ActionListener to Menu Item
	     */
	     newItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	                create(null);
	            }
	     });
	     
	     openItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	                open();
	            }
	        });
	     
	     saveItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               save();
	            }
	        });
	     
	     exitItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               System.exit(0);
	            }
	        });
	     
	     allItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               all();
	            }
	        });
	     
	     copyItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               copy();
	            }
	        });
	     
	     cutItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               cut();
	            }
	        });
	     
	     pasteItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               paste();
	            }
	        });
	     
	     deleteItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               delete();
	            }
	        });
	     
	     undoItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               undo();
	            }
	        });
	     
	     redoItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               redo();
	            }
	        });
	     
	     lexItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	              lex();
	            }
	        });
	     
	     parItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               par();
	            }
	        });
	     
	     semItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               sem();
	            }
	        });
	     
	     runItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               run();
	            }
	        });
	     
	     aboutItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               JOptionPane.showMessageDialog(new JOptionPane(),"CMoMo\n 卢钊 刘璐","关于",JOptionPane.INFORMATION_MESSAGE);
	            }
	        });
	     
	     lexButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               lex();
	            }
	        });
	     
	     parButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               par();
	            }
	        });
	     
	     semButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               sem();
	            }
	        });
	     
	     runButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent paramActionEvent) {
	               run();
	            }
	        });
	}
	
	public Demo(ANode root) {
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
