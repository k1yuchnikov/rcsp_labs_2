import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ModesPanel extends JPanel {

    private final JButton addButton = new JButton("Вставка");
    private final JButton removeButton = new JButton("Удаление");
    private final JButton stopButton = new JButton("Стоп/возобновить");
    private final JButton stopAllButton = new JButton("Стоп всех");
    private final JButton resumeAllButton = new JButton("Возобновить всех");

    public ModesPanel() {
        setLayout(new GridLayout(5,1));

        add(addButton);
        add(removeButton);
        add(stopButton);
        add(stopAllButton);
        add(resumeAllButton);

        ImageIcon plus = new ImageIcon("src/main/resources/assets/plus.png");
        ImageIcon minus = new ImageIcon("src/main/resources/assets/minus.png");
        ImageIcon stop = new ImageIcon("src/main/resources/assets/stop.png");
        ImageIcon resume = new ImageIcon("src/main/resources/assets/resume.png");

        addButton.setIcon(plus);
        removeButton.setIcon(minus);
        stopButton.setIcon(stop);
        stopAllButton.setIcon(stop);
        resumeAllButton.setIcon(resume);

        addButton.setBackground(Color.LIGHT_GRAY);
        removeButton.setBackground(Color.LIGHT_GRAY);
        stopButton.setBackground(Color.LIGHT_GRAY);
        stopAllButton.setBackground(Color.LIGHT_GRAY);
        resumeAllButton.setBackground(Color.LIGHT_GRAY);
    }

    public void onAddButtonClicked(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void onRemoveButtonClicked(ActionListener listener) {
        removeButton.addActionListener(listener);
    }

    public void onStopButtonClicked(ActionListener listener) {
        stopButton.addActionListener(listener);
    }

    public void onStopAllButtonClicked(ActionListener listener) {
        stopAllButton.addActionListener(listener);
    }

    public void onResumeAllButtonClicked(ActionListener listener) {
        resumeAllButton.addActionListener(listener);
    }
}
