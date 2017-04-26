/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild.log;

import api.web.gw2.mapping.core.CoinAmount;
import api.web.gw2.mapping.v2.guild.id.log.GuildLogEventInfluenceActivity;
import api.web.gw2.mapping.v2.guild.id.log.GuildLogEventStashOperation;
import com.bouye.gw2.sab.SABConstants;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import api.web.gw2.mapping.v2.guild.id.log.GuildLogEvent;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class LogEventListCellController implements Initializable {

    @FXML
    private Label textLabel;
    @FXML
    private Label dateLabel;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        logEvent.addListener(logEventInvalidationListener);
    }

    /**
     * Called whenever the log event is invalidated.
     */
    private final InvalidationListener logEventInvalidationListener = observable -> updateContent();

    private void updateContent() {
        // Clear old content.
        textLabel.setText(null);
        dateLabel.setText(null);
        // Install new content.
        final Optional<GuildLogEvent> logEvent = Optional.ofNullable(getLogEvent());
        logEvent.ifPresent(this::installContent);
    }

    /**
     * Install new content.
     * @param logEvent The log event.
     */
    private void installContent(final GuildLogEvent logEvent) {
        // @todo In game time is presented as a diff from "now".
        final ZonedDateTime time = logEvent.getTime();
        dateLabel.setText(time.toString());
        final String user = logEvent.getUser();
        String text = null;
        // @todo We need to be able to resolve item ids to get localized names and rarity color.
        switch (logEvent.getType()) {
            case INFLUENCE:
                // @todo influence events return by the Web API seem incomplete or buggy.
                final Optional<GuildLogEventInfluenceActivity> activity = logEvent.getActivity();
                switch (activity.get()) {
                    case DAILY_LOGIN:
                        text = SABConstants.I18N.getString("guild-log.influence.daily-login.label"); // NOI18N.
                        break;
                    case GIFTED:
                        text = SABConstants.I18N.getString("guild-log.influence.gifted.label"); // NOI18N.
                        break;
                    default:
                }
                break;
            case INVITED: {
                final Optional<String> invitedBy = logEvent.getInvitedBy();
                final String inviter = invitedBy.isPresent() ? invitedBy.get() : ""; // NOI18N.
                text = SABConstants.I18N.getString("guild-log.invited.label"); // NOI18N.
                text = String.format(text, inviter, user);
            }
            break;
            case INVITE_DECLINED: {
                text = SABConstants.I18N.getString("guild-log.declined.label"); // NOI18N.
                text = String.format(text, user);
            }
            break;
            case JOINED: {
                text = SABConstants.I18N.getString("guild-log.joined.label"); // NOI18N.
                text = String.format(text, user);
            }
            break;
            case KICK: {
                final Optional<String> kickedBy = logEvent.getKickedBy();
                final String kicker = kickedBy.isPresent() ? kickedBy.get() : ""; // NOI18N.
                if (user.equals(kicker)) {
                    text = SABConstants.I18N.getString("guild-log.kick-self.label"); // NOI18N.
                    text = String.format(text, user);
                } else {
                    text = SABConstants.I18N.getString("guild-log.kick.label"); // NOI18N.
                    text = String.format(text, kicker, user);
                }
            }
            break;
            case MOTD: {
                final Optional<String> motd = logEvent.getMotd();
                final String message = motd.isPresent() ? motd.get() : ""; // NOI18N.
                text = message;
            }
            break;
            case RANK_CHANGE: {
                final Optional<String> newRank = logEvent.getNewRank();
                final String promotion = newRank.isPresent() ? newRank.get() : ""; // NOI18N.
                final Optional<String> changeBy = logEvent.getChangedBy();
                final String promoter = changeBy.isPresent() ? changeBy.get() : ""; // NOI18N.
                text = SABConstants.I18N.getString("guild-log.rank-change.label"); // NOI18N.
                text = String.format(text, promoter, user, promotion);
            }
            break;
            case STASH: {
                final Optional<GuildLogEventStashOperation> operation = logEvent.getOperation();
                final String op = operation.isPresent() ? ((operation.get() == GuildLogEventStashOperation.DEPOSIT) ? "deposited" : "withdrewn") : "";
                final OptionalInt itemId = logEvent.getItemId();
                if (itemId.isPresent()) {
                    final String item = itemId.isPresent() ? String.valueOf(itemId.getAsInt()) : ""; // NOI18N.
                    final OptionalInt count = logEvent.getCount();
                    final String number = count.isPresent() ? String.valueOf(count.getAsInt()) : ""; // NOI18N.
                    text = SABConstants.I18N.getString("guild-log.stash.object.label"); // NOI18N.
                    text = String.format(text, user, op, item, number);
                } else {
                    final Optional<CoinAmount> coins = logEvent.getCoins();
                    final String currency = coins.isPresent() ? coins.get().toString() : ""; // NOI18N.
                    text = SABConstants.I18N.getString("guild-log.stash.coin.label"); // NOI18N.
                    text = String.format(text, op, user, currency);
                }
            }
            break;
            case TREASURY: {
                final OptionalInt itemId = logEvent.getItemId();
                final String item = itemId.isPresent() ? String.valueOf(itemId.getAsInt()) : ""; // NOI18N.
                final OptionalInt count = logEvent.getCount();
                final String number = count.isPresent() ? String.valueOf(count.getAsInt()) : ""; // NOI18N.
                text = SABConstants.I18N.getString("guild-log.treasury.label"); // NOI18N.
                text = String.format(text, user, item, number);
            }
            break;
            case UPGRADE: {
                final OptionalInt upgradeId = logEvent.getUpgradeId();
                final String upgrade = upgradeId.isPresent() ? String.valueOf(upgradeId.getAsInt()) : ""; // NOI18N.
                text = SABConstants.I18N.getString("guild-log.upgrade.label"); // NOI18N.
                text = String.format(text, user, upgrade);
            }
            break;
            default:
        }
        textLabel.setText(text);
    }

    /**
     * The log event to display.
     */
    private final ObjectProperty<GuildLogEvent> logEvent = new SimpleObjectProperty(this, "logEvent", null); // NOI18N.

    public final GuildLogEvent getLogEvent() {
        return logEvent.getValue();
    }

    public final void setLogEvent(final GuildLogEvent value) {
        logEvent.setValue(value);
    }

    public final ObjectProperty<GuildLogEvent> logEventProperty() {
        return logEvent;
    }
}
