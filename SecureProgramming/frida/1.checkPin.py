import frida, sys
jscode = """
Java.perform(function() {
 console.log("[ * ] Starting implementation override...")
 var MainActivity = Java.use("infosecadventures.allsafe.challenges.PinBypass");
 MainActivity.checkPin.implementation = function(pin){
     console.log("[ + ] PIN check successfully bypassed!")
     return true;
 }
});
"""

process = frida.get_usb_device().attach('allsafe')
script = process.create_script(jscode)
print('[ * ] Running Frida Demo application')
script.load()
sys.stdin.read()


