package org.opennars.lab.nlp;

import org.opennars.io.Narsese;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

public class NlpGui {
    public void setup() {
        JFrame frame=new JFrame();//creating instance of JFrame


        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());


        JButton b=new JButton("click");//creating instance of JButton
        b.setBounds(60,100,100, 40);//x axis, y axis, width, height

        panel.add(b);


        JTextField textField = new JTextField(20);
        textField.setText("");
        textField.addActionListener(new CommandActionListener());

        panel.add(textField);


        frame.add(panel);//adding button in JFrame

        frame.setSize(400,500);//400 width and 500 height
        //frame.setLayout(null);//using no layout managers
        frame.pack();
        frame.setVisible(true);//making the frame visible
    }

    public interface Command {
        void runCommand(final PartOfSpeech.EnumCommandType commandType, final String[] commandArguments);
    }

    public class N implements Command {
        @Override
        public void runCommand(final PartOfSpeech.EnumCommandType commandType, final String[] commandArguments) {
            int here = 5;

            if (commandType == PartOfSpeech.EnumCommandType.MAKE) {
                // TODO< work with partOfSpeech.commandArguments >
            }
        }
    }

    private class CommandActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            final JTextField sourceTextField = (JTextField)event.getSource();

            final String commandText = sourceTextField.getText();
            sourceTextField.setText("");

            command(commandText);
        }

        /**
         * executes and or dispatches the command for execution
         *
         * @param command text of the executed command
         */
        private void command(final String command) {
            final String[] tokens = PartOfSpeech.split(command);

            PartOfSpeech partOfSpeech = new PartOfSpeech();
            try {
                partOfSpeech.parsePartOfSpeech(tokens);
            } catch (IOException e) {
                // ignore
            } catch (InstantiationException e) {
                // ignore
            } catch (InvocationTargetException e) {
                // ignore
            } catch (NoSuchMethodException e) {
                // ignore
            } catch (ParserConfigurationException e) {
                // ignore
            } catch (IllegalAccessException e) {
                // ignore
            } catch (SAXException e) {
                // ignore
            } catch (ClassNotFoundException e) {
                // ignore
            } catch (ParseException e) {
                // ignore
            } catch (Narsese.InvalidInputException e) {
                // ignore
            }

            Command command1 = new N();
            command1.runCommand(partOfSpeech.commandType, partOfSpeech.commandArguments);
        }
    }
}
