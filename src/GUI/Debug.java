package GUI;

import Aplikacia.Prezenter;

import javax.swing.*;

public class Debug extends JPanel
{
    private JPanel panel;
    private JTextArea text_vystup;
    private JButton button_oknoHlavne;

    private JButton button_hlavnyParcely;
    private JButton button_preplnujuciParcely;
    private JButton button_hlavnyZretazenieParcely;
    private JButton button_preplnujuciZretazenieParcely;
    private JButton button_stromParcely;

    private JButton button_hlavnyNehnutelnosti;
    private JButton button_preplnujuciNehnutelnosti;
    private JButton button_hlavnyZretazenieNehnutelnosti;
    private JButton button_preplnujuciZretazenieNehnutelnosti;
    private JButton button_stromNehnutelnosti;

    public Debug(Prezenter prezenter, GUI gui)
    {
        this.button_oknoHlavne.addActionListener(e -> gui.zobrazHlavneOkno());

        this.button_hlavnyParcely.addActionListener(e -> this.text_vystup.setText(prezenter.getStringHlavnySuborParcely()));
        this.button_preplnujuciParcely.addActionListener(e -> this.text_vystup.setText(prezenter.getStringPreplnujuciSuborParcely()));
        this.button_hlavnyZretazenieParcely.addActionListener(e -> this.text_vystup.setText(prezenter.getStringHlavnySuborZretazenieParcely()));
        this.button_preplnujuciZretazenieParcely.addActionListener(e -> this.text_vystup.setText(prezenter.getStringPreplnujuciSuborZretazenieParcely()));
        this.button_stromParcely.addActionListener(e -> this.text_vystup.setText(prezenter.getStringStromParcely()));

        this.button_hlavnyNehnutelnosti.addActionListener(e -> this.text_vystup.setText(prezenter.getStringHlavnySuborNehnutelnosti()));
        this.button_preplnujuciNehnutelnosti.addActionListener(e -> this.text_vystup.setText(prezenter.getStringPreplnujuciSuborNehnutelnosti()));
        this.button_hlavnyZretazenieNehnutelnosti.addActionListener(e -> this.text_vystup.setText(prezenter.getStringHlavnySuborZretazenieNehnutelnosti()));
        this.button_preplnujuciZretazenieNehnutelnosti.addActionListener(e -> this.text_vystup.setText(prezenter.getStringPreplnujuciSuborZretazenieNehnutelnosti()));
        this.button_stromNehnutelnosti.addActionListener(e -> this.text_vystup.setText(prezenter.getStringStromNehnutelnosti()));
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
