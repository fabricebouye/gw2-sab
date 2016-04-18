/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.tasks.world;

import api.web.gw2.mapping.v2.worlds.World;
import com.bouye.gw2.sab.query.WebQuery;
import java.util.List;
import javafx.concurrent.Task;

/**
 * This task queries the v2/worlds endpoint to resolve world.
 * @author Fabrice Bouyé
 */
public final class WorldSolverTask extends Task<List<World>> {

    /**
     * World ids to resolve.
     */
    private final int[] ids;

    /**
     * Creates a new instance.
     * @param ids World ids to resolve.
     * <br>If no id is provided, all worlds are returned.
     */
    public WorldSolverTask(final int... ids) {
        this.ids = ids;
    }

    @Override
    protected List<World> call() throws Exception {
        return WebQuery.INSTANCE.queryWorlds(ids);
    }
}
