/*
 * Copyright (c) 2008, SQL Power Group Inc.
 *
 * This file is part of Power*Architect.
 *
 * Power*Architect is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Power*Architect is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package ca.sqlpower.architect.swingui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Logger;

import ca.sqlpower.swingui.SPSUtils;
import ca.sqlpower.swingui.SPSUtils.FileExtensionFilter;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.FontMetrics;

public class CompareDMFrame extends JDialog {

	private static Logger logger = Logger.getLogger(CompareDMFrame.class);
	private JTextPane leftOutputArea;
	private JTextPane rightOutputArea;
	
	private AbstractDocument sourceOutputText;
	private AbstractDocument targetOutputText;

	private String title;
    private String whatTheHeckIsGoingOn;
	private JComponent panel;
			
	public CompareDMFrame(Dialog owner, AbstractDocument sourceOutputText, AbstractDocument targetOutputText, 
						String leftTitle, String rightTitle)
	{
		super(owner, Messages.getString("CompareDMFrame.title"));	 //$NON-NLS-1$
		
		this.sourceOutputText = sourceOutputText;
		this.targetOutputText = targetOutputText;
		this.title = Messages.getString("CompareDMFrame.comparing", leftTitle, rightTitle); //$NON-NLS-1$
        whatTheHeckIsGoingOn =Messages.getString("CompareDMFrame.necessaryChanges");	 //$NON-NLS-1$
		panel = mainFrame();
		getContentPane().add(panel);
		
		SimpleAttributeSet att = new SimpleAttributeSet();
		
		StyleConstants.setForeground(att, Color.black);
		StyleConstants.setFontSize(att,leftOutputArea.getFont().getSize() * 2);

		try {
			sourceOutputText.insertString(0,leftTitle + "\n\n",att); //$NON-NLS-1$
			targetOutputText.insertString(0,rightTitle + "\n\n",att); //$NON-NLS-1$
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
        pack();
        setLocationRelativeTo(owner);
	}
	
	public JComponent mainFrame() {		
		
		FormLayout layout = new FormLayout(
				"4dlu,fill:min(150dlu;default):grow, 6dlu, fill:min(150dlu;default):grow, 4dlu", // columns //$NON-NLS-1$
				" min(300dlu;default), 6dlu, min(300dlu;default), 6dlu,  min(300dlu;default), 3dlu, fill:min(300dlu;default):grow, 3dlu, 20dlu,6dlu,20dlu"); // rows //$NON-NLS-1$
		

		CellConstraints cc = new CellConstraints();
		JLabel titleLabel = new JLabel(title);
        Font oldFont  = titleLabel.getFont();
		Font titleFont = new Font(oldFont.getName(),oldFont.getStyle(),oldFont.getSize()*2);
		
		titleLabel.setFont(titleFont);
		JLabel subTitleLabel = new JLabel(whatTheHeckIsGoingOn);
		leftOutputArea = new JTextPane();
		leftOutputArea.setMargin(new Insets(6, 10, 4, 6));
		leftOutputArea.setDocument(sourceOutputText);
		leftOutputArea.setEditable(false);
		JPanel comparePanel =  new JPanel(new GridLayout(1,2));
		JScrollPane sp = new JScrollPane(comparePanel);
		
		int lineHeight = 16;
		try {
			FontMetrics fm = leftOutputArea.getFontMetrics(leftOutputArea.getFont());
			lineHeight = fm.getHeight() + 2;
		} catch (Exception e) {
			lineHeight = 16;
		}
		// If the increments are not set, klicking on the up or down arrow of the scrollbar
		// will scroll the display by one pixel, which is definitely not what the user wants
		// by setting unitIncrement to the font's height the display will scroll by approx. one line
		sp.getVerticalScrollBar().setUnitIncrement(lineHeight);

		// Clicking in the "empty" area of the scrollbar will scroll by 10 lines
		sp.getVerticalScrollBar().setBlockIncrement(lineHeight * 10);

		comparePanel.add(leftOutputArea);
		Action sourceCopy = new sourceCopyAction(sourceOutputText);
	
		Action sourceSave = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				SPSUtils.saveDocument(CompareDMFrame.this,
				        sourceOutputText,
						(FileExtensionFilter) SPSUtils.TEXT_FILE_FILTER );
			}
		};
		CloseAction close = new CloseAction();
		close.setDialog(this);
		SPSUtils.makeJDialogCancellable(this, close);
		
		ButtonBarBuilder sourcebbBuilder = new ButtonBarBuilder();
		JButton copySource = new JButton(sourceCopy);
		copySource.setText(Messages.getString("CompareDMFrame.copy")); //$NON-NLS-1$
		sourcebbBuilder.addGridded (copySource);
		sourcebbBuilder.addRelatedGap();
		sourcebbBuilder.addGlue();
		

			
		
		JButton sourceSaveButton = new JButton(sourceSave);
		sourceSaveButton.setText(Messages.getString("CompareDMFrame.save")); //$NON-NLS-1$
		sourcebbBuilder.addGridded(sourceSaveButton);
		sourcebbBuilder.addRelatedGap();
		sourcebbBuilder.addGlue();
		
		ButtonBarBuilder closeBar = new ButtonBarBuilder(); 
		JButton closeButton = new JButton(close);
		closeButton.setText(Messages.getString("CompareDMFrame.close")); //$NON-NLS-1$
		closeBar.addGridded(closeButton);
		PanelBuilder pb;



		
		layout.setColumnGroups(new int [][] { {2,4}}); 
		JPanel p = logger.isDebugEnabled()  ? new FormDebugPanel(layout) : new JPanel(layout);

		
		pb = new PanelBuilder(layout,p);
		pb.setDefaultDialogBorder();		
		
		rightOutputArea = new JTextPane();
		rightOutputArea.setMargin(new Insets(6, 10, 4, 6));
		rightOutputArea.setDocument(targetOutputText);
		rightOutputArea.setEditable(false);
		comparePanel.add(rightOutputArea);
		Action targetCopy = new targetCopyAction(targetOutputText);
		//Sets the target Buttons
		ButtonBarBuilder targetbbBuilder = new ButtonBarBuilder();
		JButton copyTarget = new JButton(targetCopy);
		copyTarget.setText(Messages.getString("CompareDMFrame.copy")); //$NON-NLS-1$
		targetbbBuilder.addGridded (copyTarget);
		targetbbBuilder.addRelatedGap();
		targetbbBuilder.addGlue();					
		
		
		Action targetSaveAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				SPSUtils.saveDocument(CompareDMFrame.this,
						targetOutputText,
						(FileExtensionFilter) SPSUtils.TEXT_FILE_FILTER );
			}
		};
		

		JButton targetSave = new JButton(targetSaveAction);
		targetSave.setText(Messages.getString("CompareDMFrame.save")); //$NON-NLS-1$
		targetbbBuilder.addGridded(targetSave);
		targetbbBuilder.addRelatedGap();
		targetbbBuilder.addGlue();
		getRootPane().setDefaultButton(targetSave);

		
		pb.add(titleLabel, cc.xyw(2, 1, 3,"c,c")); //$NON-NLS-1$
        pb.add(subTitleLabel,cc.xyw(2, 3, 3,"c,c")); //$NON-NLS-1$
		pb.add(new JLabel(Messages.getString("CompareDMFrame.older")), cc.xy(2,5)); //$NON-NLS-1$
		pb.add(new JLabel(Messages.getString("CompareDMFrame.newer")), cc.xy(4,5)); //$NON-NLS-1$
		pb.add(sp, cc.xyw(2, 7,3));
		pb.add(sourcebbBuilder.getPanel(), cc.xy(2, 9, "l,c")); //$NON-NLS-1$
		pb.add(targetbbBuilder.getPanel(), cc.xy(4, 9, "r,c")); //$NON-NLS-1$
		pb.add(closeBar.getPanel(), cc.xy(4,11, "r,c")); //$NON-NLS-1$
	
		return pb.getPanel();
	}
	
	public class sourceCopyAction extends AbstractAction{

		AbstractDocument doc;
		public sourceCopyAction(AbstractDocument doc)
		{
			this.doc = doc;
			
		}
		
		public void actionPerformed(ActionEvent e) {
			
			try {
				StringSelection selection = new StringSelection(doc.getText(0,doc.getLength()));
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection,selection);
			} catch (BadLocationException e1) {
				logger.debug("Unable to get the text for copying"+ e1); //$NON-NLS-1$
			}
			
		}			
	}
	
	public class targetCopyAction extends AbstractAction{

		AbstractDocument doc;
		public targetCopyAction(AbstractDocument doc)
		{
			this.doc = doc;
		}
		
		public void actionPerformed(ActionEvent e) {
			
			try {
				StringSelection selection = new StringSelection(doc.getText(0,doc.getLength()));
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection,selection);
			} catch (BadLocationException e1) {
				logger.debug("Unable to get the text for copying"+ e1); //$NON-NLS-1$
			}
			
		}			
	}

	public class CloseAction extends AbstractAction {	
		JDialog localDialog;
		
		public void setDialog(JDialog dialog){
			localDialog = dialog;				
		}
		public void actionPerformed(ActionEvent e) {
			localDialog.setVisible(false);
		}						
	}
	
	

	public JPanel getPanel() {
		return (JPanel) panel;
	}

	public void setPanel(JPanel panel) {
		this.panel = panel;
	}
	
	@Override
	public void pack() {
		super.pack();
		Dimension d =Toolkit.getDefaultToolkit().getScreenSize();
		logger.debug("Before change: Window width ="+getWidth() + " screen width ="+d.width); //$NON-NLS-1$ //$NON-NLS-2$
		if (getWidth() > d.width - getX()) {
			setSize(d.width-getX(),getHeight());
		}
		logger.debug("Before change: Window height ="+getHeight() + " screen height ="+d.height); //$NON-NLS-1$ //$NON-NLS-2$
		if (getHeight() > d.height-getY()) {
			setSize(getWidth(),d.height-getY());
		}
	}
	
	
}
