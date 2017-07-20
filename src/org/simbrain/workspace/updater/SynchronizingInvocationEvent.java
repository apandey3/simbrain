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
package org.simbrain.workspace.updater;

import java.awt.event.InvocationEvent;
import java.util.concurrent.Callable;

import org.simbrain.workspace.Workspace;

/**
 * Class used to wrap InvocationEvents such that they are synchronized when
 * executed.
 * 
 * See https://docs.oracle.com/javase/7/docs/api/java/awt/event/InvocationEvent.html
 *
 * @author Matt Watson
 */
class SynchronizingInvocationEvent extends InvocationEvent {

    /** Default serial version uid. */
    private static final long serialVersionUID = 1L;

    /** The event to synchronize. */
    private final InvocationEvent event;

    /**
     * Creates an invocation event for the provided event using the workspace
     * for synchronization and calling signal.done() when finished.
     *
     * @param event The 'real' invocation event.
     * @param workspace The workspace used for synchronization.
     * @param signal The signal to call when done.
     */
    public SynchronizingInvocationEvent(final InvocationEvent event,
            final Workspace workspace, final CompletionSignal signal) {

        super(event.getSource(), new Runnable() {
            public void run() {
                try {
                    workspace.syncOnAllComponents(new Callable<Object>() {
                        public Object call() throws Exception {
                            event.dispatch();
                            signal.done();
                            return null;
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.event = event;
    }

    @Override
    public Exception getException() {
        return event.getException();
    }

    @Override
    public Throwable getThrowable() {
        return event.getThrowable();
    }

    @Override
    public long getWhen() {
        return event.getWhen();
    }

    @Override
    public String paramString() {
        return event.paramString();
    }
}