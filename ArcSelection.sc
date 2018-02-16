//if you put a return caret on an inner function you will hop out of a function before you mean to
ArcSelection {
	var selCol, arcName, paramsName, gridFunctions, leftIndex, rightIndex;

	*new { arg selColTemp, arcNameTemp, paramsNameTemp, gridFunctionsTemp;
		^super.new.init(selColTemp, arcNameTemp, paramsNameTemp, gridFunctionsTemp);
	}

	init { arg selColTemp, arcNameTemp, paramsNameTemp, gridFunctionsTemp;
		selCol = selColTemp;
		arcName = arcNameTemp;
		paramsName = paramsNameTemp;
		gridFunctions = gridFunctionsTemp;

		leftIndex = 0;
		rightIndex = 1;
	}

	selCol {arg index;
		^selCol[index];
	}

	arcSelLeft {
		^leftIndex;
	}

	arcSelRight {
		^rightIndex;
	}

	gridFuncs {
		^gridFunctions;
	}

	//DELETE UNUSED BULLSHIT METHODS

	setIndex { arg x, y;
		var tempx = x - selCol[0];
		var index = {arg offset, y;
			var result = offset + (y * 2);
			if(result < paramsName.size, {
				result;
			}, {
				^postln("That's Out Of Bounds!!!");
			});
		};

		switch(tempx, 0, {
			leftIndex = index.value(0, y);
			postln("Arc Selection Left =" + leftIndex);
			^leftIndex;
		}, 1, {
			rightIndex = index.value(0, y);
			postln("Arc Selection Right =" + rightIndex);
			^rightIndex;
		}, 2, {
			leftIndex = index.value(1, y);
			postln("Arc Selection Left =" + leftIndex);
			^leftIndex;
		}, 3, {
			rightIndex = index.value(1, y);
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

	arcSelGridLeds {
		//need to substitute all appropriate class variables here!!!

		~arcSelGridLeds = {arg row, col, side; //side = 0 for left encoders, side = 1 for right encoders
			var clear = {
				for(0, 7, {arg y;
					for(~selrow[0], ~selrow[3], {arg x;
						~gridLeds[x + (y * ~gridWidth)] = 0;
					});
				});
			};
			var leds = {
				~gridLeds[~arcLedsSaved[0][0] + (~arcLedsSaved[0][1] * ~gridWidth)] = 15;
				~gridLeds[(~arcLedsSaved[0][0] + 1) + (~arcLedsSaved[0][1] * ~gridWidth)] = 15;
				~gridLeds[~arcLedsSaved[1][0] + (~arcLedsSaved[1][1] * ~gridWidth)] = 7;
				~gridLeds[(~arcLedsSaved[1][0] - 1) + (~arcLedsSaved[1][1] * ~gridWidth)] = 7;
				~updateGridLeds.value;
			};
			switch(side, 0, {
				~arcLedsSaved[0] = [~selrow[row], col];
				clear.value();
				leds.value();
			}, 1, {
				~arcLedsSaved[1] = [~selrow[row], col];
				clear.value();
				leds.value();
			});
		};
	}


}

/*
var gridWidth = 8;
var selrow = [4, 5, 6, 7];
var arcSelLeds = {arg row, col, side; //side = 0 for left encoders, side = 1 for right encoders
var clear = {
for(0, 7, {arg y;
for(selrow[0], selrow[3], {arg x;
grid64Leds[x + (y * gridWidth)] = 0;
});
});
};
var leds = {
grid64Leds[arcLedsSaved[0][0] + (arcLedsSaved[0][1] * gridWidth)] = 15;
grid64Leds[(arcLedsSaved[0][0] + 1) + (arcLedsSaved[0][1] * gridWidth)] = 15;
grid64Leds[arcLedsSaved[1][0] + (arcLedsSaved[1][1] * gridWidth)] = 7;
grid64Leds[(arcLedsSaved[1][0] - 1) + (arcLedsSaved[1][1] * gridWidth)] = 7;
updateGrid64Leds.value;
};
switch(side, 0, {
arcLedsSaved[0] = [selrow[row], col];
clear.value();
leds.value();
}, 1, {
arcLedsSaved[1] = [selrow[row], col];
clear.value();
leds.value();
});
};



var leftenc = {arg messageTwo, messageThree, row;
var pairBound = {arg offset; // check to make sure there is a parameter to go to
var index = offset + (messageTwo * 2);
if(index < pairs.size, {
arcSelectionLeft[1] = index;
//postln("arc selection left =" + arcSelectionLeft[1]);
updateArcLeds.value(0, pairs[arcSelectionLeft[1]][0].arcLedValue);
postln("");
pairs[arcSelectionLeft[1]][0].displayName;
updateArcLeds.value(1, pairs[arcSelectionLeft[1]][1].arcLedValue);
pairs[arcSelectionLeft[1]][1].displayName;
//postln("arc selection left is held");
arcSelLeds.value(row, messageTwo, 0);
});
};
switch(messageThree, 1, {
if(arcSelectionLeft[0] == 0, {
arcSelectionLeft[0] = 1;
switch(row, 0, {
pairBound.value(0);
}, 2, {
pairBound.value(1);
});
});
}, 0, {
if(arcSelectionLeft[0] == 1, {
arcSelectionLeft[0] = 0;
//postln("arc selection left is not held");
});
});
};



var rightenc = {arg messageTwo, messageThree, row;
var pairBound = {arg offset; // check to make sure there is a parameter to go to
var index = offset + (messageTwo * 2);
if(index < pairs.size, {
arcSelectionRight[1] = index;
//postln("arc selection right =" + arcSelectionRight[1]);
updateArcLeds.value(2, pairs[arcSelectionRight[1]][0].arcLedValue);
postln("");
pairs[arcSelectionRight[1]][0].displayName;
updateArcLeds.value(3, pairs[arcSelectionRight[1]][1].arcLedValue);
pairs[arcSelectionRight[1]][1].displayName;
//postln("arc selection right is held");
arcSelLeds.value(row, messageTwo, 1);
});
};
switch(messageThree, 1, {
if(arcSelectionRight[0] == 0, {
arcSelectionRight[0] = 1;
switch(row, 1, {
pairBound.value(0);
}, 3, {
pairBound.value(1);
});
});
}, 0, {
if(arcSelectionRight[0] == 1, {
arcSelectionRight[0] = 0;
//postln("arc selection right is not held");
});
});
};

var updateGrid64Leds = {
grid64.levmap(0, 0, grid64Leds);
};
var updateArcLeds = {arg encoderNumber, ledValue, ledLevel = 15;
var encoderTemporaryArray = Array.fill(64, { arg index;
if(index <= ledValue, { ledLevel }, { 0 });
});
arc.ringmap(encoderNumber, encoderTemporaryArray);

};

var arcSelectionLeft = [0, 0]; //left is holding status (one is held 0 is not) right is selection index
var arcSelectionRight = [0, 1];
var arcLedsSaved = [[4, 1], [7, 1]]; //this is misleading because it's grid leds not arc leds

OSCdef(\cvSeqArc, //arc
{ arg message, time, addr, recvPort;
var arcFunctions = {arg name, messageOne, messageTwo, ledLevel = 15;
name.change(messageTwo);
name.sendChange;
updateArcLeds.value(messageOne, name.arcLedValue, ledLevel);
};
switch(message[1], 0, {
arcFunctions.value(pairs[arcSelectionLeft[1]][0], 0, message[2]);
}, 1, {
arcFunctions.value(pairs[arcSelectionLeft[1]][1], 1, message[2]);
}, 2, {
arcFunctions.value(pairs[arcSelectionRight[1]][0], 2, message[2]);
}, 3, {
arcFunctions.value(pairs[arcSelectionRight[1]][1], 3, message[2]);
});
}, "/monome/enc/delta");
*/
