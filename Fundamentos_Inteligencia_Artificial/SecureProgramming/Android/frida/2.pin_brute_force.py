import frida, sys

package_name = "infosecadventures.allsafe"

# For non-static classes
jscodeNonStatic = """
Java.perform(function() {
 console.log("[ * ] Starting PIN Brute-force, please wait...");
 

 Java.choose("infosecadventures.allsafe.challenges.PinBypass", {
  onMatch: function(instance) {
   console.log("[ * ] Instance found in memory: " + instance);
   for(var i = 1000; i < 9999; i++){
    if(instance.checkPin(i + "") == true){
     console.log("[ + ] Found correct PIN: " + i);
    }
   }
  },

  onComplete: function() { 
   console.log("[ + ] END ");
   }
 });

});
"""


# For static classes
jscodeStatic = """
Java.perform(function () {
    console.log("[ * ] Starting PIN Brute-force, please wait...")
    
    var PinUtil = Java.use("infosecadventures.allsafe.challenges.PinBypass");
 
    for(var i=1000; i < 9999; i++)
    {
        if(PinUtil.checkPin(i+"") == true){
            console.log("[ + ] Found correct PIN: " + i);
        }
    }
});
"""




try:
    timeout = 10
    print("[ * ] Looking for app: " + package_name)
    device = frida.get_usb_device(timeout)
    print("[ * ] Launching app...")
    session = device.attach('allsafe')
    exploit = session.create_script(jscodeNonStatic)
    print("[ + ] App launched. Loading exploit...")
    exploit.load()
    sys.stdin.read()
except frida.ServerNotRunningError:
    print("[ - ] Frida server is not running! Exiting...")
except frida.NotSupportedError:
    print("[ - ] Unable to find application. Please, install it first!")
except frida.ProcessNotFoundError:
    print("[ - ] Unable to find process. Launch the app and try again!")
except KeyboardInterrupt:
    print("\n[ - ] Interrupted. Exiting...")



  


