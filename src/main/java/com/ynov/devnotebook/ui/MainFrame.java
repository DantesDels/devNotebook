package com.ynov.devnotebook.ui;

import com.ynov.devnotebook.ContentService;
import com.ynov.devnotebook.ui.sections.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

/**
 * MainFrame: main window with tabs, menus, and dynamic tab management.
 */
public class MainFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());

    private final ContentService contentService = new ContentService();
    private final JTabbedPane tabs = new JTabbedPane();
    private final java.nio.file.Path tabsState = com.ynov.devnotebook.PathsUtil.getUserOverrideDir().resolve("tabs.json");

    public MainFrame() {
        super("Notebook des bonnes pratiques");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setJMenuBar(buildMenuBar());

        tabs.addTab("Lisibilité du code", new ReadabilitySection(contentService));
        tabs.addTab("Sécurité des informations", new SecuritySection(contentService));
        tabs.addTab("Commentaires", new CommentsSection(contentService));
        tabs.addTab("Refactorisation", new RefactoringSection(contentService));
        tabs.addTab("Scalabilité", new ScalabilitySection(contentService));
        restoreCustomTabs();

        // Onglet + (icône) pour ajouter
        tabs.addTab(" ", IconFactory.plusIcon(12, 12), null, "Nouvel onglet");
        tabs.addChangeListener(new ChangeListener() {
            @Override public void stateChanged(ChangeEvent e) {
                int last = tabs.getTabCount() - 1;
                if (tabs.getSelectedIndex() == last) createNewTab();
            }
        });
        tabs.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) { maybeShowContextMenu(e); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { maybeShowContextMenu(e); }
            private void maybeShowContextMenu(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int index = tabs.indexAtLocation(e.getX(), e.getY());
                    if (index >= 0 && index < tabs.getTabCount() - 1) {
                        JPopupMenu menu = new JPopupMenu();
                        JMenuItem del = new JMenuItem("Supprimer l'onglet");
                        del.addActionListener(a -> { tabs.removeTabAt(index); saveCustomTabs(); });
                        menu.add(del);
                        menu.show(tabs, e.getX(), e.getY());
                    }
                }
            }
        });

        add(tabs, BorderLayout.CENTER);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("Fichier");
        file.add(new JMenuItem(new AbstractAction("Nouveau") {
            @Override public void actionPerformed(ActionEvent e) {
                getCurrentSectionPanel().ifPresent(SectionPanel::newContent);
            }
        }));
        file.add(new JMenuItem(new AbstractAction("Enregistrer") {
            @Override public void actionPerformed(ActionEvent e) {
                getCurrentSectionPanel().ifPresent(SectionPanel::saveContent);
            }
        }));
        file.addSeparator();
        file.add(new JMenuItem(new AbstractAction("Quitter") {
            @Override public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }));

        JMenu edit = new JMenu("Édition");
        edit.add(new JMenuItem(new AbstractAction("Modifier") {
            @Override public void actionPerformed(ActionEvent e) {
                getCurrentSectionPanel().ifPresent(SectionPanel::toggleEdit);
            }
        }));
        edit.add(new JMenuItem(new AbstractAction("Supprimer (réinitialiser)") {
            @Override public void actionPerformed(ActionEvent e) {
                getCurrentSectionPanel().ifPresent(SectionPanel::deleteOverride);
            }
        }));
        JMenuItem undoItem = new JMenuItem(new AbstractAction("Annuler (Ctrl+Z)") {
            @Override public void actionPerformed(ActionEvent e) { getCurrentSectionPanel().ifPresent(SectionPanel::undo); }
        });
        undoItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        JMenuItem redoItem = new JMenuItem(new AbstractAction("Rétablir (Ctrl+Y)") {
            @Override public void actionPerformed(ActionEvent e) { getCurrentSectionPanel().ifPresent(SectionPanel::redo); }
        });
        redoItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        edit.addSeparator();
        edit.add(undoItem);
        edit.add(redoItem);

        JMenu help = new JMenu("Aide");
        help.add(new JMenuItem(new AbstractAction("À propos") {
            @Override public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Dev Notebook\nUn carnet de bonnes pratiques éditable.",
                        "À propos",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }));

        bar.add(file);
        bar.add(edit);
        bar.add(help);
        return bar;
    }

    private java.util.Optional<SectionPanel> getCurrentSectionPanel() {
        Component comp = tabs.getSelectedComponent();
        if (comp instanceof SectionPanel) return java.util.Optional.of((SectionPanel) comp);
        return java.util.Optional.empty();
    }

    private void createNewTab() {
        String name = JOptionPane.showInputDialog(this, "Nom de l'onglet :", "Nouvel onglet", JOptionPane.PLAIN_MESSAGE);
        int lastIdx = Math.max(0, tabs.getTabCount() - 2);
        if (name == null || name.isBlank()) { tabs.setSelectedIndex(lastIdx); return; }
        SectionPanel panel = new SectionPanel(contentService, sanitizeKey(name));
        int insertIndex = tabs.getTabCount() - 1;
        tabs.insertTab(name, null, panel, null, insertIndex);
        tabs.setSelectedIndex(insertIndex);
        saveCustomTabs();
    }

    private String sanitizeKey(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }

    private void saveCustomTabs() {
        try {
            java.util.List<java.util.Map<String, String>> list = new java.util.ArrayList<>();
            for (int i = 0; i < tabs.getTabCount() - 1; i++) {
                Component c = tabs.getComponentAt(i);
                if (c instanceof SectionPanel) {
                    String title = tabs.getTitleAt(i);
                    String key = getSectionKey((SectionPanel) c);
                    if (!isBuiltinTitle(title)) {
                        java.util.Map<String, String> map = new java.util.HashMap<>();
                        map.put("title", title);
                        map.put("key", key);
                        list.add(map);
                    }
                }
            }
            java.nio.file.Path dir = tabsState.getParent();
            if (!java.nio.file.Files.exists(dir)) java.nio.file.Files.createDirectories(dir);
            String json = toJson(list);
            java.nio.file.Files.writeString(tabsState, json, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception ignored) {}
    }

    private void restoreCustomTabs() {
        try {
            if (!java.nio.file.Files.exists(tabsState)) return;
            String json = java.nio.file.Files.readString(tabsState, java.nio.charset.StandardCharsets.UTF_8);
            java.util.List<java.util.Map<String, String>> list = fromJson(json);
            for (java.util.Map<String, String> m : list) {
                String title = m.get("title");
                String key = m.get("key");
                SectionPanel panel = new SectionPanel(contentService, key);
                int insertIndex = tabs.getTabCount();
                tabs.insertTab(title, null, panel, null, insertIndex - 1);
            }
        } catch (Exception ignored) {}
    }

    private boolean isBuiltinTitle(String title) {
        return java.util.Set.of("Lisibilité du code", "Sécurité des informations", "Commentaires", "Refactorisation", "Scalabilité").contains(title);
    }

    private String getSectionKey(SectionPanel panel) {
        try {
            java.lang.reflect.Field f = SectionPanel.class.getDeclaredField("sectionKey");
            f.setAccessible(true);
            Object val = f.get(panel);
            return val == null ? "" : val.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // JSON minimal (sans dépendances)
    private String toJson(java.util.List<java.util.Map<String, String>> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            var m = list.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"title\":\"").append(escape(m.get("title"))).append("\",\"key\":\"").append(escape(m.get("key"))).append("\"}");
        }
        sb.append("]");
        return sb.toString();
    }

    private java.util.List<java.util.Map<String, String>> fromJson(String json) {
        java.util.List<java.util.Map<String, String>> list = new java.util.ArrayList<>();
        json = json.trim();
        if (json.length() < 2 || json.charAt(0) != '[') return list;
        json = json.substring(1, json.length() - 1).trim();
        if (json.isEmpty()) return list;
        String[] objs = json.split("\\},\\{");
        for (String obj : objs) {
            String o = obj.replace("{", "").replace("}", "");
            String[] parts = o.split(",");
            String title = ""; String key = "";
            for (String part : parts) {
                String[] kv = part.split(":", 2);
                if (kv.length == 2) {
                    String k = unquote(kv[0]);
                    String v = unquote(kv[1]);
                    if ("title".equals(k)) title = v; else if ("key".equals(k)) key = v;
                }
            }
            java.util.Map<String, String> map = new java.util.HashMap<>();
            map.put("title", title);
            map.put("key", key);
            list.add(map);
        }
        return list;
    }

    private String escape(String s) { return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\""); }
    private String unquote(String s) { s = s.trim(); if (s.startsWith("\"")) s = s.substring(1); if (s.endsWith("\"")) s = s.substring(0, s.length()-1); return s.replace("\\\"", "\"").replace("\\\\", "\\"); }
}