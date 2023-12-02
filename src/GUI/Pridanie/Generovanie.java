package GUI.Pridanie;

import Aplikacia.Prezenter;
import GUI.*;

import javax.swing.*;

public class Generovanie extends JPanel
{
    private JPanel panel;
    private JButton button_oknoHlavne;
    private JButton button_potvrd;

    public Generovanie(Prezenter prezenter, GUI gui)
    {
        this.button_oknoHlavne.addActionListener(e -> gui.zobrazHlavneOkno());

        this.button_potvrd.addActionListener(e -> {
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
