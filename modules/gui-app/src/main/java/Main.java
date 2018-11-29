import TN93.TN93;
import picocli.CommandLine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static javax.swing.JOptionPane.showMessageDialog;

@CommandLine.Command(name = "tn93", mixinStandardHelpOptions = true, version = "0.1")
public class Main implements Runnable{
    @CommandLine.Option(names={"-i", "--inFile"}, description="input file with sequences",
            paramLabel = "FILE")
    private File inputFile;
    @CommandLine.Option(names={"-o", "--outFile"},
            description="output file with distances",
            paramLabel = "FILE")
    private File outputFile;
    @CommandLine.Option(names={"-s", "--server"}, description="run jetty server")
    private boolean is_server;

    public void run() {
        if(is_server) {
            try {
                run_server();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(inputFile == null || outputFile == null) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();
                }
            });
        }
        else {
            TN93.tn93Fasta(inputFile, outputFile);
        }
    }
    private static void run_server() throws InterruptedException {
        System.out.println("To stop the server press Ctrl-C");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Stopping the server...");
            }
        });
        //TODO: Run Server
        while(true) Thread.sleep(1000);
    }

    public static void main(String[] args) {
        CommandLine.run(new Main(), System.out, args);
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TN93");
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        StatusPanel statusPanel = new StatusPanel();
        ButtonsPanel buttonsPanel = new ButtonsPanel();
        mainPane.add(statusPanel);
        mainPane.add(buttonsPanel);
        buttonsPanel.setOpaque(true); //content panes must be opaque
        frame.setContentPane(mainPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}

class ButtonsPanel extends JPanel implements ActionListener {
    protected JButton inBut, outBut, runBut;

    private File fastaFile, edgeListFile;


    ButtonsPanel() {
        inBut = new JButton("Load Fasta");
        outBut = new JButton("Specify Edge CSV");
        runBut = new JButton("Run TN93");

        inBut.setActionCommand("loadFasta");
        outBut.setActionCommand("specifyEdgeListFile");
        runBut.setActionCommand("runTN93");

        inBut.addActionListener(this);
        outBut.addActionListener(this);
        runBut.addActionListener(this);

        add(inBut);
        add(outBut);
        add(runBut);
    }

    public void actionPerformed(ActionEvent e) {
        if("loadFasta".equals(e.getActionCommand())) {
            JFileChooser fileopen =  new JFileChooser();
            if(JFileChooser.APPROVE_OPTION == fileopen.showDialog(null, "Open Fasta file")) {
                fastaFile = fileopen.getSelectedFile();
                System.out.println(fastaFile);
            }
        }
        else if("specifyEdgeListFile".equals(e.getActionCommand())) {
            JFileChooser fileopen =  new JFileChooser();
            fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if(JFileChooser.APPROVE_OPTION == fileopen.showDialog(null, "Specify Edge List File")) {
                edgeListFile = fileopen.getSelectedFile();
                System.out.println(edgeListFile);
            }
        }
        else if("runTN93".equals(e.getActionCommand())) {
            if(fastaFile == null) {
                showMessageDialog(null, "Specify a Fasta file!");
                return;
            }
            if(edgeListFile == null) {
                showMessageDialog(null, "Specify an Edge List file!");
                return;
            }
            TN93.tn93Fasta(fastaFile, edgeListFile);
        }
    }
}

class StatusPanel extends JPanel {
    protected JTextArea ta;

    StatusPanel() {
        ta = new JTextArea(10, 40);
        add(new JScrollPane(ta), BorderLayout.PAGE_START);
    }
}