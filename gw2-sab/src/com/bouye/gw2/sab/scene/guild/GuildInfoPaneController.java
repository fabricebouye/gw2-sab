/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild;

import api.web.gw2.mapping.core.CoinAmount;
import api.web.gw2.mapping.v1.guilddetails.GuildDetails;
import api.web.gw2.mapping.v2.guild.id.log.LogEvent;
import api.web.gw2.mapping.v2.guild.id.log.LogEventType;
import api.web.gw2.mapping.v2.guild.id.log.LogEventUpgradeAction;
import api.web.gw2.mapping.v2.guild.id.members.Member;
import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.scene.guild.log.LogEventListCell;
import com.bouye.gw2.sab.session.Session;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public class GuildInfoPaneController extends SABControllerBase<GuildInfoPane> {

    @FXML
    private Label guildNameLabel;
    @FXML
    private TextArea motdArea;
    @FXML
    private TableView<Member> rosterTableView;
    @FXML
    private TableColumn<Member, Boolean> onlineTableColumn;
    @FXML
    private TableColumn<Member, Object> rankTableColumn;
    @FXML
    private TableColumn<Member, String> nameTableColumn;
    @FXML
    private TableColumn<Member, Object> locationTableColumn;
    @FXML
    private TableColumn<Member, Object> lastOnlineTableColumn;
    @FXML
    private ComboBox<LogEventType> logsFilterCombo;
    @FXML
    private TextField logsSearchField;
    @FXML
    private ListView<LogEvent> logsListView;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        Bindings.select(nodeProperty(), "guildId").addListener((observable, oldValue, newValue) -> updateContent());
        nameTableColumn.setCellValueFactory(feature -> {
            final Member member = feature.getValue();
            return new SimpleStringProperty(member.getName());
        });
        //
        logsFilterCombo.getItems().setAll(LogEventType.values());
        logsFilterCombo.getItems().remove(LogEventType.UNKNOWN);
        logsFilterCombo.getItems().add(0, null);
        logsFilterCombo.valueProperty().addListener(observable -> applyLogsListFilter());
        //
        logsSearchField.textProperty().addListener(observable -> applyLogsListFilter());
        //
        filteredLogs.setPredicate(showAllLogTypesFilter);
        logsListView.setItems(filteredLogs);
        logsListView.setCellFactory(listView -> new LogEventListCell());
    }

    @Override
    protected void clearContent(final GuildInfoPane parent) {
        guildNameLabel.setText(null);
    }

    @Override
    protected void installContent(final GuildInfoPane parent) {
        final Session session = parent.getSession();
        final String guildId = parent.getGuildId();
        guildNameLabel.setText(guildId);
        updateGuildDetailstAsync(session, guildId);
        updateGuildRosterAsync(session, guildId);
        updateGuildLogsAsync(session, guildId);
    }

    /**
     * Upgrade the guild details in a background service.
     * @param session The session.
     * @param guildId The id of the guild.
     */
    private void updateGuildDetailstAsync(final Session session, final String guildId) {
        final Service<Optional<GuildDetails>> service = new Service<Optional<GuildDetails>>() {
            @Override
            protected Task<Optional<GuildDetails>> createTask() {
                return new Task<Optional<GuildDetails>>() {
                    @Override
                    protected Optional<GuildDetails> call() throws Exception {
                        final boolean isDemo = session.isDemo();
                        final List<GuildDetails> guildDetails = WebQuery.INSTANCE.queryGuildDetails(isDemo, guildId);
                        final Optional<GuildDetails> result = guildDetails.isEmpty() ? Optional.empty() : Optional.of(guildDetails.get(0));
                        return result;
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final Optional<GuildDetails> result = service.getValue();
            result.ifPresent(guild -> {
                guildNameLabel.setText(guild.getGuildName());
            });
        });
        addAndStartService(service, "updateGuildDetailstAsync");
    }

    /**
     * Upgrade the guild roster table in a background service.
     * @param session The session.
     * @param guildId The id of the guild.
     */
    private void updateGuildRosterAsync(final Session session, final String guildId) {
        final Service<List<Member>> service = new Service<List<Member>>() {
            @Override
            protected Task<List<Member>> createTask() {
                return new Task<List<Member>>() {
                    @Override
                    protected List<Member> call() throws Exception {
                        final boolean isDemo = session.isDemo();
                        final String appKey = session.getAppKey();
                        final List<Member> result = WebQuery.INSTANCE.queryGuildMembers(isDemo, appKey, guildId);
                        return result;
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final List<Member> result = service.getValue();
            rosterTableView.getItems().setAll(result);
        });
        addAndStartService(service, "updateGuildRosterAsync");
    }

    /**
     * Raw logs list.
     */
    private final ObservableList<LogEvent> logs = FXCollections.observableList(new LinkedList());
    /**
     * Filtered logs list.
     */
    private final FilteredList<LogEvent> filteredLogs = new FilteredList(logs);

    /**
     * Upgrade the guild logs list in a background service.
     * @param session The session.
     * @param guildId The id of the guild.
     */
    private void updateGuildLogsAsync(final Session session, final String guildId) {
        final Service<List<LogEvent>> service = new Service<List<LogEvent>>() {
            @Override
            protected Task<List<LogEvent>> createTask() {
                return new Task<List<LogEvent>>() {
                    @Override
                    protected List<LogEvent> call() throws Exception {
                        final boolean isDemo = session.isDemo();
                        final String appKey = session.getAppKey();
                        final List<LogEvent> result = WebQuery.INSTANCE.queryGuildLogs(isDemo, appKey, guildId);
                        return result;
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final List<LogEvent> result = service.getValue();
            logs.setAll(result);
        });
        addAndStartService(service, "updateGuildLogsAsync");
    }

    /**
     * Show all log event types.
     */
    private final Predicate<LogEvent> showAllLogTypesFilter = logEvent -> true;

    /**
     * Apply filters to the list of logs.
     */
    private void applyLogsListFilter() {
        // Type of logs.
        final LogEventType type = logsFilterCombo.getValue();
        Predicate<LogEvent> typeFilter = (type == null) ? showAllLogTypesFilter : logEvent -> logEvent.getType() == type;
        // Search.
        final String searchStr = logsSearchField.getText();
        if (searchStr != null && !searchStr.trim().isEmpty()) {
            int searchInt = Integer.MIN_VALUE;
            try {
                searchInt = Integer.parseInt(searchStr);
            } catch (NumberFormatException ex) {
                // Silently ingnore exception.
            }
            final String criteriaStr = searchStr.trim().toLowerCase();
            final int criteriaInt = searchInt;
            final Predicate<LogEvent> searchFilter = logEvent -> {
                boolean result = false;
                final String user = logEvent.getUser();
                result |= user.toLowerCase().contains(criteriaStr);
                switch (logEvent.getType()) {
                    case INFLUENCE: {
                        final OptionalInt participants = logEvent.getTotalParticipants();
                        if (participants.isPresent()) {
                            result |= participants.getAsInt() == criteriaInt;
                        }
                    }
                    break;
                    case INVITED: {
                        final Optional<String> invitedBy = logEvent.getInvitedBy();
                        if (invitedBy.isPresent()) {
                            result |= invitedBy.get().toLowerCase().contains(criteriaStr);
                        }
                    }
                    break;
                    case KICK: {
                        final Optional<String> kickedBy = logEvent.getKickedBy();
                        if (kickedBy.isPresent()) {
                            result |= kickedBy.get().toLowerCase().contains(criteriaStr);
                        }
                    }
                    break;
                    case MOTD: {
                        final Optional<String> motd = logEvent.getMotd();
                        if (motd.isPresent()) {
                            result |= motd.get().toLowerCase().contains(criteriaStr);
                        }
                    }
                    break;
                    case RANK_CHANGE: {
                        final Optional<String> oldRank = logEvent.getOldRank();
                        if (oldRank.isPresent()) {
                            result |= oldRank.get().toLowerCase().contains(criteriaStr);
                        }
                        final Optional<String> newRank = logEvent.getNewRank();
                        if (newRank.isPresent()) {
                            result |= newRank.get().toLowerCase().contains(criteriaStr);
                        }
                        final Optional<String> changeBy = logEvent.getChangedBy();
                        if (newRank.isPresent()) {
                            result |= newRank.get().toLowerCase().contains(criteriaStr);
                        }
                    }
                    break;
                    case STASH: {
                        final OptionalInt itemId = logEvent.getCount();
                        if (itemId.isPresent()) {
                            result |= itemId.getAsInt() == criteriaInt;
                        }
                        final OptionalInt count = logEvent.getCount();
                        if (count.isPresent()) {
                            result |= count.getAsInt() == criteriaInt;
                        }
                        final Optional<CoinAmount> coins = logEvent.getCoins();
                        if (coins.isPresent()) {
                            result |= coins.get().toCopper() == criteriaInt;
                            result |= coins.get().toSilver() == criteriaInt;
                            result |= coins.get().toGold() == criteriaInt;
                        }
                    }
                    break;
                    case TREASURY: {
                        final OptionalInt itemId = logEvent.getCount();
                        if (itemId.isPresent()) {
                            result |= itemId.getAsInt() == criteriaInt;
                        }
                        final OptionalInt count = logEvent.getCount();
                        if (count.isPresent()) {
                            result |= count.getAsInt() == criteriaInt;
                        }
                    }
                    break;
                    case UPGRADE: {
                        final OptionalInt upgradeId = logEvent.getUpgradeId();
                        if (upgradeId.isPresent()) {
                            result |= upgradeId.getAsInt() == criteriaInt;
                        }
                        final Optional<LogEventUpgradeAction> action = logEvent.getAction();
                        if (action.isPresent()) {
                            result |= action.get().name().toLowerCase().contains(criteriaStr);
                        }
                    }
                    break;
                    case INVITE_DECLINED:
                    case JOINED:
                    default:
                }
                return result;
            };
            typeFilter = typeFilter.and(searchFilter);
        }
        // Apply to log list.
        filteredLogs.setPredicate(typeFilter);
    }
}
