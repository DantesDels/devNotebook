package com.ynov.devnotebook.ui;

import com.ynov.devnotebook.ContentService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SectionPanel extends JPanel {
	private static final Logger LOGGER = Logger.getLogger(SectionPanel.class.getName());

	private final ContentService contentService;
	private final JEditorPane editor = new JEditorPane();
	private final JScrollPane scroll = new JScrollPane(editor);
	private final JToolBar toolbar = new JToolBar();
	private final UndoManager undoManager = new UndoManager();
	private final JButton editBtn = new JButton("Modifier");
	private final JButton saveBtn = new JButton("Enregistrer");
	private final JButton deleteBtn = new JButton("Supprimer");
	private final JButton boldBtn = new JButton("B");
	private final JButton italicBtn = new JButton("I");
	private final JButton h1Btn = new JButton("H1");
	private final JButton h2Btn = new JButton("H2");
	private final JButton pBtn = new JButton("P");

	// Accessed by MainFrame via reflection for persistence
	private final String sectionKey;

	public SectionPanel(ContentService contentService, String sectionKey) {
		super(new BorderLayout());
		this.contentService = contentService;
		this.sectionKey = sectionKey;

		editor.setContentType("text/html");
		editor.setEditable(false);
		editor.setFont(editor.getFont().deriveFont(14f));
		add(scroll, BorderLayout.CENTER);

		buildToolbar();
		add(toolbar, BorderLayout.NORTH);

		// Undo/redo
		Document doc = editor.getDocument();
		doc.addUndoableEditListener(undoManager);
		doc.addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) { updateActionsState(); }
			@Override public void removeUpdate(DocumentEvent e) { updateActionsState(); }
			@Override public void changedUpdate(DocumentEvent e) { updateActionsState(); }
		});

		// Shortcuts
		editor.getInputMap().put(KeyStroke.getKeyStroke('B', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "bold");
		editor.getActionMap().put("bold", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) { insertAroundSelection("<b>", "</b>"); }
		});
		editor.getInputMap().put(KeyStroke.getKeyStroke('I', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "italic");
		editor.getActionMap().put("italic", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) { insertAroundSelection("<i>", "</i>"); }
		});
		editor.getInputMap().put(KeyStroke.getKeyStroke('1', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "h1");
		editor.getActionMap().put("h1", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) { wrapParagraph("h1"); }
		});
		editor.getInputMap().put(KeyStroke.getKeyStroke('2', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "h2");
		editor.getActionMap().put("h2", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) { wrapParagraph("h2"); }
		});
		editor.getInputMap().put(KeyStroke.getKeyStroke('0', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "p");
		editor.getActionMap().put("p", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) { wrapParagraph("p"); }
		});

		loadContent();
		updateActionsState();
	}

	private void buildToolbar() {
		toolbar.setFloatable(false);

		editBtn.addActionListener(e -> toggleEdit());
		saveBtn.addActionListener(e -> saveContent());
		deleteBtn.addActionListener(e -> deleteOverride());
		boldBtn.addActionListener(e -> insertAroundSelection("<b>", "</b>"));
		italicBtn.addActionListener(e -> insertAroundSelection("<i>", "</i>"));
		h1Btn.addActionListener(e -> wrapParagraph("h1"));
		h2Btn.addActionListener(e -> wrapParagraph("h2"));
		pBtn.addActionListener(e -> wrapParagraph("p"));

		toolbar.add(editBtn);
		toolbar.add(saveBtn);
		toolbar.add(deleteBtn);
		toolbar.addSeparator();
		toolbar.add(boldBtn);
		toolbar.add(italicBtn);
		toolbar.add(h1Btn);
		toolbar.add(h2Btn);
		toolbar.add(pBtn);

		JButton undoButton = new JButton("Annuler");
		JButton redoButton = new JButton("Rétablir");
		undoButton.addActionListener(e -> undo());
		redoButton.addActionListener(e -> redo());
		toolbar.addSeparator();
		toolbar.add(undoButton);
		toolbar.add(redoButton);
	}

	private void loadContent() {
		String html = contentService.getContent(sectionKey);
		editor.setText(html);
		editor.setCaretPosition(0);
	}

	public void toggleEdit() {
		boolean editable = !editor.isEditable();
		editor.setEditable(editable);
		editBtn.setText(editable ? "Lecture" : "Modifier");
		updateActionsState();
	}

	public void saveContent() {
		String html = editor.getText();
		try {
			contentService.saveContent(sectionKey, html);
			JOptionPane.showMessageDialog(this, "Enregistré.");
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Save failed: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this, "Erreur d'enregistrement: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void deleteOverride() {
		int res = JOptionPane.showConfirmDialog(this, "Supprimer la personnalisation et réinitialiser ?", "Confirmer", JOptionPane.YES_NO_OPTION);
		if (res != JOptionPane.YES_OPTION) return;
		try {
			contentService.deleteOverride(sectionKey);
			loadContent();
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Delete failed: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this, "Erreur de suppression: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void newContent() {
		editor.setText("<html><body><h1>Nouveau contenu</h1><p>...</p></body></html>");
		editor.setCaretPosition(0);
	}

	public void undo() {
		try { if (undoManager.canUndo()) undoManager.undo(); } catch (CannotUndoException ignored) {}
		updateActionsState();
	}
	public void redo() {
		try { if (undoManager.canRedo()) undoManager.redo(); } catch (CannotRedoException ignored) {}
		updateActionsState();
	}

	private void insertAroundSelection(String startTag, String endTag) {
		if (!editor.isEditable()) return;
		try {
			int start = Math.min(editor.getSelectionStart(), editor.getSelectionEnd());
			int end = Math.max(editor.getSelectionStart(), editor.getSelectionEnd());
			Document doc = editor.getDocument();
			String selected = doc.getText(start, end - start);
			doc.remove(start, end - start);
			doc.insertString(start, startTag + selected + endTag, null);
		} catch (BadLocationException ignored) {}
	}

	private void wrapParagraph(String tag) {
		if (!editor.isEditable()) return;
		try {
			int start = Math.min(editor.getSelectionStart(), editor.getSelectionEnd());
			int end = Math.max(editor.getSelectionStart(), editor.getSelectionEnd());
			if (start == end) { // wrap current line/paragraph
				Document doc = editor.getDocument();
				String all = doc.getText(0, doc.getLength());
				int pStart = Math.max(0, all.lastIndexOf('\n', Math.max(0, start - 1)) + 1);
				int pEnd = all.indexOf('\n', end);
				if (pEnd < 0) pEnd = all.length();
				String seg = all.substring(pStart, pEnd);
				doc.remove(pStart, pEnd - pStart);
				doc.insertString(pStart, "<" + tag + ">" + seg + "</" + tag + ">", null);
			} else {
				insertAroundSelection("<" + tag + ">", "</" + tag + ">");
			}
		} catch (BadLocationException ignored) {}
	}

	private void updateActionsState() {
		boolean editable = editor.isEditable();
		saveBtn.setEnabled(editable);
		deleteBtn.setEnabled(true);
		boldBtn.setEnabled(editable);
		italicBtn.setEnabled(editable);
		h1Btn.setEnabled(editable);
		h2Btn.setEnabled(editable);
		pBtn.setEnabled(editable);
	}
}
