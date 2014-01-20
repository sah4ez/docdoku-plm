/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2013 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.docdoku.client.actions;

import com.docdoku.client.data.MainModel;
import com.docdoku.client.localization.I18N;
import com.docdoku.client.ui.ExplorerFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class RefreshAction extends ClientAbstractAction {
    public RefreshAction(ExplorerFrame pOwner) {
        super(I18N.BUNDLE.getString("Refresh_title"), "/com/docdoku/client/resources/icons/refresh.png", pOwner);
        putValue(Action.SHORT_DESCRIPTION, I18N.BUNDLE.getString("Refresh_short_desc"));
        putValue(Action.LONG_DESCRIPTION, I18N.BUNDLE.getString("Refresh_long_desc"));
        putValue(Action.MNEMONIC_KEY, new Integer(I18N.getCharBundle("Refresh_mnemonic_key")));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        setLargeIcon("/com/docdoku/client/resources/icons/refresh_large.png");
    }

    @Override
    public void actionPerformed(ActionEvent pAE) {
        MainModel.getInstance().updater.clear();
    }
}
