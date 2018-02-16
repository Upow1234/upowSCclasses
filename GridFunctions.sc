GridFunctions {
	var grid, gridWidth, gridHeight, arcSelection, gridLeds, arcStateLeds;

	*new { arg gridTemp, gridWidthTemp, gridHeightTemp, arcSelectionTemp;
		^super.new.init(gridTemp, gridWidthTemp, gridHeightTemp, arcSelectionTemp);
	}

	init { arg gridTemp, gridWidthTemp, gridHeightTemp, arcSelectionTemp;
		grid = gridTemp;
		gridWidth = gridWidthTemp;
		gridHeight = gridHeightTemp;
		arcSelection = arcSelectionTemp;

		gridLeds = Array.fill(gridWidth * gridHeight, 0);
		//arcStateLeds = [[arcSelection.selCol(0), 0], [arcSelection.selCol(0), 0]];
	}

	gridWidth {
		^gridWidth;
	}

	gridLedUpdate {
		switch(gridWidth, 8, {
			grid.levmap(0, 0, gridLeds);
		}, 16, {
			var tempGridLedsLeft, tempGridLedsRight;
			tempGridLedsLeft = Array.fill(64, 0);
			tempGridLedsRight = Array.fill(64, 0);
			for(0, 7, { arg y;
				for(0, 15, { arg x;
					var index16 = x + (y * gridWidth);
					var index8 = x + (y * 8);
					if(x < 8, {
						tempGridLedsLeft[index8] = gridLeds[index16];
					}, {
						tempGridLedsRight[index8 - 8] = gridLeds[index16];
					});
				});
			});
			grid.levmap(0, 0, tempGridLedsLeft);
			grid.levmap(8, 0, tempGridLedsRight);
		});
	}

	arcSelGridLeds { arg x, y, side; //side = 0 for left encoders, side = 1 for right encoders
		/*
		var clear = {
			for(0, 7, {arg y;
				for(arcSelection.selCol(0), arcSelection.selCol(3), {arg x;
					gridLeds[x + (y * gridWidth)] = 0;
				});
			});
		};
		var leds = {
			gridLeds[arcStateLeds[0][0] + (arcStateLeds[0][1] * gridWidth)] = 15;
			gridLeds[(arcStateLeds[0][0] + 1) + (arcStateLeds[0][1] * gridWidth)] = 15;
			gridLeds[arcStateLeds[1][0] + (arcStateLeds[1][1] * gridWidth)] = 7;
			gridLeds[(arcStateLeds[1][0] - 1) + (arcStateLeds[1][1] * gridWidth)] = 7;
			this.updateGridLeds;
		};
		switch(side, 0, {
			arcStateLeds[0] = [arcSelection.selCol(0), y];
			clear.value();
			leds.value();
		}, 1, {
			arcStateLeds[1] = [arcSelection.selCol(0), y];
			clear.value();
			leds.value();
		});
		*/
	}
}