package org.auriferous.bot.scripts.adclicker.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.auriferous.bot.scripts.adclicker.AdClicker;

public class SetSignatureFrame extends JFrame {
	final JTextArea textArea = new JTextArea();
	
	public SetSignatureFrame(final AdClicker adClicker) {
		super("Set signature");

		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JScrollPane listScrollPane = new JScrollPane(textArea);
		
		content.add(listScrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				
				adClicker.signatureConfig.setValue(textArea.getText());
			}
		});
		
		buttonPanel.add(okButton);

		content.add(buttonPanel, BorderLayout.SOUTH);
		
		setContentPane(content);
		
		setSize(500, 300);
		setVisible(false);
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				
				adClicker.signatureConfig.setValue(textArea.getText());
			}
		});
	}
	
	public void setText(String s) {
		textArea.setText(s);
	}
}
