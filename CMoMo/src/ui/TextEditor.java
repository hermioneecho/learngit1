package ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

public class TextEditor extends JTextPane implements DocumentListener {
    private static final long serialVersionUID = 1L;
    // Show if use compiler to analyze data, true by default
    private boolean cmmCompiler = true;
    private int caretStartPosition = 0;
    private HighLigter codeStyle = new HighLigter();
    private StyleContext context = new StyleContext();
    private Style style = context.getStyle(StyleContext.DEFAULT_STYLE);

    public TextEditor(boolean cmm) {
        this();
        cmmCompiler = cmm;
    }

    public TextEditor() {
        setDocument(new DefaultStyledDocument());
        getDocument().addDocumentListener(this);
        setFont(new Font(("Courier New"), Font.PLAIN, 15));
        setTabs(this, 4);
        initStyleContext();
        setMargin(new Insets(2, 0, 0, 0));
    }

    private void initStyleContext() {
    }

    private void setTabs(JTextPane textPane, int charactersPerTab) {
        FontMetrics fm = textPane.getFontMetrics(textPane.getFont());
        int charWidth = fm.charWidth('w');
        int tabWidth = charWidth * charactersPerTab;
        codeStyle.setCharWidth(charWidth);
        TabStop[] tabs = new TabStop[1000];

        for (int j = 0; j < tabs.length; j++) {
            int tab = j + 1;
            tabs[j] = new TabStop(tab * tabWidth);
        }
        TabSet tabSet = new TabSet(tabs);
        StyleConstants.setTabSet(style, tabSet);
    }

    public int getLineOfOffset(int paramInt) throws BadLocationException {
        Document localDocument = getDocument();
        if (paramInt < 0)
            throw new BadLocationException("Can't translate offset to line", -1);
        if (paramInt > localDocument.getLength())
            throw new BadLocationException("Can't translate offset to line", localDocument.getLength() + 1);
        Element localElement = getDocument().getDefaultRootElement();
        return localElement.getElementIndex(paramInt);
    }

    public int getLineCount() {
        Element localElement = getDocument().getDefaultRootElement();
        return localElement.getElementCount();
    }

    public int getLineStartOffset(int paramInt) throws BadLocationException {
        int i = getLineCount();
        if (paramInt < 0)
            throw new BadLocationException("Negative line", -1);
        if (paramInt >= i)
            throw new BadLocationException("No such line", getDocument().getLength() + 1);
        Element localElement1 = getDocument().getDefaultRootElement();
        Element localElement2 = localElement1.getElement(paramInt);
        return localElement2.getStartOffset();
    }

    private void update() {
        StyledDocument oDoc = getStyledDocument();
        StyledDocument nDoc = new DefaultStyledDocument(context);
        try {
            String text = oDoc.getText(0, oDoc.getLength());
//            codeStyle.markStyle(text, nDoc, cmmCompiler);
            oDoc.removeDocumentListener(this);
            nDoc.addDocumentListener(this);
            int off = getCaretPosition();
            setDocument(nDoc);
            setCaretPosition(off);
        } catch (BadLocationException e) {
            e.printStackTrace();
        } finally {
            oDoc = null;
        }
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        update();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        update();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public void paint(Graphics graphics) {
        super.paint(graphics);
//        codeStyle.drawWaveLine(graphics);
    }

    public void setSize(Dimension dimension) {
        int parentWidth = this.getParent().getWidth();
        if (parentWidth > dimension.width)
            dimension.width = parentWidth;
        super.setSize(dimension);
    }

}