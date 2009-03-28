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
package org.simbrain.workspace.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Open data world in current workspace.
 */
public final class OpenDataWorldAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    /**
     * Create an open data world with the specified
     * workspace.
     */
    public OpenDataWorldAction() {
        super("Data World");
    }


    /** @see AbstractAction */
    public void actionPerformed(final ActionEvent event) {
//        workspace.addDataWorld(false);
//        if (!workspace.getLastDataWorld().openWorld()) {
//            workspace.getLastDataWorld().dispose();
//            workspace.getDataWorldList().remove(workspace.getLastDataWorld());
//        } else {
//            workspace.getLastDataWorld().setVisible(true);
//        }
    }
}