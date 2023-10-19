import frida
import sys

package_name = "infosecadventures.allsafe"

script = """
Java.perform(function() {
    console.log("[ * ] Starting implementation override...")
    var PinUtil = Java.use("infosecadventures.allsafe.challenges.PinBypass");
    PinUtil.checkPin.implementation = function(pin) {
        console.log("[ + ] PIN check successfully bypassed!")
        return true;
    }
});
"""


try:
    timeout = 10
    print("[ * ] Looking for app: " + package_name)
    device = frida.get_usb_device(timeout)
    print("[ * ] Launching app...")
    pid = device.spawn([package_name])
    device.resume(pid)
    session = device.attach(pid)
    exploit = session.create_script(script)
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