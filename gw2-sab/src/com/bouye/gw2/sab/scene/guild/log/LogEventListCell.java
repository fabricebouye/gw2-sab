/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild.log;

import api.web.gw2.mapping.core.CoinAmount;
import api.web.gw2.mapping.v2.guild.id.log.LogEvent;
import api.web.gw2.mapping.v2.guild.id.log.LogEventStashOperation;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.OptionalInt;
import javafx.scene.control.ListCell;

/**
 * List cell for guild log events.
 * @author Fabrice Bouyé
 */
public final class LogEventListCell extends ListCell<LogEvent> {

    /**
     * Creates a new instance.
     */
    public LogEventListCell() {
    }

    @Override
    protected void updateItem(final LogEvent logEvent, final boolean empty) {
        super.updateItem(logEvent, empty);
        String text = null;
        boolean wrapText = false;
        if (!empty && logEvent != null) {
            final ZonedDateTime time = logEvent.getTime();
            final String user = logEvent.getUser();
            switch (logEvent.getType()) {
                case INFLUENCE:
                    break;
                case INVITED: {
                    final Optional<String> invitedBy = logEvent.getInvitedBy();
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
                    final Optional<String> kickedBy = logEvent.getKickedBy();
                    final String kicker = kickedBy.isPresent() ? kickedBy.get() : "";
                    if (user.equals(kicker)) {
                        text = String.format("%s - %s decided to quit the guild.", time.toString(), user);
                    } else {
                        text = String.format("%s - %s kicked %s from the guild.", time.toString(), kicker, user);
                    }
                }
                break;
                case MOTD: {
                    final Optional<String> motd = logEvent.getMotd();
                    final String message = motd.isPresent() ? motd.get() : "";
                    text = String.format("%s - %s has edited the message of the day:\n%s.", time.toString(), user, message);
                    wrapText = true;
                }
                break;
                case RANK_CHANGE: {
                    final Optional<String> newRank = logEvent.getNewRank();
                    final String promotion = newRank.isPresent() ? newRank.get() : "";
                    final Optional<String> changeBy = logEvent.getChangedBy();
                    final String promoter = changeBy.isPresent() ? changeBy.get() : "";
                    text = String.format("%s - %s has decided that %s will now be %s.", time.toString(), promoter, user, promotion);
                }
                break;
                case STASH: {
                    final Optional<LogEventStashOperation> operation = logEvent.getOperation();
                    final String op = operation.isPresent() ? ((operation.get() == LogEventStashOperation.DEPOSIT) ? "deposited" : "withdrawn") : "";
                    final OptionalInt itemId = logEvent.getItemId();
                    if (itemId.isPresent()) {
                        final String item = itemId.isPresent() ? String.valueOf(itemId.getAsInt()) : "";
                        final OptionalInt count = logEvent.getCount();
                        final String number = count.isPresent() ? String.valueOf(count.getAsInt()) : "";
                        text = String.format("%s - %s has %s %s x %s in the guild's stash.", time.toString(), user, op, number, item);
                    } else {
                        final Optional<CoinAmount> coins = logEvent.getCoins();
                        final String currency = coins.isPresent() ? coins.get().toString() : "";
                        text = String.format("%s - %s has %s %s in the guild's stash.", time.toString(), op, user, currency);
                    }
                }
                break;
                case TREASURY: {
                    final OptionalInt itemId = logEvent.getItemId();
                    final String item = itemId.isPresent() ? String.valueOf(itemId.getAsInt()) : "";
                    final OptionalInt count = logEvent.getCount();
                    final String number = count.isPresent() ? String.valueOf(count.getAsInt()) : "";
                    text = String.format("%s - %s has deposited %s x %s in the guild's treasury.", time.toString(), user, number, item);
                }
                break;
                case UPGRADE: {
                    final OptionalInt upgradeId = logEvent.getUpgradeId();
                    final String upgrade = upgradeId.isPresent() ? String.valueOf(upgradeId.getAsInt()) : "";
                    text = String.format("%s - %s has planned upgrade %s.", time.toString(), user, upgrade);
                }
                break;
                default:
            }
        }
        setText(text);
        setWrapText(wrapText);
    }
}
