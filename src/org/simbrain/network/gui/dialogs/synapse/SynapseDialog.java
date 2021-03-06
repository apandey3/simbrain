/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.network.gui.dialogs.synapse;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.simbrain.network.core.NeuronUpdateRule.InputType;
import org.simbrain.network.core.Synapse;
import org.simbrain.network.gui.nodes.SynapseNode;
import org.simbrain.util.SimbrainConstants;
import org.simbrain.util.StandardDialog;
import org.simbrain.util.widgets.ShowHelpAction;

/**
 * The <b>SynapseDialog</b> is initialized with a list of synapses. When the
 * dialog is closed the synapses are changed based on the state of the dialog.
 */
public final class SynapseDialog extends StandardDialog {

    /**
     * Main panel for editing synapses.
     *
     * @see SynapsePropertiesSimple
     */
    private SynapsePropertiesPanel synapseEditingPanel;

    /**
     * Help Button. Links to information about the currently selected synapse
     * update rule.
     */
    private final JButton helpButton = new JButton("Help");

    /** Show Help Action. The action executed by the help button */
    private ShowHelpAction helpAction;

    /** The synapses being modified. */
    private ArrayList<Synapse> synapseList;

    /**
     * Creates a synapse dialog from a collection of SynapseNodes.
     *
     * @param selectedSynapses
     *            the nodes
     * @return the dialog.
     */
    public static SynapseDialog createSynapseDialog(
            final Collection<SynapseNode> selectedSynapses) {
        SynapseDialog sd = createSynapseDialog(getSynapses(selectedSynapses));
        return sd;
    }
    
    public static SynapseDialog createSynapseDialog(
            final Collection<SynapseNode> selectedSynapses,
            final Frame parent) {
        SynapseDialog sd = new SynapseDialog(getSynapses(selectedSynapses),
                parent);
        sd.addListeners();
        return sd;
    }

    /**
     * Creates synapse dialog from a collection of synapses.
     *
     * @param selectedSynapses
     *            the synapses
     * @return the dialog.
     */
    public static SynapseDialog createSynapseDialog(
            final List<Synapse> selectedSynapses) {
        SynapseDialog sd = new SynapseDialog(selectedSynapses);
        sd.addListeners();
        return sd;
    }

    /**
     * @param synapseList
     *            the logical synapses being adjusted
     */
    private SynapseDialog(final Collection<Synapse> synapseList) {
        this.synapseList = (ArrayList<Synapse>) synapseList;
        synapseEditingPanel = SynapsePropertiesPanel
                .createSynapsePropertiesPanel(synapseList, this);
        initializeLayout();
        updateHelp();
    }

    private SynapseDialog(final Collection<Synapse> synapseList,
            final Frame parent) {
        super(parent, "Synapse Dialog");
        this.synapseList = (ArrayList<Synapse>) synapseList;
        synapseEditingPanel = SynapsePropertiesPanel
                .createSynapsePropertiesPanel(synapseList, this);
        initializeLayout();
        updateHelp();
    }
    
    /**
     * Gets the logical synapses from a list of gui Synapse Nodes
     *
     * @param selectedSynapses
     *            the selected Synapse Node gui objects
     * @return the synapses contained within the slected synapse nodes
     */
    private static ArrayList<Synapse> getSynapses(
            final Collection<SynapseNode> selectedSynapses) {
        ArrayList<Synapse> sl = new ArrayList<Synapse>();
        for (SynapseNode s : selectedSynapses) {
            sl.add(s.getSynapse());
        }
        return sl;
    }

    /**
     * Initializes the components on the panel.
     */
    private void initializeLayout() {
        setTitle("Synapse Dialog");
        JScrollPane scroller = new JScrollPane(synapseEditingPanel);
        scroller.setBorder(null);
        setContentPane(scroller);
        this.addButton(helpButton);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void closeDialogOk() {
        super.closeDialogOk();
        commitChanges();
    }

    /**
     * Add listeners to the components of the dialog
     */
    private void addListeners() {
        synapseEditingPanel.getUpdateInfoPanel().getCbSynapseType()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                updateHelp();
                            }
                        });
                    }
                });
    }

    /**
     * Set the help page based on the currently selected synapse type.
     */
    public void updateHelp() {
        if (synapseEditingPanel.getUpdateInfoPanel().getCbSynapseType()
                .getSelectedItem() == SimbrainConstants.NULL_STRING) {
            helpAction = new ShowHelpAction("Pages/Network/synapse.html");
        } else {
            String name = (String) synapseEditingPanel.getUpdateInfoPanel()
                    .getCbSynapseType().getSelectedItem();
            helpAction = new ShowHelpAction("Pages/Network/synapse/" + name
                    + ".html");
        }
        helpButton.setAction(helpAction);
    }

    /**
     * Called externally when the dialog is closed, to commit any changes made.
     */
    public void commitChanges() {

        synapseEditingPanel.commitChanges();

        // Notify the network that changes have been made
        synapseList.get(0).getNetwork().fireSynapsesUpdated(synapseList);
    }

    /**
     * @return the list of synapses which are being edited by this dialog
     */
    public ArrayList<Synapse> getSynapseList() {
        return synapseList;
    }

    /**
     * Tests to make sure that at least one source neuron in the provided list
     * of synapses is a spiking neuron. . This is used to determine if a spike
     * responder panel should or shouldn't be displayed.
     *
     * @param synapses
     *            the synapses whose source neurons will be tested.
     * @return whether or not at least one of the synapses has a spike responder
     */
    public static boolean targsUseSynapticInputs(Collection<Synapse> synapses)
    {
    	if (synapses.isEmpty()) {return true;}
    	if (synapses.size() == 1) {
    		Iterator<Synapse> synIter = synapses.iterator();
    		if (synIter.next().getTarget() == null) {
    			// Assumed template synapses (only possible synapses w/ out
    			// source or target, are assumed to contain a spike responder
    			return true;
    		}
    	}
        for (Synapse s : synapses) {
        	if (s.getTarget() != null) {
        		if (s.getTarget().getUpdateRule().getInputType()
        				== InputType.SYNAPTIC) {
        			return true;
        		}
        	}
        }
        return false;
    }

}
