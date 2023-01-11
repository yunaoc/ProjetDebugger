package ihm;


import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.MouseListener;

public class MainFrame extends JFrame {
    private JPanel boutonsPanel;
    private JPanel contextePanel;
    private JPanel breakpointsPanel;
    private JPanel codeSourcePanel;
    public JTextArea affichageBreakpoints;
    public JTextArea affichageContexte;
    public JTextArea affichageCodeSource;
    private MouseListener listener;


    public MainFrame(){
        super("Debugger");
        setSize(new Dimension(600,500));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        listener = new BoutonsMouseListener(this);
        initFrame();
        setVisible(true);
    }

    private void initFrame() {
        setLayout(new BorderLayout());
        add(initBoutons(),BorderLayout.WEST);
        add(initContext(),BorderLayout.SOUTH);
        add(initBreakpoints(),BorderLayout.EAST);
        add(initCodeSource(),BorderLayout.CENTER);
    }

    private JPanel initBoutons() {
        boutonsPanel = new JPanel();
        initParties("Actions",boutonsPanel);
        boutonsPanel.setBackground(Color.BLACK);
        boutonsPanel.add(addBoutons("Continue"));
        boutonsPanel.add(addBoutons("Break"));
        boutonsPanel.add(addBoutons("Step"));
        boutonsPanel.add(addBoutons("StepOver"));
        return boutonsPanel;
    }

    private void initParties(String titrePartie, JPanel panel) {
        JLabel titre = new JLabel(titrePartie);
        titre.setFont(new Font(titre.getFont().getFontName(), titre.getFont().getStyle(), 14));
        panel.add(titre);
        BoxLayout boutonsLayout = new BoxLayout(panel,BoxLayout.Y_AXIS);
        panel.setLayout(boutonsLayout);
        panel.setBorder( BorderFactory.createLineBorder(Color.BLACK,1));

    }

    private JPanel initContext() {
        contextePanel = new JPanel();
        initParties("Contexte",contextePanel);
        contextePanel.setBackground(Color.RED);
        affichageContexte = new JTextArea();
        //TODO tests
        affichageContexte.append("Contexte");
        contextePanel.add(affichageContexte);
        return contextePanel;
    }

    private JPanel initBreakpoints() {
        breakpointsPanel = new JPanel();
        initParties("Breakpoints",breakpointsPanel);
        breakpointsPanel.setBackground(Color.GREEN);
        affichageBreakpoints = new JTextArea();
        //TODO tests
        breakpointsPanel.add(affichageBreakpoints);
        return breakpointsPanel;
    }

    private void AjouterLigneBreakpoints(String nomClasse, String numeroLigne) {
        affichageBreakpoints.append(nomClasse+":"+numeroLigne+"\n");
    }

    public void initTextBreakpoints(List<String> breakpoints) {
        for(String breakpoint : breakpoints){
            AjouterLigneBreakpoints(breakpoint,breakpoint); //TODO substring ?
        }
    }

    public void initTextContexte(String locationFrame,List<String> methodesStack,List<String> temporaries,List<String> arguments){
        resetTextContexte();
        affichageContexte.append("Frame : "+locationFrame+"\n");
        affichageContexte.append("Call-stack :\n");
        for(String nomMethode : methodesStack){
            affichageContexte.append("  "+nomMethode+"\n");
        }
        affichageContexte.append("Temporaries :\n");
        for(String variable : temporaries){
            affichageContexte.append("  "+variable+"\n");
        }
        affichageContexte.append("Arguments :\n");
        for(String variable : arguments){
            affichageContexte.append("  "+variable+"\n");
        }
    }

    public void initTextCodeSource(StringBuffer string){
        resetTextCodeSource();
        affichageCodeSource.append(string.toString());
    }

    private void resetTextContexte(){
        affichageContexte = new JTextArea();
    }

    private void resetTextBreakpoints(){
        affichageBreakpoints = new JTextArea();
    }

    private void resetTextCodeSource(){
        affichageCodeSource = new JTextArea();
    }

    private JPanel initCodeSource() {
        codeSourcePanel = new JPanel();
        initParties("CodeSource",codeSourcePanel);
        codeSourcePanel.setBackground(Color.CYAN);
        affichageCodeSource = new JTextArea();
        //TODO tests
        affichageCodeSource.append("CodeSource");
        codeSourcePanel.add(affichageCodeSource);
        return codeSourcePanel;
    }

    private JButton addBoutons(String titre){
        JButton bouton = new JButton();
        bouton.setText(titre);
        bouton.setActionCommand(titre);
        bouton.addMouseListener(listener);
        return bouton;
    }

}
