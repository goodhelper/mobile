import flash.display.Sprite;
import flash.display.Shape;
import flash.events.MouseEvent;
import flash.desktop.NativeApplication;
import flash.desktop.SystemIdleMode;
 
// Keep the phone from sleeping or dimming if left idle. Should not be used if the app might be started and forgotten about by the user. Also need to set permissions shown above.
NativeApplication.nativeApplication.systemIdleMode = SystemIdleMode.KEEP_AWAKE;
 
// r is the radius of the "angle" drawn as a circular section
var r:Number = 100;
 
// The isLocked flag indicates when the user has tapped the screen to lock the value so it can be easily read. Tapping the screen toggles the flag between true and false.
var isLocked:Boolean = false;
 
// spHolder is the "mother sprite" to which we will attach all of our display objects. This makes it easy to reposition things as desired.
var spHolder:Sprite = new Sprite();
spHolder.x = 240;
spHolder.y = 700;
addChild(spHolder);
 
// Place the stage assets relative to spHolder and add them as children of spHolder.
mcRay.x = 0;
mcRay.y = 0;
mcHoriz.x = 0;
mcHoriz.y = 0;
 
// This Shape object gives the visual representation of the angle.
var shAngle:Shape = new Shape();
spHolder.addChild(shAngle);
 
spHolder.addChild(mcHoriz);
spHolder.addChild(mcRay);
 
var accl:Accelerometer = new Accelerometer();
var isSupported:Boolean = Accelerometer.isSupported;
 
// We check for accelerometer result so we don't listen for things that cannot happen.
checksupport();
 
function checksupport():void {
if (isSupported) {
txtMsg.text = "Accelerometer supported";
accl.addEventListener(AccelerometerEvent.UPDATE, updateHandler);
} else {
txtMsg.text = "Accelerometer not supported";
}
}
 
function updateHandler(evt:AccelerometerEvent):void {
// Don't bother doing anything if the screen is "locked."
if (isLocked) return;
 
var aX:Number = evt.accelerationX;
var aY:Number = evt.accelerationY;
var aZ:Number = evt.accelerationZ;
 
// These numbers can creep outside of the interval -1 to 1 if the phone is even moving very slightly, so we use the following lines to keep the values between -1 and 1.
if (aX < -1) aX = -1;
if (aX > 1) aX = 1;
if (aY < -1) aY = -1;
if (aY > 1) aY = 1;
if (aZ < -1) aZ = -1;
if (aZ > 1) aZ = 1;
 
/* 
We calculate the angle by using the vectory identity u.v = |u| |v| cos(angle), where u is the vector (aX,aY,aZ) and v is the vector (0,aY,0) which points vertically. We have to subract 90 because arccos essentially returns values between 0 and 180, and we would like to interpret these between -90 and 90.
*/
var mag:Number = Math.sqrt(aX*aX+aY*aY+aZ*aZ);
var angle:Number = Math.round((180/Math.PI)*Math.acos(aY/mag)) - 90;
 
// We do not allow the angle to get outside of the range -90 to 90.
if (angle < -90) angle = -90;
if (angle > 90) angle = 90;
 
// Rotate mcRay and draw the corresponding angle on shAngle.
mcRay.rotation = angle;
drawAngle(angle);
 
// Update output
txtMsg.text = "ANGLE = " + Math.abs(angle).toFixed(0) + String.fromCharCode(186) ;
}
 
// Test to see if a number is effectively 0.
function isZero(n:Number):Boolean {
if (Math.abs(n) < 0.05) {
return true;
}
return false;
}
}
// Draw the circular segment that denotes the angle between the two rays. This is drawn as a filled polygon with radius r and each small side corresponding to 1 degree
function drawAngle(t:Number):void {
var i:Number;
shAngle.graphics.clear();
shAngle.graphics.lineStyle(3,0xFFFFFF);
shAngle.graphics.beginFill(0xFFFFFF,0.5);
shAngle.graphics.moveTo(0,0);
if (t > 0) {
for (i=1; i<t; i++) {
shAngle.graphics.lineTo(-r*Math.cos(i*Math.PI/180 + Math.PI/2), r*Math.sin(i*Math.PI/180 + Math.PI/2));
}
}
if (t < 0) {
for (i=1; i<(-t); i++) {
shAngle.graphics.lineTo(r*Math.cos(i*Math.PI/180+Math.PI/2), r*Math.sin(i*Math.PI/180+Math.PI/2));
}
}
shAngle.graphics.lineTo(0,0);
shAngle.graphics.endFill();
}
 
// Tapping the screen toggles the flag between true and false. The screen displays a message when locked.
stage.addEventListener(MouseEvent.CLICK, toggleLock);
 
function toggleLock(me:MouseEvent):void {
isLocked = !isLocked ;
if (isLocked) txtMsg.appendText("\nLOCKED");
}
 
// Handle the pressing of the "Back" button to exit the app
stage.addEventListener(KeyboardEvent.KEY_UP, fl_OptionsMenuHandler);
 
function fl_OptionsMenuHandler(e:KeyboardEvent):void {
if(e.keyCode == Keyboard.BACK)	{
NativeApplication.nativeApplication.exit(0);
}
}
