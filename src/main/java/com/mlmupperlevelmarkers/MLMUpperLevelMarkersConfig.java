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
package com.mlmupperlevelmarkers;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(MLMUpperLevelMarkersConfig.CONFIG_GROUP_NAME)
public interface MLMUpperLevelMarkersConfig extends Config
{
	String CONFIG_GROUP_NAME = "mlmupperlevelmarkers";
	String HIGHER_RENDER_PRIORITY_KEY_NAME = "higherRenderPriority";

	@Alpha
	@ConfigItem(
		keyName = "selfMarkerColor",
		name = "Self Marker Color",
		description = "Color of markers on veins you've mined.",
		position = 1
	)
	default Color getSelfMarkerColor()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		keyName = "otherMarkerColor",
		name = "Other Marker Color",
		description = "Color of markers on veins other players have mined.",
		position = 2
	)
	default Color getOtherMarkerColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
		keyName = "tileMarkerType",
		name = "Tile Marker Type",
		description = "Choose a Tile Marker type.<br>" +
			"Hidden: Hides the tile markers, useful to reduce clutter if using marker timers.<br>" +
			"Normal: Shows regular tile markers.<br>" +
			"Contour Timer: Makes the contour of the tile markers behave as a pie-chart of sorts with the timeouts.",
		position = 3
	)
	default TileMarkerType tileMarkerType()
	{
		return TileMarkerType.NORMAL;
	}

	@ConfigItem(
		keyName = "showOtherMarkers",
		name = "Show Other Players' Markers",
		description = "Add markers to veins other players have mined.",
		position = 4
	)
	default boolean showOtherMarkers()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showOnlyOnSameLevel",
		name = "Show Only When On Same Level",
		description = "Only show markers on the same level as you.",
		position = 5
	)
	default boolean showOnlyOnSameLevel()
	{
		return true;
	}

	@ConfigItem(
		keyName = "markersToShow",
		name = "Markers to Show",
		description = "Select which levels the markers should show on.",
		position = 6
	)
	default ShowMarkerType getMarkersToShow()
	{
		return ShowMarkerType.BOTH;
	}

	// 23-27 and 36-40 are values from https://secure.runescape.com/m=news/project-rebalance-skilling--poll-81-mta-changes?oldschool=1
	@ConfigItem(
		keyName = "firstTimeoutUL",
		name = "(Upper) First Timeout",
		description = "(Upper level) Darkens the marker after a vein has been first mined for this long (-1 to disable).",
		position = 7
	)
	@Units(Units.SECONDS)
	@Range(min = -1)
	default int getFirstTimeoutUL()
	{
		return 36;
	}

	@ConfigItem(
		keyName = "secondTimeoutUL",
		name = "(Upper) Second Timeout",
		description = "(Upper level) Darkens the marker again after a vein has been first mined for this long (-1 to disable).",
		position = 8
	)
	@Units(Units.SECONDS)
	@Range(min = -1)
	default int getSecondTimeoutUL()
	{
		return 40;
	}
	@ConfigItem(
		keyName = "firstTimeoutLL",
		name = "(Lower) First Timeout",
		description = "(Lower level) Darkens the marker after a vein has been first mined for this long (-1 to disable).",
		position = 9
	)
	@Units(Units.SECONDS)
	@Range(min = -1)
	default int getFirstTimeoutLL()
	{
		return 23;
	}

	@ConfigItem(
		keyName = "secondTimeoutLL",
		name = "(Lower) Second Timeout",
		description = "(Lower level) Darkens the marker again after a vein has been first mined for this long (-1 to disable).",
		position = 10
	)
	@Units(Units.SECONDS)
	@Range(min = -1)
	default int getSecondTimeoutLL()
	{
		return 27;
	}

	@ConfigItem(
		keyName = "respawnTimeout",
		name = "Respawn Timeout",
		description = "Deletes the marker for a vein when it's been this long after first being mined (-1 to disable).",
		position = 11
	)
	@Units(Units.SECONDS)
	@Range(min = -1)
	default int getRespawnTimeout()
	{
		return 120;
	}

	@ConfigItem(
		keyName = "markerTimerMode",
		name = "Marker Timer Mode",
		description = "Shows a timer on the marked tiles.<br>" +
			"Hidden: No timer is shown.<br>" +
			"Timeout: Counts down from the max of the two timeout values.<br>" +
			"Timeout (p): Same as Timeout, but persists when it reaches 0.<br>" +
			"Counter: Counts up from when a vein is first mined.",
		position = 12
	)
	default MarkerTimerMode getMarkerTimerMode()
	{
		return MarkerTimerMode.Off;
	}

	@ConfigItem(
		keyName = "showMarkerTimerDecimal",
		name = "Show Marker Timer Decimal",
		description = "Shows the tenth of seconds decimal on the marker timers.",
		position = 13
	)
	default boolean showMarkerTimerDecimal()
	{
		return true;
	}

	@ConfigItem(
		keyName = "markerTimerOutline",
		name = "Show Marker Timer Outline",
		description = "Show an outline around the text of the marker timers.",
		position = 14
	)
	default boolean showMarkerTimerOutline()
	{
		return true;
	}

	@ConfigItem(
		keyName = "markerTimerOffset",
		name = "Marker Timer Offset",
		description = "Adjust the height offset of the marker timers.",
		position = 15
	)
	@Range(min = -500, max = 500)
	default int getMarkerTimerOffset()
	{
		return 0;
	}

	@ConfigItem(
		keyName = HIGHER_RENDER_PRIORITY_KEY_NAME,
		name = "Render Above Motherlode Icons",
		description = "Gives a higher rendering priority to the markers and timers,<br>" +
			"allowing them to appear above the Motherlode plugin mining icons.",
		position = 16
	)
	default boolean higherRenderPriority()
	{
		return false;
	}
}
