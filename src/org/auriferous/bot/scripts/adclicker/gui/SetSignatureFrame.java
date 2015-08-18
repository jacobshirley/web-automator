package org.auriferous.bot.scripts.adclicker.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.auriferous.bot.scripts.adclicker.AdClicker;

public class SetSignatureFrame extends JFrame {
	public SetSignatureFrame(final AdClicker adClicker) {
		super("Set signature");
		
		final JTextArea textArea = new JTextArea();
		
		textArea.setText(adClicker.currentSignature);
		
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
				adClicker.currentSignature = textArea.getText();
				
				dispose();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		content.add(buttonPanel, BorderLayout.SOUTH);
		
		setContentPane(content);
		
		setSize(500, 300);
		setVisible(true);
		setLocationRelativeTo(null);
	}
}
