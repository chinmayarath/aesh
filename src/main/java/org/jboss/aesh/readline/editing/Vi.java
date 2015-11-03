/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.aesh.readline.editing;

import org.jboss.aesh.readline.Action;
import org.jboss.aesh.readline.ActionEvent;
import org.jboss.aesh.readline.KeyEvent;
import org.jboss.aesh.readline.actions.ActionMapper;
import org.jboss.aesh.terminal.Key;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class Vi extends BaseEditMode {

    private Status status;
    private Status prevStatus;

    private Action previousAction;

    private ActionEvent currentAction;

    Vi() {
    }

    @Override
    public Action parse(KeyEvent event) {
        //are we already searching, it need to be processed by search action
        if(currentAction != null) {
            if(currentAction.keepFocus()) {
                currentAction.input(getAction(event), event);
                return currentAction;
            }
            else
                currentAction = null;
        }

        return getAction(event);
    }

    private Action getAction(KeyEvent event) {
        if(event instanceof Key) {
            parseKeyEvent((Key) event);
        }

        if(actions.containsKey(event)) {
            Action action =  actions.get(event);
            if(action instanceof ActionEvent) {
                currentAction = (ActionEvent) action;
                currentAction.input(action, event);
            }
            return action;
        }
        else {
            return null;
        }
    }

    private void parseKeyEvent(Key event) {
        if(searchMode()) {
            if(Key.ESC == event) {
                status = Status.EDIT;
            }
        }

    }

    public Vi addAction(Key key, String action) {
        actions.put(key, ActionMapper.mapToAction(action));
        return this;
    }

    public Vi addAction(Key key, Action action) {
        actions.put(key, action);
        return this;
    }

    private boolean deleteMode() {
        return status == Status.DELETE;
    }

    private boolean changeMode() {
        return status == Status.CHANGE;
    }

    private boolean replaceMode() {
        return status == Status.REPLACE;
    }

    private boolean yankMode() {
        return status == Status.YANK;
    }

    private boolean searchMode() {
        return status == Status.SEARCH;
    }

}