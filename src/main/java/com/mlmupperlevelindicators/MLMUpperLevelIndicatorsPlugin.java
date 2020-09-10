/*
 * Copyright (c) 2020, Cyborger1
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.mlmupperlevelindicators;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import static net.runelite.api.AnimationID.*;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import static net.runelite.api.ObjectID.DEPLETED_VEIN_26665;
import static net.runelite.api.ObjectID.DEPLETED_VEIN_26666;
import static net.runelite.api.ObjectID.DEPLETED_VEIN_26667;
import static net.runelite.api.ObjectID.DEPLETED_VEIN_26668;
import static net.runelite.api.ObjectID.ORE_VEIN_26661;
import static net.runelite.api.ObjectID.ORE_VEIN_26662;
import static net.runelite.api.ObjectID.ORE_VEIN_26663;
import static net.runelite.api.ObjectID.ORE_VEIN_26664;
import net.runelite.api.Perspective;
import net.runelite.api.WallObject;
import net.runelite.api.coords.Angle;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "MLM Upper Level Indicators",
	description = "Helps you remember to go back and clean up your one-rocks!",
	tags = {"Motherlode", "Mine", "Indicator", "MLM"}
)
public class MLMUpperLevelIndicatorsPlugin extends Plugin
{
	// From official Motherlode plugin
	private static final int UPPER_FLOOR_HEIGHT = -490;
	private static final Set<Integer> MOTHERLODE_MAP_REGIONS = ImmutableSet.of(14679, 14680, 14681, 14935, 14936, 14937, 15191, 15192, 15193);
	private static final Set<Integer> MINE_SPOTS = ImmutableSet.of(ORE_VEIN_26661, ORE_VEIN_26662, ORE_VEIN_26663, ORE_VEIN_26664);
	private static final Set<Integer> DEPLETED_SPOTS = ImmutableSet.of(DEPLETED_VEIN_26665, DEPLETED_VEIN_26666, DEPLETED_VEIN_26667, DEPLETED_VEIN_26668);
	private static final Set<Integer> MINING_ANIMATION_IDS = ImmutableSet.of(
		MINING_MOTHERLODE_BRONZE, MINING_MOTHERLODE_IRON, MINING_MOTHERLODE_STEEL,
		MINING_MOTHERLODE_BLACK, MINING_MOTHERLODE_MITHRIL, MINING_MOTHERLODE_ADAMANT,
		MINING_MOTHERLODE_RUNE, MINING_MOTHERLODE_GILDED, MINING_MOTHERLODE_DRAGON,
		MINING_MOTHERLODE_DRAGON_UPGRADED, MINING_MOTHERLODE_DRAGON_OR, MINING_MOTHERLODE_INFERNAL,
		MINING_MOTHERLODE_CRYSTAL
	);

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MLMUpperLevelIndicatorsConfig config;

	@Inject
	private MLMUpperLevelIndicatorsOverlay overlay;

	@Getter(AccessLevel.PACKAGE)
	private boolean inMLM;

	@Getter(AccessLevel.PACKAGE)
	private final Map<WorldPoint, OreVeinState> oreVeinStateMap = new HashMap<>();

	@Provides
	MLMUpperLevelIndicatorsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MLMUpperLevelIndicatorsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		inMLM = checkInMLM();
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		oreVeinStateMap.clear();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOADING:
				oreVeinStateMap.clear();
				inMLM = checkInMLM();
				break;
			case LOGIN_SCREEN:
				inMLM = false;
				break;
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged animationChanged)
	{
		if (!inMLM)
		{
			return;
		}

		Actor actor = animationChanged.getActor();
		if (!MINING_ANIMATION_IDS.contains(actor.getAnimation()))
		{
			return;
		}

		WorldPoint target = getWorldLocationInFront(actor);
		LocalPoint localTarget = LocalPoint.fromWorld(client, target);

		if (localTarget != null && isUpstairs(localTarget))
		{
			WallObject obj = client.getScene().getTiles()[0][localTarget.getSceneX()][localTarget.getSceneY()].getWallObject();
			if (obj != null && MINE_SPOTS.contains(obj.getId()))
			{
				OreVeinState prevState = oreVeinStateMap.getOrDefault(target, OreVeinState.Untouched);
				if (prevState != OreVeinState.MinedBySelf)
				{
					OreVeinState newState = actor == client.getLocalPlayer() ?
						OreVeinState.MinedBySelf : OreVeinState.MinedByOther;
					oreVeinStateMap.put(target, newState);
				}
			}
		}
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned event)
	{
		if (!inMLM)
		{
			return;
		}

		WallObject obj = event.getWallObject();
		if (DEPLETED_SPOTS.contains(obj.getId()) && isUpstairs(obj.getLocalLocation()))
		{
			oreVeinStateMap.remove(obj.getWorldLocation());
		}
	}

	// From official Motherlode plugin
	private boolean checkInMLM()
	{
		GameState gameState = client.getGameState();
		if (gameState != GameState.LOGGED_IN
			&& gameState != GameState.LOADING)
		{
			return false;
		}

		// Verify that all regions exist in MOTHERLODE_MAP_REGIONS
		for (int region : client.getMapRegions())
		{
			if (!MOTHERLODE_MAP_REGIONS.contains(region))
			{
				return false;
			}
		}

		return true;
	}

	// From official Motherlode plugin
	boolean isUpstairs(LocalPoint localPoint)
	{
		return Perspective.getTileHeight(client, localPoint, 0) < UPPER_FLOOR_HEIGHT;
	}

	private static WorldPoint getWorldLocationInFront(Actor actor)
	{
		final Direction orientation = new Angle(actor.getOrientation()).getNearestDirection();
		int dx = 0, dy = 0;

		switch (orientation)
		{
			case SOUTH:
				dy = -1;
				break;
			case WEST:
				dx = -1;
				break;
			case NORTH:
				dy = 1;
				break;
			case EAST:
				dx = 1;
				break;
		}

		final WorldPoint currWP = actor.getWorldLocation();
		return new WorldPoint(currWP.getX() + dx, currWP.getY() + dy, currWP.getPlane());
	}
}
