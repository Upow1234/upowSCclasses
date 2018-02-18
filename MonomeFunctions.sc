//if you put a return caret on an inner function you will hop out of a function before you mean to
MonomeFunctions {
	var selCol, arcName, paramsName, leftIndex, rightIndex;
	var grid, gridWidth, gridHeight, gridLeds, arcStateLeds;

	*new { arg selColTemp, arcNameTemp, paramsNameTemp,
		gridTemp, gridWidthTemp, gridHeightTemp;
		^super.new.init(selColTemp, arcNameTemp, paramsNameTemp,
			gridTemp, gridWidthTemp, gridHeightTemp);
	}

	init { arg selColTemp, arcNameTemp,
		gridTemp, gridWidthTemp, gridHeightTemp;

		selCol = selColTemp;
		arcName = arcNameTemp;

		leftIndex = 0;
		rightIndex = 1;

		//from GridFunctions
		grid = gridTemp;
		gridWidth = gridWidthTemp;
		gridHeight = gridHeightTemp;

		gridLeds = Array.fill(gridWidth * gridHeight, 0);
		arcStateLeds = [[selCol[0], 0], [selCol[3], 0]];
	}

	selCol {arg index;
		^selCol[index];
	}

	setIndex { arg x, y;
		var xTemp = x - selCol[0];
		var index = {arg offset, y;
			var result = offset + (y * 2);
			if(result < paramsName.size, {
				result;
			}, {
				^postln("That's Out Of Bounds!!!");
			});
		};
		switch(xTemp, 0, {
			leftIndex = index.value(0, y);
			this.arcSelGridLeds(x, y, 0);
			postln("Arc Selection Left =" + leftIndex);
			^leftIndex;
		}, 1, {
			rightIndex = index.value(0, y);
			this.arcSelGridLeds(x, y, 1);
			postln("Arc Selection Right =" + rightIndex);
			^rightIndex;
		}, 2, {
			leftIndex = index.value(1, y);
			this.arcSelGridLeds(x, y, 0);
			postln("Arc Selection Left =" + leftIndex);
			^leftIndex;
		}, 3, {
			rightIndex = index.value(1, y);
			this.arcSelGridLeds(x, y, 1);
			postln("Arc Selection Right =" + rightIndex);
			^rightIndex;
		});
	}

	encArray {arg encoderNumber, ledValue, fineLedLevel;
		var ledLevel = 15;
		var encoderTemporaryArray = Array.newClear(64);
		for(0, (ledValue - 1).thresh(0), {arg i;
			encoderTemporaryArray[i] = ledLevel;
		});
		encoderTemporaryArray[ledValue] = fineLedLevel;
		for(ledValue + 1, 63, {arg i;
			encoderTemporaryArray[i] = 0;
		});
		^encoderTemporaryArray;
	}

	paramChange {arg enc, delta;
		var arcFunctions = {arg name, messageOne, messageTwo;
			var encArray = { arg encoderNumber, ledValue, fineLedLevel;
				var ledLevel = 15;
				var encoderTemporaryArray = Array.newClear(64);
				for(0, (ledValue - 1).thresh(0), {arg i;
					encoderTemporaryArray[i] = ledLevel;
				});
				encoderTemporaryArray[ledValue] = fineLedLevel;
				for(ledValue + 1, 63, {arg i;
					encoderTemporaryArray[i] = 0;
				});
				encoderTemporaryArray;
			};
			var encArrayTemp;
			name.change(messageTwo);
			encArrayTemp = encArray.value(messageOne, name.arcLedValue, name.arcLedValueFine);
			arcName.ringmap(messageOne, encArrayTemp);
		};

		switch(enc, 0, {
			arcFunctions.value(paramsName[leftIndex][0], enc, delta);
		}, 1, {
			arcFunctions.value(paramsName[leftIndex][1], enc, delta);
		}, 2, {
			arcFunctions.value(paramsName[rightIndex][0], enc, delta);
		}, 3, {
			arcFunctions.value(paramsName[rightIndex][1], enc, delta);
		});
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
		var xTemp = x - selCol[0];
		var clear = {
			for(0, 7, {arg y;
				for(selCol[0], selCol[3], {arg x;
					gridLeds[x + (y * gridWidth)] = 0;
				});
			});
		};
		var leds = {
			gridLeds[arcStateLeds[0][0] + (arcStateLeds[0][1] * gridWidth)] = 15;
			gridLeds[(arcStateLeds[0][0] + 1) + (arcStateLeds[0][1] * gridWidth)] = 15;
			gridLeds[arcStateLeds[1][0] + (arcStateLeds[1][1] * gridWidth)] = 7;
			gridLeds[(arcStateLeds[1][0] - 1) + (arcStateLeds[1][1] * gridWidth)] = 7;
			this.gridLedUpdate;
		};
		switch(side, 0, {
			arcStateLeds[0] = [selCol[xTemp], y];
			this.paramChange(0, 0);
			this.paramChange(1, 0);
			clear.value();
			leds.value();
		}, 1, {
			arcStateLeds[1] = [selCol[xTemp], y];
			this.paramChange(2, 0);
			this.paramChange(3, 0);
			clear.value();
			leds.value();
		});
	}

	gridLedSet {arg x, y, ledLevel;
		gridLeds[x + (y * gridWidth)] = ledLevel;
	}

	monomeStart {arg paramsNameTemp;
		paramsName = paramsNameTemp;
		//initialize grid
		gridLeds[arcStateLeds[0][0] + (arcStateLeds[0][1] * gridWidth)] = 15;
		gridLeds[(arcStateLeds[0][0] + 1) + (arcStateLeds[0][1] * gridWidth)] = 15;
		gridLeds[arcStateLeds[1][0] + (arcStateLeds[1][1] * gridWidth)] = 7;
		gridLeds[(arcStateLeds[1][0] - 1) + (arcStateLeds[1][1] * gridWidth)] = 7;
		this.gridLedUpdate;

		//initialize arc
		this.paramChange(0, 0);
		this.paramChange(1, 0);
		this.paramChange(2, 0);
		this.paramChange(3, 0);
	}

}

