/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild.log;

import api.web.gw2.mapping.v2.guild.id.log.LogEvent;
import java.time.ZonedDateTime;
import java.util.Optional;
import javafx.scene.control.ListCell;

/**
 * Lis cell for guild log events.
 * @author Fabrice Bouyé
 */
public final class LogEventListCell extends ListCell<LogEvent> {

    /**
     * Creates a new instance.
     */
    public LogEventListCell() {
    }

    @Override
    protected void updateItem(final LogEvent item, final boolean empty) {
        super.updateItem(item, empty);
        String text = null;
        boolean wrapText = false;
        if (!empty && item != null) {
            final ZonedDateTime time = item.getTime();
            final String user = item.getUser();
            switch (item.getType()) {
                case INFLUENCE:
                    break;
                case INVITED: {
                    final Optional<String> invitedBy = item.getInvitedBy();
                    final String inviter = invitedBy.isPresent() ? invitedBy.get() : "";
                    text = String.format("%s - %s has invited %s into the guild.", time.toString(), inviter, user);
                }
                break;
                case INVITE_DECLINED: {
                    text = String.format("%s - %s declined to join the guild.", time.toString(), user);
                }
                break;
                case JOINED: {
                    text = String.format("%s - %s has joined the guild.", time.toString(), user);
                }
                break;
                case KICK: {
                    final Optional<String> kickedBy = item.getKickedBy();
                    final String kicker = kickedBy.isPresent() ? kickedBy.get() : "";
                    if (user.equals(kicker)) {
                        text = String.format("%s - %s decided to quit the guild.", time.toString(), user);
                    } else {
                        text = String.format("%s - %s kicked %s from the guild.", time.toString(), kicker, user);
                    }
                }
                break;
                case MOTD: {
                    final Optional<String> motd = item.getMotd();
                    final String message = motd.isPresent() ? motd.get() : "";
                    text = String.format("%s - %s has edited the message of the day:\n%s.", time.toString(), user, message);
                    wrapText = true;
                }
                break;
                case RANK_CHANGE: {
                    final Optional<String> newRank = item.getNewRank();
                    final String promotion = newRank.isPresent() ? newRank.get() : "";
                    final Optional<String> changeBy = item.getChangedBy();
                    final String promoter = changeBy.isPresent() ? changeBy.get() : "";
                    text = String.format("%s - %s has decided that %s will now be %s.", time.toString(), promoter, user, promotion);
                }
                break;
                case STASH:
                    break;
                case TREASURY:
                    break;
                case UPGRADE:
                    break;
                default:
            }
        }
        setText(text);
        setWrapText(wrapText);
    }
}
